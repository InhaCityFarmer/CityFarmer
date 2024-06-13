package com.CapstoneDesign.cityfarmer.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.CapstoneDesign.cityfarmer.R
import com.CapstoneDesign.cityfarmer.adapter.chatAdapter
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.firestore

class PrevChatActivity : AppCompatActivity() {
    private lateinit var currnetUserUid : String
    private lateinit var currentUserName : String

    override fun onCreate(savedInstanceState: Bundle?) {
        // currentUserUid를 intent로 main에서 받아옴
        currnetUserUid = intent.getStringExtra("currentUserUid").toString()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_prevchat)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val userList = mutableListOf<String>()
        val userNameList = mutableListOf<String>()
        val db = Firebase.firestore
        val userCollection = db.collection("User")
        val documentRef = userCollection.document(currnetUserUid)

        ////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////
        // 현재 uid를 통해서 이름을 얻는 작업
        documentRef.get()
            .addOnSuccessListener { documentSnapshot->
                if(documentSnapshot.exists()){
                    currentUserName = documentSnapshot.get("name") as String
                    Log.d("이름 데이터 읽어오기 성공", "$currentUserName")
                }
                else{

                }
            }
            .addOnFailureListener { exception ->
                Log.d("이름 데이터 읽어오기 실패", "$exception")

            }
        ////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////

        // 이제부터는 User collection에서 uid를 통해 document에 접근, 그 아래 prevchat에서
        // userlist 받아오고, 그 userlist를 바탕으로 userNamelist에 넣어서 recyclerview에 보여주는 파트
        documentRef
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val prevChatMap = documentSnapshot.get("prev_chat") as? Map<String, String>
                    if (prevChatMap != null) {
                        for ((key, value) in prevChatMap) {
                            // 맵의 각 키값을 userList에 추가
                            // 여기서 key값이 상대방의 uid ( prev_chat 부분이 uid-chatroomid의 pair로 되어있음
                            userList.add(key)
                            ////////////////////////////////////////////////////////////////
                            // 여기는 key를 가지고 Uesr collection에서 key(uid)에 매칭되는 name을 nameList에 추가하는 과정

                            userCollection
                                .whereEqualTo(FieldPath.documentId(), key)
                                .get()
                                .addOnSuccessListener { documents ->
                                    for (document in documents) {
                                        val name = document.getString("name")
                                        if (name != null) {
                                            Log.d("이름", name)
                                            userNameList.add(name)
                                        }
                                        else {
                                            Log.d("name", "name 필드가 없습니다.")
                                        }
                                    }

                                    ////////////////////////////////////////////////////////////////

                                    val rv = findViewById<RecyclerView>(R.id.rvUsers)

                                    val rv_adapter = chatAdapter(userNameList)
                                    rv.adapter = rv_adapter
                                    rv.layoutManager = LinearLayoutManager(this)
                                    rv_adapter.itemclick = object : chatAdapter.Itemclick {
                                        override fun onclick(view: View, position: Int) {
                                            //Toast.makeText(baseContext, "Clicked position: $position", Toast.LENGTH_LONG).show()
                                            val intent = Intent(baseContext, ChatroomActivity::class.java)
                                            intent.putExtra("currentUserUid", currnetUserUid)
                                            intent.putExtra("opponentUserUid", userList[position])
                                            intent.putExtra("opponentUserName", userNameList[position])
                                            intent.putExtra("currentUserName",currentUserName)
                                            startActivity(intent)
                                        }
                                        
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    Log.w("TAG", "Error getting documents: ", exception)
                                }

                        }

                    }

                    else {
                        Log.d("prev_chat 데이터 없음", "0")
                    }
                }
                else {
                    Log.d("문서가 존재하지 않음", "0")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("데이터 읽어오기 실패", "$exception")
            }



    }


}