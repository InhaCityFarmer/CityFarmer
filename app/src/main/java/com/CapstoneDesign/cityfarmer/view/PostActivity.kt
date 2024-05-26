package com.CapstoneDesign.cityfarmer.view

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.CapstoneDesign.cityfarmer.R
import com.CapstoneDesign.cityfarmer.`object`.Farm
import com.CapstoneDesign.cityfarmer.`object`.Post
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.toObject

class PostActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var myFarm : String
    private lateinit var myName : String
    private lateinit var myUID : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_post)
        //파이어베이스 권한 생성 및 받아오기
        auth = Firebase.auth
        //파이어스토어 DB 접근 객체 얻어오기
        db = FirebaseFirestore.getInstance()
        //내 객체 정보 가져오기
        db.collection("User").document(auth.currentUser!!.uid.toString()).get().addOnSuccessListener {
            document ->
           if(document.exists())
           {
               myFarm = document.get("farmName").toString()
               myName = document.get("name").toString()
               myUID = document.get("uid").toString()
                println(myFarm)
               println(myName)
               println(myUID)
           }
        }
        //제목
        val editTextTextPostTitle = findViewById<EditText>(R.id.editTextPostTitle)
        //본문
        val exitTextTextPostBody = findViewById<EditText>(R.id.editTextPostBody)
        //글쓰기 버튼
        val btnPost = findViewById<Button>(R.id.btnPost)
        //글쓰기 버튼 누르면
        btnPost.setOnClickListener {
            //내 농장에 접근해서 기존에 있는 post가져오고 거기에 내가 방금 작성한 post 추가해서 다시 update해줌
            db.collection("Farm").get().addOnSuccessListener {
                documents ->
                if(!documents.isEmpty)
                {
                    for(document in documents)
                    {
                        var farm = document.toObject(Farm::class.java)//Farm 객체 타입으로 임시 저장
                        if(farm.name == myFarm)
                        {
                            var postArray = farm.post
                            var myPost : Post = Post(writer = myName, writerUID = myUID, title = editTextTextPostTitle.text.toString(), body = exitTextTextPostBody.text.toString() )
                            postArray.add(myPost)
                            farm.post = postArray
                            db.collection("Farm").document(myFarm).set(farm)
                            break
                        }
                    }
                    finish()
                }
            }
        }
    }
}

