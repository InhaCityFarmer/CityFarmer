package com.CapstoneDesign.cityfarmer.view

import android.content.Intent
import android.content.res.AssetManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.ImageBitmap
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.CapstoneDesign.cityfarmer.R
import com.CapstoneDesign.cityfarmer.`object`.Item
import com.CapstoneDesign.cityfarmer.`object`.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.io.InputStream
import java.io.InputStreamReader
import com.opencsv.CSVReader
import okhttp3.internal.notify
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var builderFirstLogin: AlertDialog.Builder
    private lateinit var arrayUserSelect: ArrayList<String>
    private lateinit var arrayUserRecommend: ArrayList<String>
    private lateinit var assetManager: AssetManager
    private lateinit var inputStream: InputStream
    private lateinit var reader: CSVReader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        arrayUserSelect = ArrayList()
        arrayUserRecommend = ArrayList()
        builderFirstLogin = AlertDialog.Builder(this)

        assetManager = this.assets
        inputStream = assetManager.open("recommend_plant.csv")
        reader = CSVReader(InputStreamReader(inputStream))

        val auth: FirebaseAuth = Firebase.auth
        val db = FirebaseFirestore.getInstance()

        val mainImage: ImageView = findViewById(R.id.mainImage)
        val mainTitle: TextView = findViewById(R.id.mainTitle)

        Log.d("내 auth", auth.currentUser?.uid.toString())
        db.collection("User").document(auth.currentUser!!.uid.toString()).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val nickname = document.getString("name")
                    mainTitle.text = nickname + "'s 농장"

                    val myUser = document.toObject<User>(User::class.java)
                    var firstLoginCheck = myUser!!.firstLogin
                    if (firstLoginCheck == true) {
                        firstLoginCheck = false
                        db.collection("User").document(auth.currentUser!!.uid.toString())
                            .update("firstLogin", firstLoginCheck)
                        Select1st()
                    }
                }
            }
        var standard_h: Double? = null
        var standard_m: Double? = null
        var standard_t: Double? = null

        db.collection("Standard").document("tomato").get()
            .addOnSuccessListener{document ->
                if (document != null) {
                    standard_h = document.getDouble("H")
                    Log.d("standard_h", "$standard_h")
                    standard_m = document.getDouble("M")
                    Log.d("standard_m", "$standard_m")
                    standard_t = document.getDouble("T")
                    Log.d("standard_t", "$standard_t")
                }
                db.collection("User").document(auth.currentUser!!.uid.toString()).collection("crop")
                    .document("tomato")
                    .addSnapshotListener { snapshot, e ->
                        if (e != null) {
                            Log.w("", "Listen failed.", e)
                            return@addSnapshotListener
                        }


                        var checked = 0;
                        if (snapshot != null && snapshot.exists()) {
                            val hField = snapshot.get("h") as? List<Any>
                            if (hField != null && hField.isNotEmpty()) {
                                val lastItem = hField[hField.size - 1].toString()
                                val numberPart = lastItem.split("*")[0]
                                //val number = numberPart.toIntOrNull()
                                val number = numberPart.toDouble()
                                println(number)
                                if (number != null) {
                                    Log.d("h", "$number")

                                    if (standard_h != null && number < (standard_h!! + 10) && number > (standard_h!! - 10)){
                                        checked+=1;
                                    }
                                }
                            }
                            val mField = snapshot.get("m") as? List<Any>
                            if (mField != null && mField.isNotEmpty()) {
                                val lastItem = mField[mField.size - 1].toString()
                                val numberPart = lastItem.split("*")[0]
                                //val number = numberPart.toIntOrNull()
                                val number = numberPart.toDouble()
                                if (number != null) {
                                    Log.d("m", "$number")

                                    if (standard_m != null && number < (standard_m!! + 10) && number > (standard_m!! - 10)){
                                        checked+=1;
                                    }
                                }

                            }
                            val tField = snapshot.get("t") as? List<Any>
                            if (tField != null && tField.isNotEmpty()) {
                                val lastItem = tField[tField.size - 1].toString()
                                val numberPart = lastItem.split("*")[0]
                                //val number = numberPart.toIntOrNull()
                                val number = numberPart.toDouble()
                                if (number != null) {
                                    Log.d("t", "$number")

                                    if (standard_t != null && number < (standard_t!! + 10) && number > (standard_t!! - 10)){
                                        checked+=1;
                                    }
                                }

                            }
                            if (checked <= 1) {
                                mainImage.setImageResource(R.drawable.sad)
                            } else if (checked <= 2){
                                mainImage.setImageResource(R.drawable.average)
                            }else{
                                mainImage.setImageResource(R.drawable.smile)
                            }
                        }
                    }

            }



        val btnMap = findViewById<Button>(R.id.btnMap)
        btnMap.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }

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

        val btnDB = findViewById<Button>(R.id.btnDashboard)
        btnDB.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }

        val btncctv = findViewById<ImageButton>(R.id.btnCCTV)
        btncctv.setOnClickListener {
            val intent = Intent(this, WebViewActivity::class.java)
            startActivity(intent)
        }

        val btnLogout = findViewById<ImageButton>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            auth.signOut()
            finish()
        }

        val btnChatbot = findViewById<Button>(R.id.btnChatbot)
        btnChatbot.setOnClickListener {
            val intent = Intent(baseContext, ChatBotActivity::class.java)
            startActivity(intent)
        }

        val btnChatting = findViewById<Button>(R.id.btnChatting)
        btnChatting.setOnClickListener {
            val intent = Intent(baseContext, PrevChatActivity::class.java)
            val user = auth.uid
            Log.d("지금 uid", user.toString())
            intent.putExtra("currentUserUid", user!!.toString())
            startActivity(intent)
        }
    }

    private fun Select1st() {
        builderFirstLogin.setTitle("재배 난이도")
        val itemList = arrayOf("상", "중", "하")
        builderFirstLogin.setItems(itemList) { dialog, which ->
            arrayUserSelect.add(itemList[which])
            Select2nd()
        }
        builderFirstLogin.show()
    }

    private fun Select2nd() {
        builderFirstLogin.setTitle("재배 시작 시기")
        val itemList = arrayOf("3~5월", "6~8월", "9~11월")
        builderFirstLogin.setItems(itemList) { dialog, which ->
            arrayUserSelect.add(itemList[which])
            Select3th()
        }
        builderFirstLogin.show()
    }

    private fun Select3th() {
        builderFirstLogin.setTitle("재배 면적 크기")
        val itemList = arrayOf("대", "중", "소")
        builderFirstLogin.setItems(itemList) { dialog, which ->
            if (itemList[which] == "대") arrayUserSelect.add("3")
            else if (itemList[which] == "중") arrayUserSelect.add("2")
            else if (itemList[which] == "소") arrayUserSelect.add("1")
            var nextLine: Array<String>?
            while (reader.readNext().also { nextLine = it } != null) {
                if (nextLine?.get(1) == arrayUserSelect[0]
                    && nextLine?.get(2) == arrayUserSelect[1]
                    && Integer.parseInt(nextLine!![3]) <= Integer.parseInt(arrayUserSelect[2])
                ) {
                    arrayUserRecommend.add(nextLine!![0])
                }
            }
            ShowRecommendResult()
        }
        builderFirstLogin.show()
    }

    private fun ShowRecommendResult() {
        builderFirstLogin.setTitle("재배 추천 작물")
        val itemList: Array<String> = if (arrayUserRecommend.isEmpty()) {
            arrayOf("추천 작물이 없습니다.")
        } else {
            arrayUserRecommend.toTypedArray()
        }
        builderFirstLogin.setItems(itemList) { dialog, which -> }
        builderFirstLogin.show()
    }
}
