package com.example.qlctcn.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.qlctcn.auth.AuthViewModel
import com.example.qlctcn.repository.*

class ViewModelFactory(
    private val userViewModel: UserViewModel? = null
) : ViewModelProvider.Factory {
    // ... Khởi tạo các repository như cũ ...
    private val userRepository = UserRepository()
    private val transactionRepository = TransactionRepository()
    private val categoryRepository = CategoryRepository()
    private val budgetRepository = BudgetRepository()

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(UserViewModel::class.java) ->
                UserViewModel(userRepository) as T
            modelClass.isAssignableFrom(AuthViewModel::class.java) ->
                AuthViewModel() as T
            modelClass.isAssignableFrom(CategoryViewModel::class.java) ->
                CategoryViewModel(categoryRepository) as T


            modelClass.isAssignableFrom(BudgetViewModel::class.java) ->
                BudgetViewModel(budgetRepository) as T

            modelClass.isAssignableFrom(TransactionViewModel::class.java) ->
                TransactionViewModel(
                    transactionRepository,
                    userViewModel ?: UserViewModel(userRepository)

                ) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}