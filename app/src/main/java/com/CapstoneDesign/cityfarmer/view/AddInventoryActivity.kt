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

        val spinner: Spinner = findViewById(R.id.spinnerInventoryItem)
        ArrayAdapter.createFromResource(
            this,
            R.array.inventory_item_array,
            R.layout.spinner_dropdown_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
            spinner.adapter = adapter
        }

        val btnAddItem = findViewById<Button>(R.id.btnAddItem)
        btnAddItem.setOnClickListener {
            val auth: FirebaseAuth = Firebase.auth
            val db: FirebaseFirestore = FirebaseFirestore.getInstance()
            val myAuth = auth.currentUser

            val spinner = findViewById<Spinner>(R.id.spinnerInventoryItem)
            val textItemName = findViewById<TextInputEditText>(R.id.textItemName)
            val textItemNumber = findViewById<TextInputEditText>(R.id.textItemNumber)

            val stringItemType = spinner.selectedItem.toString()
            val stringItemName = textItemName.text.toString()
            val intItemNumber = Integer.parseInt(textItemNumber.text.toString())

            db.collection("User").document(myAuth!!.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val myUser = document.toObject(User::class.java)
                        val myInventory = myUser!!.inventory
                        myInventory.add(Item(stringItemType, stringItemName, intItemNumber))
                        myUser.inventory = myInventory
                        db.collection("User").document(myAuth.uid).update("inventory", myInventory)
                    }
                }
            finish()
        }

        val btnAddItemCancel = findViewById<Button>(R.id.btnAddItemCancel)
        btnAddItemCancel.setOnClickListener {
            finish()
        }
    }
}