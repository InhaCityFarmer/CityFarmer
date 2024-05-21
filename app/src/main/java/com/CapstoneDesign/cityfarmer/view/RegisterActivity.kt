package com.CapstoneDesign.cityfarmer.view

import android.content.ContentValues
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.CapstoneDesign.cityfarmer.R
import com.CapstoneDesign.cityfarmer.`object`.Farm
// merge 할때 추가안된애들 추가함
import com.CapstoneDesign.cityfarmer.`object`.Item
//
import com.CapstoneDesign.cityfarmer.`object`.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        auth = Firebase.auth

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //파이어스토어 DB 접근 객체 얻어오기
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()

        //회원가입에 필요한 유저 정보 받아오기
        val textID = findViewById<EditText>(R.id.registerID)
        val textPW = findViewById<EditText>(R.id.registerPW)
        val textNickname = findViewById<EditText>(R.id.registerNickname)
        val textFarmName = findViewById<EditText>(R.id.farmName)
        val textFarmAddress = findViewById<EditText>(R.id.farmAddress)
        val textFarmNumber = findViewById<EditText>(R.id.farmNumber)
        val btnRegister = findViewById<Button>(R.id.btnNext)

        btnRegister.setOnClickListener {
                val stringID = textID.text.toString()
                val stringPW = textPW.text.toString()
                val stringAddress = textFarmAddress.text.toString()


                // 이메일에 '@' 기호가 포함되어 있는지 확인
                if (!stringID.contains('@')) {
                    Toast.makeText(
                        baseContext,
                        "이메일 주소가 올바르지 않습니다.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    return@setOnClickListener
                }

                // 비밀번호가 6자리 이상인지 확인
                if (stringPW.length < 6) {
                    Toast.makeText(
                        baseContext,
                        "비밀번호는 6자리 이상이어야 합니다.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    return@setOnClickListener
                }
            auth.createUserWithEmailAndPassword(textID.text.toString(), textPW.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) { //인증 성공
                        Log.d(ContentValues.TAG, "createUserWithEmail:success")
                        val me = auth.currentUser
                        val user = User( //유저 객체 생성 및 입력 받은 값 삽입
                            UID = me!!.uid,
                            name = textNickname.text.toString(),
                            firstLogin = false,
                            farmName = textFarmName.text.toString(),
                            address = textFarmAddress.text.toString(),
                            sector = textFarmNumber.text.toString(),
                            crop = ArrayList<HashMap<String,Array<Double>>>(),
                            inventory = ArrayList<Item>(),
                            prev_chat = HashMap<String,String>()
                            )
                        //db에 유저 객체 저장
                        db.collection("User").document(me.uid).set(user)

                        //db에서 유저가 입력한 farmname과 일치하는 농장 있는지 탐색
                        db.collection("Farm")
                            .whereEqualTo("name",textFarmName.text.toString())
                            .get()
                            .addOnSuccessListener { documents ->
                                if(documents.isEmpty) //유저가 입력한 farmName의 농장이 없다면 새로운 농장 객체 생성
                                {
                                    //주소를 x, y 좌표로 변환하기 위한 지오코더 선언
                                    val geocoder : Geocoder = Geocoder(this.applicationContext)
                                    //stringAddress에서 받아온 주소를 지오코더에 넣어서 나온 결과를 list에 저장한다.
                                    val list : List<Address>? = geocoder.getFromLocationName(stringAddress,1)
                                    if(list != null) { //리스트가 비어있지 않다면 위도 경도 받아옴
                                        val address: Address = list.get(0)//받아온 결과의 0번째 결과를 address로 선언
                                        val lat = address.latitude //위도 받아오기
                                        val lon = address.longitude //경도 받아오기
                                        val farm = Farm(
                                            name = textFarmName.text.toString(),
                                            address = textFarmAddress.text.toString(),
                                            users = hashMapOf(
                                                Pair<String, String>(
                                                    me.uid,
                                                    textFarmNumber.text.toString()
                                                )
                                            ),
                                            lat = lat,
                                            lon = lon
                                        )
                                        //생성한 농장 객체를 DB에 저장
                                        db.collection("Farm").document(textFarmName.text.toString())
                                            .set(farm)
                                    }
                                }
                                else
                                {
                                    for( document in documents) {//유저가 입력한 farmName의 농장이 있다면 해당 농장에 유저 추가
                                        val myFarm = document.toObject(Farm::class.java);
                                        //DB에 있는 농장 데이터를 Farm 객체 타입으로 받아와서 myFarm에 저장
                                            myFarm.users.put(me.uid,textFarmNumber.text.toString());
                                        //myFarm의 users에 회원가입한 유저의 UID, 회원가입한 유저의 농지번호로 해쉬맵 형태로 저장
                                        db.collection("Farm").document(document.id).set(myFarm)//수정한 농장 객체를 다시 DB에 덮어쓰기로 저장
                                    }
                                }
                            }
                            .addOnFailureListener {exception ->
                                Log.e("register", "error : ",exception)
                            }

                        //회원가입 종료 후 MainActivity로 intent
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {//인증 실패
                        // If sign in fails, display a message to the user.
                        Log.w(ContentValues.TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
        }


    }





}
