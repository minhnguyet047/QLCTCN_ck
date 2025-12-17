package com.example.qlctcn.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.qlctcn.model.Budget
import com.example.qlctcn.model.TransactionType
import com.example.qlctcn.ui.navigation.Screen
import com.example.qlctcn.viewmodel.BudgetViewModel
import com.example.qlctcn.viewmodel.TransactionViewModel
import java.time.LocalDate
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BudgetScreen(
    navController: NavController,
    viewModel: BudgetViewModel,
    transactionViewModel: TransactionViewModel
) {
    var selectedMonth by remember { mutableStateOf(currentMonth()) }


    var showDeleteDialog by remember { mutableStateOf(false) }
    var budgetToDelete by remember { mutableStateOf<Budget?>(null) }

    LaunchedEffect(selectedMonth) {
        viewModel.loadBudgets(selectedMonth)
        transactionViewModel.loadTransactions()
    }

    val budgets = viewModel.budgets
    val allTransactions = transactionViewModel.transactions

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddBudget.route) },
                backgroundColor = Color(0xFF76B7FF)
            ) { Icon(Icons.Default.Add, null, tint = Color.White) }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF2FBFF)).padding(paddingValues)) {
            // Header & Month Selector (Giữ nguyên)
            Box(modifier = Modifier.fillMaxWidth().background(Color(0xFF76B7FF)).statusBarsPadding().padding(16.dp)) {
                Text("Quản lý Ngân sách", style = MaterialTheme.typography.h6, color = Color.White)
            }
            Spacer(modifier = Modifier.height(12.dp))
            MonthSelector(selectedMonth) { selectedMonth = it }
            Spacer(modifier = Modifier.height(12.dp))

            if (budgets.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Chưa có ngân sách", color = Color.Gray) }
            } else {
                LazyColumn(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(budgets) { budget ->
                        val spentAmount = allTransactions
                            .filter { it.categoryId == budget.categoryId && it.type == TransactionType.EXPENSE && isSameMonth(it.date, selectedMonth) }
                            .sumOf { it.amount }

                        BudgetCard(
                            budget = budget,
                            spentAmount = spentAmount,
                            onEditClick = {
                                navController.navigate(Screen.EditBudget.createRoute(budget.id))
                            },

                            onDeleteClick = {
                                budgetToDelete = budget
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }
    }


    if (showDeleteDialog && budgetToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Xóa ngân sách?") },
            text = { Text("Bạn có chắc muốn xóa ngân sách cho mục '${budgetToDelete!!.categoryName}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteBudget(budgetToDelete!!.id)
                        showDeleteDialog = false
                    }
                ) { Text("Xóa", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Hủy") }
            }
        )
    }
}

@Composable
fun BudgetCard(
    budget: Budget,
    spentAmount: Long,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val remaining = budget.limitAmount - spentAmount
    val isOver = remaining < 0

    Card(modifier = Modifier.fillMaxWidth(), elevation = 4.dp) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(budget.categoryName, fontWeight = FontWeight.Bold)


                Row {
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Sửa", tint = Color(0xFF76B7FF))
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = Color.Gray)
                    }
                }
            }
            Divider()
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Hạn mức: ${budget.limitAmount} đ", color = Color.Gray)
                Text("Đã chi: $spentAmount đ", color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (isOver) {
                Text("Vượt quá: ${-remaining} đ", color = Color(0xFFFF8A80), fontWeight = FontWeight.Bold)
            } else {
                Text("Còn dư: $remaining đ", color = Color(0xFF5CC8A1), fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Helpers giữ nguyên
@RequiresApi(Build.VERSION_CODES.O)
fun isSameMonth(timestamp: com.google.firebase.Timestamp, selectedMonthStr: String): Boolean {
    val date = timestamp.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    val monthStr = "${date.year}-${date.monthValue.toString().padStart(2, '0')}"
    return monthStr == selectedMonthStr
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MonthSelector(selectedMonth: String, onMonthChange: (String) -> Unit) {
    val months = remember { (0..11).map { LocalDate.now().minusMonths(it.toLong()).let { d -> "${d.year}-${d.monthValue.toString().padStart(2, '0')}" } } }
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text("Tháng: $selectedMonth"); Spacer(Modifier.weight(1f)); Icon(Icons.Default.ArrowDropDown, null)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.fillMaxWidth()) {
            months.forEach { m -> DropdownMenuItem(onClick = { onMonthChange(m); expanded = false }) { Text(m) } }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun currentMonth(): String = LocalDate.now().let { "${it.year}-${it.monthValue.toString().padStart(2, '0')}" }