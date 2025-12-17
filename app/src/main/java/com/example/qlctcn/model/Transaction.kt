package com.example.qlctcn.model

import com.google.firebase.Timestamp

data class Transaction(
    val id: String = "",
    val userId: String = "",
    val amount: Long = 0L,
    val categoryId: String = "",
    val categoryName: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val date: Timestamp = Timestamp.now(),
    val note: String = ""
)