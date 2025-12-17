package com.example.qlctcn.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qlctcn.model.Category
import com.example.qlctcn.model.Transaction
import com.example.qlctcn.model.TransactionType
import com.example.qlctcn.repository.TransactionRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.UUID

class TransactionViewModel(
    private val transactionRepository: TransactionRepository,
    private val userViewModel: UserViewModel
) : ViewModel() {

    val transactions = mutableStateListOf<Transaction>()

    var uiBalance by mutableStateOf(0L)
    var uiIncome by mutableStateOf(0L)
    var uiExpense by mutableStateOf(0L)

    fun loadTransactions() {
        viewModelScope.launch {
            val list = transactionRepository.getAll()
            transactions.clear()
            transactions.addAll(list)
            recalculateStats()

            userViewModel.user.value?.let {
                uiBalance = it.balance
            }
        }
    }

    private fun recalculateStats() {
        uiIncome = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        uiExpense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addTransaction(amount: Long, category: Category, type: TransactionType, note: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        // ✅ SỬA: Tạo UUID ngẫu nhiên cho ID, tránh lỗi id rỗng
        val newId = UUID.randomUUID().toString()

        val transaction = Transaction(
            id = newId,
            userId = userId,
            amount = amount,
            categoryId = category.id,
            categoryName = category.name,
            type = type,
            date = Timestamp.now(),
            note = note
        )
        addTransaction(transaction)
    }

    fun addTransaction(transaction: Transaction) {
        // 1. CẬP NHẬT UI BALANCE
        if (transaction.type == TransactionType.EXPENSE) {
            uiBalance -= transaction.amount
            uiExpense += transaction.amount
        } else {
            uiBalance += transaction.amount
            uiIncome += transaction.amount
        }

        // 2. CẬP NHẬT LIST HIỂN THỊ
        transactions.add(0, transaction)

        // 3. UPDATE USER GỐC
        userViewModel.updateBalance(
            if (transaction.type == TransactionType.EXPENSE) -transaction.amount else transaction.amount
        )

        // 4. LƯU FIRESTORE (Chạy ngầm - Không reload)
        viewModelScope.launch {
            try {
                transactionRepository.create(transaction)
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    // ... (Các hàm update/delete giữ nguyên logic cũ, chúng đã ổn)
    fun updateTransaction(old: Transaction, updated: Transaction) {
        if (old.type == TransactionType.EXPENSE) { uiBalance += old.amount; uiExpense -= old.amount }
        else { uiBalance -= old.amount; uiIncome -= old.amount }

        if (updated.type == TransactionType.EXPENSE) { uiBalance -= updated.amount; uiExpense += updated.amount }
        else { uiBalance += updated.amount; uiIncome += updated.amount }

        val index = transactions.indexOfFirst { it.id == old.id }
        if (index != -1) transactions[index] = updated

        val diff = (if (updated.type == TransactionType.INCOME) updated.amount else -updated.amount) -
                (if (old.type == TransactionType.INCOME) old.amount else -old.amount)
        userViewModel.updateBalance(diff)

        viewModelScope.launch { transactionRepository.update(updated) }
    }

    fun deleteTransaction(transaction: Transaction) {
        if (transaction.type == TransactionType.EXPENSE) { uiBalance += transaction.amount; uiExpense -= transaction.amount }
        else { uiBalance -= transaction.amount; uiIncome -= transaction.amount }

        transactions.remove(transaction)

        userViewModel.updateBalance(
            if (transaction.type == TransactionType.EXPENSE) transaction.amount else -transaction.amount
        )

        viewModelScope.launch { transactionRepository.delete(transaction.id) }
    }

    fun getTransactionById(id: String): Transaction? = transactions.find { it.id == id }
    fun totalIncome(): Long = uiIncome
    fun totalExpense(): Long = uiExpense
}