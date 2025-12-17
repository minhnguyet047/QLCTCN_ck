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
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import kotlinx.coroutines.delay

private val ColorBg = Color(0xFFF2FBFF)
private val ColorIncome = Color(0xFF5CC8A1)
private val ColorExpense = Color(0xFFFF8A80)
private val ColorHeader = Color(0xFF76B7FF)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddTransactionScreen(
    onDone: () -> Unit,
    // ✅ Bỏ giá trị mặc định để ép buộc dùng ViewModel từ MainScreen
    categoryViewModel: CategoryViewModel,
    transactionViewModel: TransactionViewModel
) {

    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    var shouldClose by remember { mutableStateOf(false) }

    LaunchedEffect(shouldClose) {
        if (shouldClose) {
            delay(100)
            onDone()
        }
    }

    LaunchedEffect(selectedType) {
        categoryViewModel.loadCategories(selectedType)
        selectedCategory = null
    }

    val categories = categoryViewModel.categories
    val activeColor = if (selectedType == TransactionType.EXPENSE) ColorExpense else ColorIncome

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBg)
    ) {
        // ... (Phần UI giữ nguyên không thay đổi) ...
        // ===== HEADER =====
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(ColorHeader)
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            Text(
                text = "Thêm giao dịch",
                style = MaterialTheme.typography.h6,
                color = Color.White
            )
        }

        // ===== TAB THU / CHI =====
        TabRow(
            selectedTabIndex = if (selectedType == TransactionType.EXPENSE) 0 else 1,
            backgroundColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(
                        tabPositions[if (selectedType == TransactionType.EXPENSE) 0 else 1]
                    ),
                    color = activeColor
                )
            }
        ) {
            Tab(
                selected = selectedType == TransactionType.EXPENSE,
                onClick = { selectedType = TransactionType.EXPENSE },
                text = { Text("Chi tiêu") }
            )
            Tab(
                selected = selectedType == TransactionType.INCOME,
                onClick = { selectedType = TransactionType.INCOME },
                text = { Text("Thu nhập") }
            )
        }

        // ===== FORM =====
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            // ===== DANH MỤC =====
            Text("Danh mục", style = MaterialTheme.typography.subtitle1)
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
                                activeColor else Color.White,
                        elevation = 4.dp
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
                            Text(
                                text = category.name,
                                color = if (selectedCategory?.id == category.id)
                                    Color.White else Color.Black
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ===== SỐ TIỀN =====
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Số tiền") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ===== GHI CHÚ =====
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Ghi chú") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ===== LƯU =====
            Button(
                onClick = {
                    val money = amount.toLongOrNull() ?: return@Button
                    val category = selectedCategory ?: return@Button

                    // Gọi hàm này trên Shared ViewModel sẽ cập nhật UI Balance ngay lập tức
                    transactionViewModel.addTransaction(
                        amount = money,
                        category = category,
                        type = selectedType,
                        note = note
                    )

                    shouldClose = true
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(backgroundColor = activeColor)
            ) {
                Text("Lưu giao dịch", color = Color.White)
            }
        }
    }
}