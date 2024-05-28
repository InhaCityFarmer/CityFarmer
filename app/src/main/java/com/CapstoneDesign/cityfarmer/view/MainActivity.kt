package com.CapstoneDesign.cityfarmer.view

import android.content.Intent
import android.content.res.AssetManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var builderFirstLogin : AlertDialog.Builder
    private lateinit var arrayUserSelect : ArrayList<String>
    private lateinit var arrayUserRecommend : ArrayList<String>
    private lateinit var assetManager : AssetManager
    private lateinit var inputStream: InputStream
    private lateinit var reader : CSVReader
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
        arrayUserSelect  = ArrayList<String>()
        //유저에게 추천해줄 작물의 이름을 담을 배열 초기화
        arrayUserRecommend = ArrayList<String>()
        //작물 추천 알고리즘에서 다이얼로그를 띄우기 위한 빌더
        builderFirstLogin = AlertDialog.Builder(this)
        //cvs 파일 읽기 위한 작업
        assetManager = this.assets
        inputStream = assetManager.open("recommend_plant.csv")
        reader = CSVReader(InputStreamReader(inputStream))
        //파이어베이스 권한 생성 및  받아오기
        var auth : FirebaseAuth = Firebase.auth
        //파이어스토어 DB 접근 객체 얻어오기
        var db = FirebaseFirestore.getInstance()

        Log.d ("내 auth",auth.currentUser?.uid.toString())
        db.collection("User").document(auth.currentUser!!.uid.toString()).get()
            .addOnSuccessListener {document ->
                if( document != null)
                {
                    //내 유저 객체 DB에서 가져오기
                    val myUser = document.toObject<User>(User::class.java)
                    //처음으로 로그인 한 유저인지 DB에서 값을 가져옴
                    var firstLoginCheck = myUser!!.firstLogin
                    //만약 처음으로 로그인한 유저라면 firstLoginCheck를 false로 바꾸고 작물 추천 알고리즘을 띄움
                    if(firstLoginCheck == true)
                    {
                        firstLoginCheck=false
                        //변경된 값으로 DB 수정
                        db.collection("User").document(auth.currentUser!!.uid.toString()).update("firstLogin",firstLoginCheck)
                        //작물 추천 시작
                        Select1st()
                    }

                }
            }

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

        val btnDB = findViewById<Button>(R.id.btnDashboard)
        btnDB.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }

        val btncctv = findViewById<Button>(R.id.btnCCTV)
        btncctv.setOnClickListener {
            val intent = Intent(this, WebViewActivity::class.java)
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
            val intent = Intent(baseContext,ChatBotActivity::class.java)
            startActivity(intent)

        }

        val btnChatting = findViewById<Button>(R.id.btnChatting)
        btnChatting.setOnClickListener {
            // 채팅 버튼 클릭시 전의 채팅목록 불러오기
            val intent = Intent(baseContext, PrevChatActivity::class.java)
            val user = auth.uid
            Log.d("지금 uid",user.toString())
            intent.putExtra("currentUserUid", user!!.toString())

            startActivity(intent)
        }

    }

    private fun Select1st(){
        builderFirstLogin.setTitle("재배 난이도")    // 제목
        val itemList = arrayOf<String>("상", "중", "하")    // 항목 리스트
        // 항목 클릭 시 이벤트
        builderFirstLogin.setItems(itemList) { dialog, which ->
            arrayUserSelect.add(itemList[which])
            Select2nd()
        }
        builderFirstLogin.show()
    }

    private fun Select2nd(){
        builderFirstLogin.setTitle("재배 시작 시기")    // 제목
        val itemList = arrayOf<String>("3~5월", "6~8월", "9~11월")    // 항목 리스트
        // 항목 클릭 시 이벤트
        builderFirstLogin.setItems(itemList) { dialog, which ->
            arrayUserSelect.add(itemList[which])
            Select3th()
        }
        builderFirstLogin.show()
    }
    private fun Select3th(){
        builderFirstLogin.setTitle("재배 면적 크기")    // 제목
        val itemList = arrayOf<String>("대", "중", "소")    // 항목 리스트
        // 항목 클릭 시 이벤트
        builderFirstLogin.setItems(itemList) { dialog, which ->
            if(itemList[which] == "대") arrayUserSelect.add("3")
            else if(itemList[which] == "중") arrayUserSelect.add("2")
            else if(itemList[which] == "소") arrayUserSelect.add("1")
            var nextLine : Array<String>? = arrayOf()
            while (nextLine.apply {
                    nextLine = reader.readNext()
                } != null)
            {
                //위에서 고른 3가지 조건을 모두 부합하는지 cvs의 값과 비교
                if(nextLine?.get(1)?.toString() == arrayUserSelect[0]
                    && nextLine?.get(2)?.toString() == arrayUserSelect[1]
                    && Integer.parseInt(nextLine!!.get(3)) <= Integer.parseInt( arrayUserSelect[2]))
                {
                    //모두 부합하는 식물의 이름만 arrayUserRecommend에 저장
                    arrayUserRecommend.add(nextLine?.get(0)!!.toString())
                }
            }
            ShowRecommendResult()
        }
        builderFirstLogin.show()
    }

    private fun ShowRecommendResult() {

        builderFirstLogin.setTitle("재배 추천 작물")    // 제목
        val itemList : Array<String>
        if(arrayUserRecommend.isEmpty()) {
            itemList = arrayOf("추천 작물이 없습니다.")
        }
        else {
            itemList = arrayUserRecommend.toTypedArray()  // 항목 리스트
        }
        // 항목 클릭 시 이벤트
        builderFirstLogin.setItems(itemList) { dialog, which ->
        }
        builderFirstLogin.show()
    }
}