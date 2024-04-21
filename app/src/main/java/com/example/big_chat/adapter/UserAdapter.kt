package com.example.big_chat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.big_chat.OnUserClickListener
import com.example.big_chat.R
import com.example.big_chat.model.User

class UserAdapter(private val context: Context, private val userList: ArrayList<User>,private val onUserClickListener: OnUserClickListener) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

        val currentUser = userList[position]

        holder.textName.text = currentUser.name
        Glide.with(holder.itemView.context)
            .load(currentUser.imgUri)
            .into(holder.imageUser)

        holder.itemView.setOnClickListener { onUserClickListener.onUserClicked(userList[position]) }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textName = itemView.findViewById<TextView>(R.id.name_tv)
        val imageUser = itemView.findViewById<ImageView>(R.id.icon_iv)
    }
}