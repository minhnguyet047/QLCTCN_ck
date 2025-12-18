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
import com.example.qlctcn.model.Category
import com.example.qlctcn.model.TransactionType
import com.example.qlctcn.viewmodel.CategoryViewModel
import com.example.qlctcn.viewmodel.TransactionViewModel

private val ColorBg = Color(0xFFF2FBFF)
private val ColorIncome = Color(0xFF5CC8A1)
private val ColorExpense = Color(0xFFFF8A80)
private val ColorHeader = Color(0xFF76B7FF)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditTransactionScreen(
    transactionId: String,
    onDone: () -> Unit,

    categoryViewModel: CategoryViewModel,
    transactionViewModel: TransactionViewModel
) {
    val oldTransaction =
        transactionViewModel.getTransactionById(transactionId)

    if (oldTransaction == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    // ... (Phần logic State và UI bên dưới giữ nguyên) ...
    var selectedType by remember { mutableStateOf(oldTransaction.type) }
    var selectedCategory by remember {
        mutableStateOf<Category?>(
            Category(
                id = oldTransaction.categoryId,
                name = oldTransaction.categoryName,
                type = oldTransaction.type
            )
        )
    }
    var amount by remember { mutableStateOf(oldTransaction.amount.toString()) }
    var note by remember { mutableStateOf(oldTransaction.note) }

    LaunchedEffect(selectedType) {
        categoryViewModel.loadCategories(selectedType)
        if (selectedType != oldTransaction.type) {
            selectedCategory = null
        }
    }

    val categories = categoryViewModel.categories
    val activeColor =
        if (selectedType == TransactionType.EXPENSE) ColorExpense else ColorIncome

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBg)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(ColorHeader)
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            Text("Chỉnh sửa giao dịch", color = Color.White)
        }

        Column(modifier = Modifier.padding(16.dp)) {

            Text("Danh mục")
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.heightIn(max = 200.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedCategory = category },
                        backgroundColor =
                            if (selectedCategory?.id == category.id)
                                activeColor else Color.White
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Category,
                                contentDescription = null,
                                tint = if (selectedCategory?.id == category.id)
                                    Color.White else activeColor
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(category.name)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Số tiền") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Ghi chú") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val money = amount.toLongOrNull() ?: return@Button
                    val category = selectedCategory ?: return@Button

                    val updated = oldTransaction.copy(
                        amount = money,
                        categoryId = category.id,
                        categoryName = category.name,
                        type = selectedType,
                        note = note
                    )

                    transactionViewModel.updateTransaction(
                        old = oldTransaction,
                        updated = updated
                    )

                    onDone()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(backgroundColor = activeColor)
            ) {
                Text("Lưu thay đổi", color = Color.White)
            }
        }
    }
}
