package com.CapstoneDesign.cityfarmer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.CapstoneDesign.cityfarmer.R
import com.CapstoneDesign.cityfarmer.`object`.Message


class MessageRecyclerViewAdapter(private val messageList: List<Message>) :
    RecyclerView.Adapter<MessageRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_chat_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messageList[position]
        if (message.sentBy == Message.SENT_BY_ME) {
            holder.left_chat_view.visibility = View.GONE
            //holder.left_chat_view.visibility = View.VISIBLE
            holder.right_chat_view.visibility = View.VISIBLE
            holder.right_chat_tv.text = message.message
        } else {
            holder.right_chat_view.visibility = View.GONE
            //holder.right_chat_view.visibility = View.VISIBLE
            holder.left_chat_view.visibility = View.VISIBLE
            holder.left_chat_tv.text = message.message
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val left_chat_view: ConstraintLayout = itemView.findViewById(R.id.leftChatView)
        val right_chat_view: ConstraintLayout = itemView.findViewById(R.id.rightChatView)
        val left_chat_tv: TextView = itemView.findViewById(R.id.left_chat_tv)
        val right_chat_tv: TextView = itemView.findViewById(R.id.right_chat_tv)
    }
}
