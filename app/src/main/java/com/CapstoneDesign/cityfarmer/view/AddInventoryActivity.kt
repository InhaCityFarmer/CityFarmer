package com.CapstoneDesign.cityfarmer.view

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.CapstoneDesign.cityfarmer.R

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
            finish()
        }

        //인벤토리에 농작물, 농기구 추가하는 버튼
        val btnAddItemCancel = findViewById<Button>(R.id.btnAddItemCancel)
        btnAddItemCancel.setOnClickListener {
            finish()
        }

    }




}