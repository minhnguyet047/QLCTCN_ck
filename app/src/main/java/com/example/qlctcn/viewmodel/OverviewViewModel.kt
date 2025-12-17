package com.example.qlctcn.viewmodel

import androidx.lifecycle.ViewModel
import com.example.qlctcn.model.Transaction
import com.example.qlctcn.model.TransactionType

class OverviewViewModel {

    fun totalIncome(transactions: List<Transaction>): Long =
        transactions.filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }

    fun totalExpense(transactions: List<Transaction>): Long =
        transactions.filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }

    fun mostExpenseCategory(
        transactions: List<Transaction>
    ): String? =
        transactions
            .filter { it.type == TransactionType.EXPENSE }
            .groupBy { it.categoryName }
            .maxByOrNull { it.value.sumOf { t -> t.amount } }
            ?.key
}