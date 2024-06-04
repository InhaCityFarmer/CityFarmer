package com.CapstoneDesign.cityfarmer.view

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.CapstoneDesign.cityfarmer.R
import com.CapstoneDesign.cityfarmer.adapter.InventoryRecyclerViewAdapter
import com.CapstoneDesign.cityfarmer.databinding.ActivityInventoryBinding
import com.CapstoneDesign.cityfarmer.`object`.Item
import com.CapstoneDesign.cityfarmer.`object`.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class InventoryActivity : AppCompatActivity() {
    private lateinit var binding : ActivityInventoryBinding
    private lateinit var db : FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var myItem : ArrayList<Item>
    private lateinit var adapter : InventoryRecyclerViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inventory)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //데이터 바인딩 하기 위한 설정
        binding = ActivityInventoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //myItem 배열을 사용하기 전 초기화 해주기
        myItem = ArrayList<Item>()
        //파이어베이스 권한 생성 및 받아오기
        auth = Firebase.auth
        //파이어스토어 DB 접근 객체 얻어오기
        db = FirebaseFirestore.getInstance()
        //기본으로는 내 농작물 띄워줌
        setRecyclerView()
        getMyCrop()
        //인벤토리에 농작물, 농기구 추가하는 버튼
        val btnImageAddInventory = findViewById<ImageView>(R.id.imageViewAddInventory)
        btnImageAddInventory.setOnClickListener {
            val intent = Intent(this, AddInventoryActivity::class.java)
            startActivity(intent)
        }




        //인벤토리에 농작물을 볼 수 있는 버튼
        val btnSearchCrop = findViewById<Button>(R.id.btnSearchCrop)
        btnSearchCrop.setOnClickListener {

            getMyCrop()
        }

        //인벤토리에 농기구를 볼 수 있는 버튼
        val btnSearchTool = findViewById<Button>(R.id.btnSearchTool)
        btnSearchTool.setOnClickListener {
            getMyTool()
        }



    }


    private fun getMyTool() {
        db.collection("User").document(auth.currentUser!!.uid.toString())
            .addSnapshotListener { document, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (document != null && document.exists()) {
                    // myItemList 초기화
                    myItem = ArrayList()
                    // 내 유저 객체 가져오기
                    val myUser = document.toObject<User>(User::class.java)
                    // 내 인벤토리 가져오기
                    val tempList: ArrayList<Item> = myUser!!.inventory
                    for (i in tempList) {
                        // 타입이 농기구인 경우에만 어레이리스트에 넣어서 리사이클러뷰로 보내줌
                        if (i.type == "농기구") {
                            myItem.add(i)
                        }
                    }
                    setRecyclerView()
                }
            }
    }

    private fun getMyCrop() {
        db.collection("User").document(auth.currentUser!!.uid.toString())
            .addSnapshotListener { document, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (document != null && document.exists()) {
                    // myItemList 초기화
                    myItem = ArrayList()
                    // 내 유저 객체 가져오기
                    val myUser = document.toObject<User>(User::class.java)
                    // 내 인벤토리 가져오기
                    val tempList: ArrayList<Item> = myUser!!.inventory
                    for (i in tempList) {
                        // 타입이 농작물인 경우에만 어레이리스트에 넣어서 리사이클러뷰로 보내줌
                        if (i.type == "농작물") {
                            myItem.add(i)
                        }
                    }
                    setRecyclerView()
                }
            }
    }

    private fun setRecyclerView() {
        // 리사이클러뷰 설정
        runOnUiThread {
            this@InventoryActivity.adapter = InventoryRecyclerViewAdapter(myItem) // 어댑터 객체 할당
            binding.recyclerViewInventory.adapter =
                this@InventoryActivity.adapter // 리사이클러뷰 어댑터로 위에 만든 어댑터 올리기
            binding.recyclerViewInventory.layoutManager =
                LinearLayoutManager(this@InventoryActivity) // 레이아웃 매니저 설정
            adapter.notifyDataSetChanged()

            adapter.itemclick = object : InventoryRecyclerViewAdapter.Itemclick {

                override fun onclick(binding: View, position: Int) {
                    val clickedItem = myItem[position]
                    val itemname = myItem[position].name
                    Toast.makeText(baseContext, "$itemname 수정 페이지로 이동", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@InventoryActivity, ModifyInventoryActivity::class.java)
                    intent.putExtra("type",myItem[position].type)
                    intent.putExtra("name",myItem[position].name)
                    intent.putExtra("number",myItem[position].number.toString())
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
}
