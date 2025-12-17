package com.example.qlctcn.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.qlctcn.model.Category
import com.example.qlctcn.model.TransactionType
import com.example.qlctcn.viewmodel.BudgetViewModel
import com.example.qlctcn.viewmodel.CategoryViewModel
import com.example.qlctcn.viewmodel.ViewModelFactory
import java.time.LocalDate

private val ColorBg = Color(0xFFF2FBFF)
private val ColorHeader = Color(0xFF76B7FF)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddBudgetScreen(
    onDone: () -> Unit,
    budgetViewModel: BudgetViewModel, // Nhận từ MainScreen để dùng chung
    categoryViewModel: CategoryViewModel = viewModel(factory = ViewModelFactory())
) {
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var amount by remember { mutableStateOf("") }
    var currentMonth by remember { mutableStateOf(LocalDate.now().let { "${it.year}-${it.monthValue.toString().padStart(2, '0')}" }) }

    // Load danh mục chi tiêu
    LaunchedEffect(Unit) {
        categoryViewModel.loadCategories(TransactionType.EXPENSE)
    }
    val categories = categoryViewModel.categories

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thiết lập ngân sách ($currentMonth)", color = Color.White) },
                backgroundColor = ColorHeader,
                elevation = 0.dp,
                modifier = Modifier.statusBarsPadding()
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ColorBg)
                .padding(padding)
                .padding(16.dp)
        ) {
            // Nhập số tiền
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Hạn mức chi tiêu (VNĐ)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Chọn danh mục áp dụng:", style = MaterialTheme.typography.subtitle1)
            Spacer(modifier = Modifier.height(8.dp))

            // Danh sách Category
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedCategory = category },
                        backgroundColor = if (selectedCategory?.id == category.id) ColorHeader else Color.White,
                        elevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Category, null,
                                tint = if (selectedCategory?.id == category.id) Color.White else ColorHeader
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                category.name,
                                color = if (selectedCategory?.id == category.id) Color.White else Color.Black
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val limit = amount.toLongOrNull()
                    if (limit != null && selectedCategory != null) {
                        budgetViewModel.createOrUpdateBudget(
                            categoryId = selectedCategory!!.id,
                            categoryName = selectedCategory!!.name,
                            month = currentMonth,
                            limitAmount = limit
                        )
                        onDone()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = ColorHeader),
                enabled = selectedCategory != null && amount.isNotBlank()
            ) {
                Text("Lưu ngân sách", color = Color.White)
            }
        }
    }
}