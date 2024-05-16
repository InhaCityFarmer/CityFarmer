package com.CapstoneDesign.cityfarmer.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.CapstoneDesign.cityfarmer.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    //파이어베이스 권한 생성
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //ID와 PW를 EditText에서 받아오기
        val textLoginID = findViewById<EditText>(R.id.textLoginID)
        val textLoginPW = findViewById<EditText>(R.id.textLoginPW)


        //파이어베이스 권한 받아오기
        auth = Firebase.auth
        //로그인 버튼
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        //로그인 버튼 클릭시 setOnClickListener 실행
        btnLogin.setOnClickListener {
            //textLoginID과 textLoginPW로 로그인 시도
            auth.signInWithEmailAndPassword(textLoginID.text.toString(), textLoginPW.text.toString())
                .addOnCompleteListener(this) {task->
                    //로그인 성공
                    if (task.isSuccessful) {
                        Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                        //로그인 성공시 MainActivity로 intent
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                    //로그인 실패
                    else {
                        Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        //RegisterActivity로 이동하는 버튼
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        //setOnClickListner 사용해서 버튼 누르면 intent
        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

}
