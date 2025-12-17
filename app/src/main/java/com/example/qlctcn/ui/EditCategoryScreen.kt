package com.example.qlctcn.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.qlctcn.viewmodel.CategoryViewModel

@Composable
fun EditCategoryScreen(
    categoryId: String,
    onDone: () -> Unit,
    viewModel: CategoryViewModel
) {
    val category = viewModel.getCategoryById(categoryId)

    // Nếu không tìm thấy (ví dụ load lại app), thoát ra
    if (category == null) {
        LaunchedEffect(Unit) { onDone() }
        return
    }

    var nameInput by remember { mutableStateOf(category.name) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chỉnh sửa danh mục") },
                backgroundColor = Color(0xFF76B7FF),
                contentColor = Color.White,
                actions = {
                    // Nút Xóa
                    IconButton(onClick = {
                        viewModel.deleteCategory(category)
                        onDone()
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Xóa")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF2FBFF))
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = nameInput,
                onValueChange = { nameInput = it },
                label = { Text("Tên danh mục") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Button(
                onClick = {
                    if (nameInput.isNotBlank()) {
                        val updated = category.copy(name = nameInput.trim())
                        viewModel.updateCategory(updated)
                        onDone()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF76B7FF))
            ) {
                Text("Lưu thay đổi", color = Color.White)
            }
        }
    }
}