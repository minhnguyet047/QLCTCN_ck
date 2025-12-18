package com.example.qlctcn.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qlctcn.model.Category
import com.example.qlctcn.model.TransactionType
import com.example.qlctcn.repository.CategoryRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.UUID

class CategoryViewModel(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _categories = mutableStateListOf<Category>()
    val categories: List<Category> get() = _categories

    private var loadJob: Job? = null

    fun loadCategories(type: TransactionType) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _categories.clear()
            _categories.addAll(categoryRepository.getByType(type))
        }
    }

    fun addCategory(name: String, type: TransactionType) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        // 1. TẠO ID & OBJECT NGAY TẠI ĐÂY
        val newId = UUID.randomUUID().toString()
        val newCategory = Category(
            id = newId,
            name = name,
            type = type,
            userId = userId
        )

       
        _categories.add(newCategory)

        viewModelScope.launch {
           
            categoryRepository.create(newCategory)

            
        }
    }

    fun getCategoryById(id: String): Category? {
        return _categories.find { it.id == id }
    }

    fun updateCategory(category: Category) {
        // Cập nhật list local
        val index = _categories.indexOfFirst { it.id == category.id }
        if (index != -1) {
            _categories[index] = category
        }

        viewModelScope.launch {
            categoryRepository.update(category)
        }
    }

    fun deleteCategory(category: Category) {
        // Xóa local
        _categories.remove(category)

        viewModelScope.launch {
            categoryRepository.delete(category.id)
        }
    }
}
