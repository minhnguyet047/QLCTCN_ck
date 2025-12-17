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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.qlctcn.viewmodel.TransactionViewModel
import androidx.compose.runtime.saveable.rememberSaveable
import java.util.*
import com.example.qlctcn.model.Transaction
import com.example.qlctcn.model.TransactionType
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import java.text.SimpleDateFormat

private val ColorBg = Color(0xFFF2FBFF)
private val ColorIncome = Color(0xFF5CC8A1)
private val ColorExpense = Color(0xFFFF8A80)
private val ColorHeader = Color(0xFF76B7FF)

private enum class FilterType { ALL, MONTH, YEAR }

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionListScreen(
    navController: NavController,
    onAddClick: () -> Unit,
    // ✅ Nhận Shared ViewModel
    transactionViewModel: TransactionViewModel
) {
    // ===== LOAD DATA =====
    LaunchedEffect(Unit) {
        transactionViewModel.loadTransactions()
    }

    val transactions = transactionViewModel.transactions
    var selectedFilter by rememberSaveable { mutableStateOf(FilterType.ALL) }

    val filteredTransactions by remember {
        derivedStateOf {
            when (selectedFilter) {
                FilterType.ALL -> transactions
                FilterType.MONTH -> transactions.filter { isSameMonth(it.date.toDate()) }
                FilterType.YEAR -> transactions.filter { isSameYear(it.date.toDate()) }
            }.sortedByDescending { it.date }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                backgroundColor = ColorHeader
            ) {
                Icon(Icons.Default.Add, "Thêm giao dịch", tint = Color.White)
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ColorBg)
                .padding(padding)
        ) {

            // ===== HEADER =====
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ColorHeader)
                    .statusBarsPadding()
                    .padding(16.dp)
            ) {
                Text("Danh sách giao dịch", style = MaterialTheme.typography.h6, color = Color.White)
            }

            // ===== TAB FILTER =====
            TabRow(selectedTabIndex = selectedFilter.ordinal, backgroundColor = Color.White) {
                FilterType.values().forEach { filter ->
                    Tab(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        text = {
                            Text(when (filter) {
                                FilterType.ALL -> "Tất cả"
                                FilterType.MONTH -> "Tháng"
                                FilterType.YEAR -> "Năm"
                            })
                        }
                    )
                }
            }

            // ===== LIST =====
            if (filteredTransactions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Chưa có giao dịch nào", color = Color.Gray)
                }
            } else {
                LazyColumn(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(filteredTransactions) { transaction ->
                        TransactionCard(
                            transaction = transaction,
                            onDelete = { transactionViewModel.deleteTransaction(transaction) },
                            onEdit = { navController.navigate("edit_transaction/${transaction.id}") }
                        )
                    }
                }
            }
        }
    }
}

// ... (Giữ nguyên phần Helpers và TransactionCard) ...
@Composable
fun TransactionCard(transaction: Transaction, onDelete: () -> Unit, onEdit: () -> Unit) {
    val isExpense = transaction.type == TransactionType.EXPENSE
    val bgColor = if (isExpense) ColorExpense else ColorIncome
    val icon = if (isExpense) Icons.Default.TrendingDown else Icons.Default.TrendingUp

    Card(modifier = Modifier.fillMaxWidth(), backgroundColor = bgColor, elevation = 4.dp) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(transaction.categoryName, style = MaterialTheme.typography.subtitle1, color = Color.White)
                Text(formatDate(transaction.date.toDate()), style = MaterialTheme.typography.caption, color = Color.White.copy(alpha = 0.9f))
                if (transaction.note.isNotBlank()) Text(transaction.note, style = MaterialTheme.typography.body2, color = Color.White)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(if (isExpense) "-${transaction.amount} đ" else "+${transaction.amount} đ", style = MaterialTheme.typography.h6, color = Color.White)
                Row {
                    IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, "Xóa", tint = Color.White) }
                    IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, "Sửa", tint = Color.White) }
                }
            }
        }
    }
}

private fun isSameMonth(date: Date): Boolean {
    val now = Calendar.getInstance()
    val cal = Calendar.getInstance().apply { time = date }
    return now.get(Calendar.YEAR) == cal.get(Calendar.YEAR) && now.get(Calendar.MONTH) == cal.get(Calendar.MONTH)
}

private fun isSameYear(date: Date): Boolean {
    val now = Calendar.getInstance()
    val cal = Calendar.getInstance().apply { time = date }
    return now.get(Calendar.YEAR) == cal.get(Calendar.YEAR)
}

private fun formatDate(date: Date): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(date)
}