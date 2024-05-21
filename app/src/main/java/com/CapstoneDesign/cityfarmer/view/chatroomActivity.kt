package com.CapstoneDesign.cityfarmer.view

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore

class chatroomActivity : AppCompatActivity() {
    private lateinit var opponentUserUid : String
    private lateinit var currentUserUid : String
    private lateinit var currentUserName : String
    private lateinit var opponentUserName : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chatroom)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //currentUserName = "황윤준"
        //opponentUserUid = "황윤준2"
        currentUserName = intent.getStringExtra("currentUserName").toString()
        opponentUserUid = intent.getStringExtra("opponentUserUid").toString()
        currentUserUid = intent.getStringExtra("currentUserUid").toString()
      //  Log.d("내 Uid : ",currentUserUid)
      //  Log.d("상대 uid : ",opponentUserUid)



        val chatList = mutableListOf<String>()

        val db = Firebase.firestore
        val userCollection = db.collection("User")

        val uid = currentUserUid // 사용자의 실제 UID 값으로 대체

        val documentRef = userCollection.document(uid)
        val documentRef2 = userCollection.document(opponentUserUid)

        documentRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // 문서가 존재하는 경우 prev_chatroom 필드의 값을 읽어옴
                    val prevChatroom = document.get("prev_chat") as? Map<String, String>
                    if (prevChatroom != null && prevChatroom.containsKey(opponentUserUid)) {

                        val value = prevChatroom[opponentUserUid] // key가 opponent_uid인 값
                        // 여기서 그럼 value가 cid가 됨, 이 cid를 기반으로 chatroom을 감
                        // 일단 지금 코드에서는 chatroom_1이 나와야 함
                        // chatroom에서 data를 불러와서 recyclerview에 넣고 봄
                        // chatList에다가 넣음

                        val chatroomRef = db.collection("Chat").document(value!!)
                        chatroomRef.get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    val chatlog = document.get("message") as? List<String>
                                    if (chatlog != null) {
                                        for (message in chatlog) {
                                            Log.d("Chatlog", message)
                                            chatList.add(message.toString())
                                        }

                                        val sendButton = findViewById<Button>(R.id.btnSendMessage)
                                        sendButton.setOnClickListener {
                                            val messageText = findViewById<EditText>(R.id.sendingMsg).text.toString()

                                            if (messageText.isNotEmpty()) {
                                                val newMessage = currentUserName + " : " + messageText
                                                // Firestore에 새로운 메시지를 추가
                                                val chatroomRef = db.collection("Chat").document(value!!) // value는 chatroom의 CID
                                                chatroomRef.update("message", FieldValue.arrayUnion(newMessage))
                                                    .addOnSuccessListener {
                                                        // 성공적으로 메시지가 추가된 경우
                                                        Log.d("메시지 송신 완료", "Message sent successfully: $messageText")
                                                        chatList.add(newMessage)

                                                        // EditText를 초기화합니다.
                                                        findViewById<EditText>(R.id.sendingMsg).setText("")
                                                    }
                                                    .addOnFailureListener { e ->
                                                        // 메시지 추가 중 오류가 발생한 경우
                                                        Log.w("메시지 송신 오류", "Error sending message", e)
                                                    }
                                            } else {
                                                // 메시지가 비어있는 경우
                                                Log.d("sendMessage", "Message is empty")
                                                Toast.makeText(this, "문자를 입력하세요!!", Toast.LENGTH_SHORT).show()
                                            }
                                        }

                                        val rv = findViewById<RecyclerView>(R.id.chatting_log)
                                        val rv_adapter = chatAdapter(chatList)


                                        rv.adapter = rv_adapter
                                        rv.layoutManager = LinearLayoutManager(this)


                                        val chatroomRef = db.collection("Chat").document(value!!)
                                        chatroomRef.addSnapshotListener { snapshot, e ->
                                            if (e != null) {
                                                Log.w("Chatlog", "Listen failed", e)
                                                return@addSnapshotListener
                                            }

                                            if (snapshot != null && snapshot.exists()) {
                                                val chatlog = snapshot.get("message") as? List<String>
                                                if (chatlog != null) {
                                                    chatList.clear() // 이전 데이터를 지우고 새로운 데이터로 채웁니다.
                                                    for (message in chatlog) {
                                                        Log.d("Chatlog", message)
                                                        chatList.add(message.toString())
                                                    }
                                                    // RecyclerView에 데이터가 변경되었음을 알립니다.
                                                    rv_adapter.notifyDataSetChanged()
                                                } else {
                                                    Log.d("Chatlog", "Chatlog field is null or not a list")
                                                }
                                            } else {
                                                Log.d("Chatlog", "Current data: null")
                                            }
                                        }


                                    }
                                    else {
                                        Log.d("Chatlog", "Chatlog field is null or not a list")
                                    }
                                }

                                else {
                                    Log.d("Chatlog", "Document does not exist")
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.d("Chatlog", "Error getting document", exception)
                            }


                    }


                    else {
                        val newValue = opponentUserUid // 새로운 값을 추가
                        val chatCollection = db.collection("Chat")
                        val newDocumentRef = chatCollection.document()

                        val data = hashMapOf(
                            "message" to arrayListOf<String>()
                        )
                        newDocumentRef.set(data)
                            .addOnSuccessListener { documentReference ->
                                Log.d("업데이트 성공","나이스")
                            }
                            .addOnFailureListener { e ->
                                Log.d("업데이트 실패","아오")
                            }

                        Log.d("newdocumentREF : ", newDocumentRef.id.toString())

                        // Chat collection에 새로운 값 추가
                        // 그리고 유져 collection에는 두번추가해야함

                        documentRef.get()
                            .addOnSuccessListener { documentSnapshot ->
                                if (documentSnapshot.exists()) {
                                    // documentSnapshot에서 prev_chat 필드 가져오기
                                    val prevChatMap = documentSnapshot.get("prev_chat") as? Map<String, String> ?: mutableMapOf()
                                    // prev_chat 값을 저장
                                    if (prevChatMap != null) {
                                        val newMap = prevChatMap?.toMutableMap() ?: mutableMapOf() // 기존 맵을 변경 가능한 맵으로 변환하고 아오 없으면 빈 맵을 생성.
                                        newMap?.put(opponentUserUid.toString(), newDocumentRef.id.toString()) // 새로운 요소를 추가합
                                        documentRef.update("prev_chat", newMap)
                                            .addOnSuccessListener {
                                                Log.d("TAG", "prev_chat field created11")
                                            }
                                            .addOnFailureListener { e ->
                                                Log.w("TAG", "Error updating document", e)
                                            }
                                    }
                                }
                                else {
                                    val newPrevChatMap = hashMapOf<String, String>()
                                    // 여기서 값 추가
                                    newPrevChatMap[opponentUserUid.toString()] =
                                        newDocumentRef.id.toString()

                                    documentRef.update("prev_chat", newPrevChatMap)
                                        .addOnSuccessListener {
                                            Log.d("TAG", "prev_chat field created22")
                                        }
                                        .addOnFailureListener { e ->
                                            Log.w("TAG", "Error updating document", e)
                                        }
                                }

                            }
                            .addOnFailureListener { e ->
                                Log.w("TAG", "Error getting document", e)
                            }

                        documentRef2.get()
                            .addOnSuccessListener { documentSnapshot ->
                                if (documentSnapshot.exists()) {
                                    // documentSnapshot에서 prev_chat 필드 가져오기
                                    val prevChatMap = documentSnapshot.get("prev_chat") as? Map<String, String> ?: mutableMapOf()
                                    Log.d("아니 니는 null이 아니잖아",prevChatMap.toString())
                                    // prev_chat 값을 저장
                                    if (prevChatMap != null) {
                                        val newMap = prevChatMap?.toMutableMap() ?: mutableMapOf() // 기존 맵을 변경 가능한 맵으로 변환하고 아오 없으면 빈 맵을 생성.
                                        newMap?.put(currentUserUid.toString(), newDocumentRef.id.toString()) // 새로운 요소를 추가합

                                        Log.d("아니 니는 null이 아니잖아",newMap.toString())


                                        documentRef2.update("prev_chat", newMap)
                                            .addOnSuccessListener {
                                                Log.d("TAG", "prev_chat field created33")
                                            }
                                            .addOnFailureListener { e ->
                                                Log.w("TAG", "Error updating document", e)
                                            }
                                    }
                                }
                                else {
                                    val newPrevChatMap = hashMapOf<String, String>()
                                    // 여기서 값 추가
                                    newPrevChatMap[currentUserUid.toString()] =
                                        newDocumentRef.id.toString()

                                    documentRef2.update("prev_chat", newPrevChatMap)
                                        .addOnSuccessListener {
                                            Log.d("TAG", "prev_chat field created44")
                                        }
                                        .addOnFailureListener { e ->
                                            Log.w("TAG", "Error updating document", e)
                                        }
                                }

                            }
                            .addOnFailureListener { e ->
                                Log.w("TAG", "Error getting document", e)
                            }





                        val value = newDocumentRef.id

                        //Log.d("prev_chatroom이 존재하네요", "Value: $value")
                        // 여기서 그럼 value가 cid가 됨, 이 cid를 기반으로 chatroom을 감
                        // 일단 지금 코드에서는 chatroom_1이 나와야 함
                        // chatroom에서 data를 불러와서 recyclerview에 넣고 봄
                        // chatList에다가 넣음

                        val chatroomRef = db.collection("Chat").document(value!!)
                        chatroomRef.get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    val chatlog = document.get("message") as? List<String>
                                    if (chatlog != null) {
                                        for (message in chatlog) {
                                            Log.d("Chatlog", message)
                                            chatList.add(message.toString())
                                        }

                                        val sendButton = findViewById<Button>(R.id.btnSendMessage)
                                        sendButton.setOnClickListener {
                                            val messageText = findViewById<EditText>(R.id.sendingMsg).text.toString()

                                            if (messageText.isNotEmpty()) {
                                                val newMessage = currentUserName + " : " + messageText
                                                // Firestore에 새로운 메시지를 추가
                                                val chatroomRef = db.collection("Chat").document(value!!) // value는 chatroom의 CID
                                                chatroomRef.update("message", FieldValue.arrayUnion(newMessage))
                                                    .addOnSuccessListener {
                                                        // 성공적으로 메시지가 추가된 경우
                                                        Log.d("메시지 송신 완료", "Message sent successfully: $messageText")
                                                        chatList.add(newMessage)

                                                        // EditText를 초기화합니다.
                                                        findViewById<EditText>(R.id.sendingMsg).setText("")
                                                    }
                                                    .addOnFailureListener { e ->
                                                        // 메시지 추가 중 오류가 발생한 경우
                                                        Log.w("메시지 송신 오류 ", "Error sending message", e)
                                                    }
                                            } else {
                                                // 메시지가 비어있는 경우
                                                Log.d("sendMessage", "Message is empty")
                                                Toast.makeText(this, "문자를 입력하세요!!", Toast.LENGTH_SHORT).show()
                                            }
                                        }

                                        val rv = findViewById<RecyclerView>(R.id.chatting_log)
                                        val rv_adapter = chatAdapter(chatList)


                                        rv.adapter = rv_adapter
                                        rv.layoutManager = LinearLayoutManager(this)



                                        val chatroomRef = db.collection("Chat").document(value!!)
                                        chatroomRef.addSnapshotListener { snapshot, e ->
                                            if (e != null) {
                                                Log.w("Chatlog", "Listen failed", e)
                                                return@addSnapshotListener
                                            }

                                            if (snapshot != null && snapshot.exists()) {
                                                val chatlog = snapshot.get("message") as? List<String>
                                                if (chatlog != null) {
                                                    chatList.clear() // 이전 데이터를 지우고 새로운 데이터로 채웁니다.
                                                    for (message in chatlog) {
                                                        Log.d("Chatlog", message)
                                                        chatList.add(message.toString())
                                                    }
                                                    // RecyclerView에 데이터가 변경되었음을 알립니다.
                                                    rv_adapter.notifyDataSetChanged()
                                                } else {
                                                    Log.d("Chatlog", "Chatlog field is null or not a list")
                                                }
                                            } else {
                                                Log.d("Chatlog", "Current data: null")
                                            }
                                        }


                                    }
                                    else {
                                        Log.d("Chatlog", "Chatlog field is null or not a list")
                                    }
                                }

                                else {
                                    Log.d("Chatlog", "Document does not exist")
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.d("Chatlog", "Error getting document", exception)
                            }

                    }
                }
                else {
                    // 문서가 존재하지 않는 경우
                    Log.d("currentuseruid를 잘못읽어와서 제대로 읽을 수 없음", "Document does not exist")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("prev_chatroom", "Error getting document: ", exception)
            }

    }
}