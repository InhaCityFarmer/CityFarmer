package com.CapstoneDesign.cityfarmer.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.CapstoneDesign.cityfarmer.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    //private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //파이어베이스 권한 생성 및  받아오기
        var auth : FirebaseAuth = Firebase.auth

        Log.d ("내 auth",auth.currentUser?.uid.toString())

        //MapActivity로 이동하는 버튼
        val btnMap = findViewById<Button>(R.id.btnMap)
        btnMap.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }

        //InventoryActivity로 이동하는 버튼
        val btnInventory = findViewById<Button>(R.id.btnInventory)
        btnInventory.setOnClickListener {
            val intent = Intent(this, InventoryActivity::class.java)
            startActivity(intent)
        }

        val btnDetect = findViewById<Button>(R.id.btnDetect)
        btnDetect.setOnClickListener {
            val intent = Intent(this, DetectActivity::class.java)
            startActivity(intent)
        }



        //임시 로그아웃 버튼 연결
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        //로그아웃 버튼 클릭시 로그아웃
        btnLogout.setOnClickListener {
            auth.signOut()
            //로그아웃 이후 엑티비티 종료
            finish()
        }

        val btnChatbot = findViewById<Button>(R.id.btnChatbot)
        btnChatbot.setOnClickListener {
            val intent = Intent(baseContext,chatbotActivity::class.java)
            startActivity(intent)

        }

        val btnChatting = findViewById<Button>(R.id.btnChatting)
        btnChatting.setOnClickListener {
            // 채팅 버튼 클릭시 전의 채팅목록 불러오기
            val intent = Intent(baseContext, prevchatActivity::class.java)
            val user = auth.currentUser
            intent.putExtra("currentUserUid", user!!.uid.toString())

            startActivity(intent)
        }

        val btnDashboard = findViewById<Button>(R.id.btnDashboard)
        btnDashboard.setOnClickListener{

        }

    }
}