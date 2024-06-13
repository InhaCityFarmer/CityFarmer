package com.CapstoneDesign.cityfarmer.view

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.CapstoneDesign.cityfarmer.R


class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val myWebView = findViewById<WebView>(R.id.myDashboardWebView)
        var myUrl = "http://43.202.237.52:8501"
        val dash1 = findViewById<Button>(R.id.btnDash1)
        val dash2 = findViewById<Button>(R.id.btnDash2)
        dash1.setOnClickListener {
            myUrl = "http://43.202.237.52:8501"
            myWebView.loadUrl(myUrl)
        }
        dash2.setOnClickListener{
            myUrl = "https://dashboard-kwonjangwoo-ews.education.wise-paas.com/d/oouLvK8Iz/cityfarmerdashboard?orgId=8"
            myWebView.loadUrl(myUrl)
        }

        myWebView.webViewClient = WebViewClient()
        myWebView.webChromeClient = WebChromeClient()
        myWebView.settings.apply {
            javaScriptEnabled = true
            databaseEnabled = true
            javaScriptCanOpenWindowsAutomatically= true // 자바스크립트가 window.open()을 사용할 수 있도록 설정
            loadWithOverviewMode= true // html의 컨텐츠가 웹뷰보다 클 경우 스크린 크기에 맞게 조정
            useWideViewPort= true // 화면 사이즈 맞추기 허용여부
            domStorageEnabled= true // DOM(html 인식) 저장소 허용여부
        }
        myWebView.loadUrl(myUrl)
    }
}