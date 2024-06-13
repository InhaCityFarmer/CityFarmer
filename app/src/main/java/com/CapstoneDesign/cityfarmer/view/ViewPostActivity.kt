package com.CapstoneDesign.cityfarmer.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.CapstoneDesign.cityfarmer.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class ViewPostActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var currentUserName : String
    private lateinit var writerUid : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_post)

        //파이어베이스 권한 생성 및 받아오기
        auth = Firebase.auth
        //파이어스토어 DB 접근 객체 얻어오기
        db = FirebaseFirestore.getInstance()

        db.collection("User").document(auth.currentUser!!.uid.toString()).get().addOnSuccessListener {
            document ->
            if(document.exists())
            {
                currentUserName = document.get("name").toString()
            }
        }
        writerUid = intent.getStringExtra("writerUID").toString()


        val editTextViewTitle = findViewById<EditText>(R.id.editTextViewTitle)
        val editTextViewBody = findViewById<EditText>(R.id.editTextViewBody)
        val btnGoChat = findViewById<Button>(R.id.btnGoChat)

        //intent로 가져온 값을 제목과 본문에 넣기
        editTextViewTitle.setText(intent.getStringExtra("title").toString())
        editTextViewBody.setText(intent.getStringExtra("body").toString())
        //제목과 본문 수정 불가능으로 변경
        editTextViewTitle.isEnabled = false
        editTextViewBody.isEnabled = false
        //채팅하기 버튼 누르면 채팅으로 넘겨줌
        btnGoChat.setOnClickListener {
            val intent = Intent(baseContext, ChatroomActivity::class.java)
            intent.putExtra("currentUserName", currentUserName )
            intent.putExtra("currentUserUid", auth.currentUser!!.uid.toString())
            intent.putExtra("opponentUserName", intent.getStringExtra("writer"))
            intent.putExtra("opponentUserUid", writerUid)
            startActivity(intent)
        }
    }
}