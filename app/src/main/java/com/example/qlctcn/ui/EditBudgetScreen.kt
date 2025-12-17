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
import com.example.qlctcn.viewmodel.BudgetViewModel

@Composable
fun EditBudgetScreen(
    budgetId: String,
    onDone: () -> Unit,
    viewModel: BudgetViewModel
) {
    val budget = viewModel.getBudgetById(budgetId)

    if (budget == null) {
        LaunchedEffect(Unit) { onDone() }
        return
    }

    var limitInput by remember { mutableStateOf(budget.limitAmount.toString()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chỉnh sửa ngân sách") },
                backgroundColor = Color(0xFF76B7FF),
                contentColor = Color.White,
                actions = {
                    IconButton(onClick = {
                        viewModel.deleteBudget(budgetId)
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
            Text(
                text = "Danh mục: ${budget.categoryName}",
                style = MaterialTheme.typography.h6
            )

            OutlinedTextField(
                value = limitInput,
                onValueChange = { limitInput = it },
                label = { Text("Hạn mức (VNĐ)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Button(
                onClick = {
                    val newLimit = limitInput.toLongOrNull()
                    if (newLimit != null && newLimit > 0) {
                        viewModel.updateLimit(budgetId, newLimit)
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