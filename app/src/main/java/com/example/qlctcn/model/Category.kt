package com.example.qlctcn.model

data class Category(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val type: TransactionType = TransactionType.EXPENSE
)