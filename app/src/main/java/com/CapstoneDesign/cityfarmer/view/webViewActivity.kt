package com.CapstoneDesign.cityfarmer.view

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.CapstoneDesign.cityfarmer.R

class webViewActivity : AppCompatActivity() {
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
        myWebView.settings.javaScriptEnabled = true
        val myUrl = "http://165.246.110.201:5000/stream?src=0"
        myWebView.webViewClient = WebViewClient()
        myWebView.webChromeClient = WebChromeClient()
        myWebView.loadUrl(myUrl)


    }

    override fun onBackPressed() {
        val my_wb = findViewById<WebView>(R.id.myWebView)
        if(my_wb.canGoBack()){
            my_wb.goBack()
        }
        else{
            super.onBackPressed()
        }

    }
}