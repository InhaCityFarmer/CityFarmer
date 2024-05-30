package com.CapstoneDesign.cityfarmer.view

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.CapstoneDesign.cityfarmer.R

class WebViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_web_view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val myWebView = findViewById<WebView>(R.id.myWebView)
        var myUrl = "http://165.246.110.201:5000/stream?src=0"
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

//    override fun onBackPressed() {
//        val my_wb = findViewById<WebView>(R.id.myWebView)
//        if(my_wb.canGoBack()){
//            my_wb.goBack()
//        }
//        else{
//            super.onBackPressed()
//        }
//
//    }
}