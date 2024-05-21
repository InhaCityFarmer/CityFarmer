package com.CapstoneDesign.cityfarmer.view

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.CapstoneDesign.cityfarmer.R
import com.CapstoneDesign.cityfarmer.`object`.Item
import com.CapstoneDesign.cityfarmer.`object`.User
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class AddInventoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_inventory)

        // R.id.spinnerInventoryItem 는 activity_add_inventory.xml의 Spinner 태그의 id
        val spinner: Spinner = findViewById(R.id.spinnerInventoryItem)

        ArrayAdapter.createFromResource(
            this,
            //  R.array.inventory_item_array 는 res/values의 string-array 태그의 name
            R.array.inventory_item_array,

            // android.R.layout.simple_spinner_dropdown_item 은 android 에서 기본 제공 되는 layout
            // 이 부분은 "선택된 item" 부분의 layout을 결정
            android.R.layout.simple_spinner_dropdown_item

        ).also { adapter ->

            // android.R.layout.simple_spinner_dropdown_item 도 android 에서 기본 제공 되는 layout
            // 이 부분은 "선택할 item 목록" 부분의 layout을 결정
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        //인벤토리에 농작물, 농기구 추가하는 버튼
        val btnAddItem = findViewById<Button>(R.id.btnAddItem)
        btnAddItem.setOnClickListener {
            //파이어베이스 권한 생성 및 받아오기
            var auth : FirebaseAuth = Firebase.auth

            //파이어스토어 DB 접근 객체 얻어오기
            val db: FirebaseFirestore = FirebaseFirestore.getInstance()

            //현재 내가 로그인 중인 유저 지정하기
            val myAuth = auth.currentUser

            //findViewById로 아이템 타입, 아이템 이름, 아이템 개수 가져 오기
            val spinner = findViewById<Spinner>(R.id.spinnerInventoryItem)
            val textItemName = findViewById<TextInputEditText>(R.id.textItemName)
            val textItemNumber = findViewById<TextInputEditText>(R.id.textItemNumber)

            //가져온 것들 string, int로 변환하기
            val stringItemType = spinner.selectedItem.toString()
            val stringItemName = textItemName.text.toString()
            val intItemNumber = Integer.parseInt(textItemNumber.text.toString())

            //db에 아이템 저장
            db.collection("User").document(myAuth!!.uid).get()
                .addOnSuccessListener { document ->
                    if(document != null)
                    {
                        //내 유저 객체 가져오기
                        val myUser = document.toObject(User::class.java)
                        //내 인벤토리 가져오기
                        val myInventory = myUser!!.inventory
                        //입력 받은 아이템 정보 인벤토리에 입력
                        myInventory.add(Item(stringItemType, stringItemName, intItemNumber))
                        myUser.inventory = myInventory
                        //변경된 값으로 DB 수정
                        db.collection("User").document(myAuth.uid).update("inventory",myInventory)
                    }
                }
            finish()
        }

        //인벤토리에 농작물, 농기구 추가를 취소 하는 버튼
        val btnAddItemCancel = findViewById<Button>(R.id.btnAddItemCancel)
        btnAddItemCancel.setOnClickListener {
            finish()
        }

    }




}