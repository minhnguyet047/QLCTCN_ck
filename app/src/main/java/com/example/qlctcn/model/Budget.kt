// qlctcn/model/Budget.kt
package com.example.qlctcn.model

data class Budget(
    val id: String = "",
    val userId: String = "",
    val categoryId: String = "",
    val categoryName: String = "",
    val month: String = "",
    val limitAmount: Long = 0L

)