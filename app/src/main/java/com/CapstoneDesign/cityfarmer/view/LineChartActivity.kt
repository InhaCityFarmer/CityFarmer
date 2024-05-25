package com.CapstoneDesign.cityfarmer.view

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.CapstoneDesign.cityfarmer.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class LineChartActivity : AppCompatActivity() {
    private lateinit var selectedCrop : String
    private lateinit var currentUserUid : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_line_chart)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        currentUserUid = intent.getStringExtra("currentUserUid").toString()
        selectedCrop = intent.getStringExtra("selectedCrop").toString()
        Log.d("현재 유져 uid",currentUserUid)
        Log.d("선택된 작물",selectedCrop)
        val db = Firebase.firestore
        val userCollection = db.collection("User")
        val documentRef = userCollection.document(currentUserUid).collection("crop").document(selectedCrop)
        val lineChart = findViewById<LineChart>(R.id.lineChart)

        documentRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val dValues = document.get("D") as List<String>
                    val hValues = document.get("H") as List<String>
                    val mValues = document.get("M") as List<String>
                    val tValues = document.get("T") as List<String> // Assuming another "T" field exists

                    val entriesD = dValues.mapIndexed { index, value -> Entry(index.toFloat(), value.toFloat()) }
                    val entriesT = tValues.mapIndexed { index, value -> Entry(index.toFloat(), value.toFloat()) }
                    val entriesM = mValues.mapIndexed { index, value -> Entry(index.toFloat(), value.toFloat()) }
                    val entriesH = hValues.mapIndexed { index, value -> Entry(index.toFloat(), value.toFloat()) }

                    val dataSetD = LineDataSet(entriesD, "D").apply { color = android.graphics.Color.RED }
                    val dataSetH = LineDataSet(entriesH, "H").apply { color = android.graphics.Color.BLUE }
                    val dataSetM = LineDataSet(entriesM, "M").apply { color = android.graphics.Color.GREEN }
                    val dataSetT = LineDataSet(entriesT, "T").apply { color = android.graphics.Color.BLACK }

                    val lineData = LineData(dataSetD, dataSetT, dataSetM, dataSetH)
                    lineChart.data = lineData
                    lineChart.invalidate() // Refresh the chart
                }

                else {
                    Log.d("Firestore", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Firestore", "get failed with ", exception)
            }
    }
}