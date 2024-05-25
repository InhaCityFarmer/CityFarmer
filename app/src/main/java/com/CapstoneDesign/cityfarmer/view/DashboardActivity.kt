package com.CapstoneDesign.cityfarmer.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.CapstoneDesign.cityfarmer.R
import com.CapstoneDesign.cityfarmer.adapter.chatAdapter
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore


class DashboardActivity : AppCompatActivity() {
    private lateinit var currentUserUid : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_daashboard)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        currentUserUid = intent.getStringExtra("currentUserUid").toString()
        // 일단은 crop에 접근해야 하는데, 넣은 data가 practice밖에 없으므로 일단 practice로 uid 설정해둠. 나중에 바꾸면됨
        ///////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////
        currentUserUid = "practice"
        ///////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////

        val cropList = mutableListOf<String>()
        val db = Firebase.firestore
        val userCollection = db.collection("User")
        val documentRef = userCollection.document(currentUserUid).collection("crop")

        documentRef.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    cropList.add(document.id)
                }
                // 여기에 cropList를 사용한 추가 작업을 작성할 수 있습니다.
                Log.d("CropList", cropList.toString())

                val croprv = findViewById<RecyclerView>(R.id.croplistRecyclerView)

                val rv_adapter = chatAdapter(cropList)
                croprv.adapter = rv_adapter
                croprv.layoutManager = LinearLayoutManager(this)
                rv_adapter.itemclick = object : chatAdapter.Itemclick {
                    override fun onclick(view: View, position: Int) {
                        Toast.makeText(baseContext, "Clicked position: $position", Toast.LENGTH_LONG).show()
                        val intent = Intent(baseContext, LineChartActivity::class.java)
                        intent.putExtra("currentUserUid",currentUserUid)
                        intent.putExtra("selectedCrop", cropList[position])
                        startActivity(intent)
                    }


                }

            }
            .addOnFailureListener { exception ->
                Log.w("Error", "Error getting documents: ", exception)
            }

    }
}