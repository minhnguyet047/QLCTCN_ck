package com.example.qlctcn.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.qlctcn.model.TransactionType
import com.example.qlctcn.viewmodel.TransactionViewModel
import com.example.qlctcn.viewmodel.UserViewModel
import com.example.qlctcn.viewmodel.ViewModelFactory

// ===== COLOR PALETTE =====
private val ColorBg = Color(0xFFF2FBFF)
private val ColorHeader = Color(0xFF76B7FF)
private val ColorIncome = Color(0xFF5CC8A1)
private val ColorExpense = Color(0xFFFF8A80)
private val ColorCard = Color.White

@Composable
fun OverviewScreen(
    userViewModel: UserViewModel,
    transactionViewModel: TransactionViewModel
) {

    // ===== LOAD DATA =====
    LaunchedEffect(Unit) {
        transactionViewModel.loadTransactions()
    }

    val transactions = transactionViewModel.transactions

    val balance = userViewModel.user.value?.balance ?: 0L

    // Thu chi lấy từ TransactionViewModel (đã tối ưu)
    val totalIncome = transactionViewModel.uiIncome
    val totalExpense = transactionViewModel.uiExpense

    val incomeCount = transactions.count { it.type == TransactionType.INCOME }
    val expenseCount = transactions.count { it.type == TransactionType.EXPENSE }

    val topExpenseCategory =
        transactions
            .filter { it.type == TransactionType.EXPENSE }
            .groupBy { it.categoryName }
            .maxByOrNull { it.value.sumOf { tx -> tx.amount } }
            ?.key ?: "—"

    val topIncomeCategory =
        transactions
            .filter { it.type == TransactionType.INCOME }
            .groupBy { it.categoryName }
            .maxByOrNull { it.value.sumOf { tx -> tx.amount } }
            ?.key ?: "—"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBg)
    ) {

        // ===== HEADER =====
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(ColorHeader)
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            Text(
                text = "Tổng quan",
                style = MaterialTheme.typography.h6,
                color = Color.White
            )
        }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ===== SỐ DƯ =====
            SummaryCard(
                title = "Số dư hiện tại",
                value = "$balance đ",
                icon = Icons.Default.Wallet,
                backgroundColor = ColorHeader
            )

            // ===== TỔNG THU / CHI =====
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryCard(
                    modifier = Modifier.weight(1f),
                    title = "Tổng thu",
                    value = "+$totalIncome đ",
                    icon = Icons.Default.ArrowUpward,
                    backgroundColor = ColorIncome
                )

                SummaryCard(
                    modifier = Modifier.weight(1f),
                    title = "Tổng chi",
                    value = "-$totalExpense đ",
                    icon = Icons.Default.ArrowDownward,
                    backgroundColor = ColorExpense
                )
            }

            // ===== THỐNG KÊ =====
            Text(
                text = "Thống kê",
                style = MaterialTheme.typography.subtitle1
            )

            StatCard(title = "Số giao dịch thu", value = incomeCount.toString())
            StatCard(title = "Số giao dịch chi", value = expenseCount.toString())
            StatCard(title = "Danh mục chi nhiều nhất", value = topExpenseCategory)
            StatCard(title = "Danh mục thu nhiều nhất", value = topIncomeCategory)
        }
    }
}

// ... (Các hàm SummaryCard và StatCard giữ nguyên) ...
@Composable
fun SummaryCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    backgroundColor: Color
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = backgroundColor,
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, color = Color.White, style = MaterialTheme.typography.subtitle1)
                Text(text = value, color = Color.White, style = MaterialTheme.typography.h6)
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String) {
    Card(elevation = 3.dp, backgroundColor = ColorCard, modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.List, contentDescription = null, tint = ColorHeader)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.body2)
                Text(text = value, style = MaterialTheme.typography.subtitle1)
            }
        }
    }
}
