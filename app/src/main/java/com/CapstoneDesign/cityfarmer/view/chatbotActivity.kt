package com.CapstoneDesign.cityfarmer.view

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.CapstoneDesign.cityfarmer.R
import com.CapstoneDesign.cityfarmer.`object`.Message
import com.CapstoneDesign.cityfarmer.adapter.MessageRecyclerViewAdapter
import com.google.rpc.context.AttributeContext.Resource

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class chatbotActivity : AppCompatActivity() {

    private lateinit var recycler_view: RecyclerView
    private lateinit var tv_welcome: TextView
    private lateinit var et_msg: EditText
    private lateinit var btn_send: Button
    private lateinit var MY_SECRET_KEY : String
    private var messageList: MutableList<Message> = mutableListOf()
    private lateinit var messageAdapter: MessageRecyclerViewAdapter

    private val JSON = "application/json; charset=utf-8".toMediaType()
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chatbot)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        MY_SECRET_KEY = resources.getString(R.string.OpenAI_API)
        recycler_view = findViewById(R.id.recycler_view)
        tv_welcome = findViewById(R.id.tv_welcome)
        et_msg = findViewById(R.id.et_msg)
        btn_send = findViewById(R.id.btn_send)

        recycler_view.setHasFixedSize(true)
        val manager = LinearLayoutManager(this)
        manager.stackFromEnd = true
        recycler_view.layoutManager = manager

        messageAdapter = MessageRecyclerViewAdapter(messageList)
        recycler_view.adapter = messageAdapter

        btn_send.setOnClickListener {
            val question = et_msg.text.toString()
            if (question.isNotEmpty()) {
                addToChat(question, Message.SENT_BY_ME)
                et_msg.setText("")
                callAPI(question)
                tv_welcome.visibility = View.GONE
            } else {
                // 질문이 비어 있는 경우 사용자에게 메시지를 입력하라는 안내 메시지를 보여줄 수 있습니다.
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addToChat(message: String, sentBy: String) {
        runOnUiThread {
            messageList.add(Message(message, sentBy))
            messageAdapter.notifyDataSetChanged()
            // recycler_view.smoothScrollToPosition(messageList.size)
        }
    }

    private fun addResponse(response: String) {
        messageList.removeAt(messageList.size - 1)
        addToChat(response, Message.SENT_BY_BOT)
    }

    private fun callAPI(question: String) {
        messageList.add(Message("...", Message.SENT_BY_BOT))

        val arr = JSONArray()
        val baseAi = JSONObject()
        val userMsg = JSONObject()
        try {
            baseAi.put("role", "user")
            baseAi.put("content", "You are a helpful and kind AI Assistant.")
            userMsg.put("role", "user")
            userMsg.put("content", question)
            arr.put(baseAi)
            arr.put(userMsg)
        } catch (e: JSONException) {
            throw RuntimeException(e)
        }

        val jsonObject = JSONObject().apply {
            put("model", "gpt-3.5-turbo")
            put("messages", arr)
        }

        val body = jsonObject.toString().toRequestBody(JSON)
        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .header("Authorization", "Bearer $MY_SECRET_KEY")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                addResponse("Failed to load response due to ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    if (response.isSuccessful) {
                        try {
                            val jsonObject = JSONObject(responseBody.string())
                            val jsonArray = jsonObject.getJSONArray("choices")
                            val result = jsonArray.getJSONObject(0).getJSONObject("message").getString("content")
                            addResponse(result.trim())
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        addResponse("Failed to load response due to ${responseBody.string()}")
                    }
                }
            }
        })
    }
}