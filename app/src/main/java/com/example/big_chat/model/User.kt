package com.example.big_chat.model

data class User(
    var name: String? = null,
    var email: String? = null,
    var uid: String? = null,
    var password: String? = null,
    var imgUri: String? = null
)