package com.CapstoneDesign.cityfarmer.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.CapstoneDesign.cityfarmer.R
import com.CapstoneDesign.cityfarmer.`object`.Item
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.ktx.Firebase
import kotlin.properties.Delegates

class ModifyInventoryActivity : AppCompatActivity() {
    private lateinit var db : FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var itemName : String
    private lateinit var itemNumber : String
    private lateinit var type : String
    private lateinit var myItem : ArrayList<Item>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_modify_inventory)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = Firebase.auth
        //파이어스토어 DB 접근 객체 얻어오기
        db = FirebaseFirestore.getInstance()
        val myAuth = auth.currentUser

        itemName = intent.getStringExtra("name").toString()
        itemNumber = intent.getStringExtra("number").toString()
        type = intent.getStringExtra("type").toString()

        val itemname = findViewById<TextView>(R.id.itemNamePlace)
        itemname.text = itemName

        val itemtype = findViewById<TextView>(R.id.itemTypePlace)
        itemtype.text = type

        val itemnum = findViewById<TextView>(R.id.itemNumPlace)
        itemnum.text = itemNumber

        val btnCancle = findViewById<Button>(R.id.btnAddItemCancelInModify)
        btnCancle.setOnClickListener {
            Toast.makeText(this,"$itemName 수정 has been cancled",Toast.LENGTH_SHORT).show()
            val intent = Intent(baseContext,InventoryActivity::class.java)
            startActivity(intent)
            finish()
        }

        val btnUpdate = findViewById<Button>(R.id.btnModifyItem)
        btnUpdate.setOnClickListener {
            val textItemNumber = findViewById<TextInputEditText>(R.id.textItemNumberModify)
            val intItemNumber = Integer.parseInt(textItemNumber.text.toString())

            val userId = auth.currentUser?.uid ?: return@setOnClickListener

            // Firestore의 사용자 문서 참조
            val userDocRef = db.collection("User").document(userId)

            userDocRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    // 스냅샷에서 inventory 필드만  보기
                    val inventory = snapshot.get("inventory") as? ArrayList<HashMap<String, Any>>?
                    if (inventory != null) {
                        // 아이템 삭제
                        val updatedInventory = inventory.map { item ->
                            // 아이템 이름이 일치하는 경우
                            if (item["name"] == itemName) {
                                // 개수를 수정하여 반환
                                item.apply { put("number", intItemNumber) }
                            } else {
                                // 나머지는 그대로 넣음
                                item
                            }
                        }                        // Firestore의 inventory 업데이트
                        userDocRef.update("inventory", updatedInventory)
                            .addOnSuccessListener {
                            }
                    }

                } else {
                    Toast.makeText(
                        this, "user document로 가는 path에 오류 발생",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            val intent = Intent(baseContext,InventoryActivity::class.java)
            startActivity(intent)
            finish()

        }

        val btnDeleteItem = findViewById<Button>(R.id.btnDeleteItem)
        btnDeleteItem.setOnClickListener {
            val userId = auth.currentUser?.uid ?: return@setOnClickListener

            val userDocRef = db.collection("User").document(userId)

            userDocRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    // inventory 필드 가져오기
                    val inventory =
                        snapshot.get("inventory") as? ArrayList<HashMap<String, Any>>?
                    if (inventory != null) {
                        // 아이템 삭제
                        val updatedInventory = inventory.filter { it["name"] != itemName }
                        // Firestore의 inventory 업데이트
                        userDocRef.update("inventory", updatedInventory)
                            .addOnSuccessListener {
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    this,
                                    "Failed to delete $itemName: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                } else {
                    Log.d("아오", "Current data: null")
                }
            }
            val intent = Intent(baseContext,InventoryActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}