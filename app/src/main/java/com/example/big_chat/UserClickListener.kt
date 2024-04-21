package com.example.big_chat

import com.example.big_chat.model.User


interface OnUserClickListener {
    fun onUserClicked(user: User?)
}