package com.CapstoneDesign.cityfarmer.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.CapstoneDesign.cityfarmer.R

class chatAdapter (val users : MutableList<String>) :
    RecyclerView.Adapter<chatAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // 여기서 가져옴
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_chatting , parent, false )

        return ViewHolder(view)
    }

    interface Itemclick{
        fun onclick(view : View, position: Int)
    }

    var itemclick : Itemclick? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if(itemclick != null) {
            holder.itemView.setOnClickListener { v ->
                itemclick?.onclick(v, position)
            }
        }
        // 여기서 binding : recyclerview의 item에 넣어서 내가 원하는 activity의 xml로 넘겨주는 것
        holder.bindItems(users[position])
    }


    override fun getItemCount(): Int {
        // 여기서는 몇개인지 알려줌
        return users.size

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bindItems(item : String){
            val rv_text = itemView.findViewById<TextView>(R.id.chatTextplace)
            rv_text.text = item
        }
    }

}