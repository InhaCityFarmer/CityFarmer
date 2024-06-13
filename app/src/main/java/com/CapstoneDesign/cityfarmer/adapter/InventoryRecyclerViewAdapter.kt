package com.CapstoneDesign.cityfarmer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.CapstoneDesign.cityfarmer.databinding.ActivityInventoryBinding
import com.CapstoneDesign.cityfarmer.databinding.RecyclerviewInventoryItemBinding
import com.CapstoneDesign.cityfarmer.`object`.Item


class InventoryRecyclerViewAdapter (private val item : ArrayList<Item>)
    : RecyclerView.Adapter<InventoryRecyclerViewAdapter.MyViewHolder>() {
        //레이아웃 이름 뒤에 Binding을 붙이면 binding 타입으로 만들 수 있다.
        //바인딩 타입으로 만들면 findViewByID이런거 안 써도 해당 레이아웃 내의 있는 것들에 접근 가능하다.
    inner class MyViewHolder(binding: RecyclerviewInventoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var textItemName = binding.textItemName
        val textItemNumber = binding.textItemNumber
            //뷰 바인딩에서 기본적으로 제공하는 root 변수는 레이아웃의 루트 레이아웃을 의미한다.
        val root = binding.root
    }

    interface Itemclick{
        fun onclick(binding : View, position: Int)
    }

    var itemclick : Itemclick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // item_todo.xml 뷰 바인딩 객체 생성
        val binding: RecyclerviewInventoryItemBinding =
            RecyclerviewInventoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val itemDate = item[position]
        // 기존에 임시로 들어가 있던 값 실제 값으로 변경
        holder.textItemName.text = itemDate.name
        holder.textItemNumber.text = itemDate.number.toString()+" 개"

        if(itemclick != null) {
            holder.itemView.setOnClickListener { v ->
                itemclick?.onclick(v, position)
            }
        }
    }
    override fun getItemCount(): Int {
        // 리사이클러뷰 아이템 개수는 할 일 리스트 크기
        return item.size
    }
}