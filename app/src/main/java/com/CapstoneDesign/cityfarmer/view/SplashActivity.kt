package com.CapstoneDesign.cityfarmer.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.CapstoneDesign.cityfarmer.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // 일정 시간 지연 이후 실행하기 위한 코드
        Handler(Looper.getMainLooper()).postDelayed({
            auth = Firebase.auth
            val currentUser = auth.currentUser
            if (currentUser == null) {
                //비로그인 상태
                // 일정 시간이 지나면 MainActivity로 이동
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                // 이전 키를 눌렀을 때 스플래시 스크린 화면으로 이동을 방지 하기 위해
                // 이동한 다음 사용 안 함으로 finish 처리
                finish()
            }else {
                //로그인 상태
                // 일정 시간이 지나면 MainActivity로 이동
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                // 이전 키를 눌렀을 때 스플래시 스크린 화면으로 이동을 방지 하기 위해
                // 이동한 다음 사용 안 함으로 finish 처리
                finish()
            }
        }, 2000) // 시간 2초 이후 실행
    }
}