package com.example.qlctcn.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qlctcn.model.Budget
import com.example.qlctcn.repository.BudgetRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.UUID

class BudgetViewModel(
    private val budgetRepository: BudgetRepository
) : ViewModel() {

    val budgets = mutableStateListOf<Budget>()
    private var loadJob: Job? = null

    fun loadBudgets(month: String) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            budgets.clear()
            budgets.addAll(budgetRepository.getByMonth(month))
        }
    }

    fun createOrUpdateBudget(categoryId: String, categoryName: String, month: String, limitAmount: Long) {
        viewModelScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            val existingIndex = budgets.indexOfFirst { it.categoryId == categoryId && it.month == month }

            if (existingIndex != -1) {
                // UPDATE LOCAL
                val oldBudget = budgets[existingIndex]
                val updatedBudget = oldBudget.copy(limitAmount = limitAmount, categoryName = categoryName)
                budgets[existingIndex] = updatedBudget

                // UPDATE SERVER
                budgetRepository.update(updatedBudget)
            } else {
                // CREATE NEW
                val newId = UUID.randomUUID().toString()
                val newBudget = Budget(
                    id = newId,
                    categoryId = categoryId,
                    categoryName = categoryName,
                    month = month,
                    limitAmount = limitAmount,
                    userId = userId
                )

               
                budgets.add(newBudget)

                budgetRepository.create(newBudget)
            }

          
        }
    }

    fun getBudgetById(id: String): Budget? {
        return budgets.find { it.id == id }
    }

    fun updateLimit(budgetId: String, newLimit: Long) {
        val index = budgets.indexOfFirst { it.id == budgetId }
        if (index != -1) {
            val oldBudget = budgets[index]
            val updated = oldBudget.copy(limitAmount = newLimit)
            budgets[index] = updated

            viewModelScope.launch {
                budgetRepository.update(updated)
            }
        }
    }

    fun deleteBudget(budgetId: String) {
        val index = budgets.indexOfFirst { it.id == budgetId }
        if (index != -1) {
            budgets.removeAt(index)
            viewModelScope.launch {
                budgetRepository.delete(budgetId)
            }
        }
    }
}
