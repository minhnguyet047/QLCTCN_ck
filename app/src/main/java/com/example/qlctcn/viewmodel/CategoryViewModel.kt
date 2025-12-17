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

    // Biến để quản lý job load, giúp hủy job cũ nếu gọi liên tục (tránh race condition)
    private var loadJob: Job? = null

    fun loadCategories(type: TransactionType) {
        // Hủy job cũ đang chạy dở (nếu có) để tránh 2 job cùng đổ dữ liệu vào list
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

        // 2. CẬP NHẬT UI NGAY LẬP TỨC (Optimistic)
        _categories.add(newCategory)

        viewModelScope.launch {
            // 3. GỬI LÊN SERVER (Background)
            categoryRepository.create(newCategory)

            // ✅ QUAN TRỌNG: KHÔNG GỌI loadCategories(type) Ở ĐÂY NỮA
            // Vì ta đã thêm thủ công ở bước 2 rồi. Gọi lại sẽ gây trùng lặp.
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