package com.example.qlctcn.model

import com.google.firebase.Timestamp

data class User(
    val uid: String = "",
    val name: String = "",
    val balance: Long = 0L,
    val createdAt: Timestamp = Timestamp.now()
)