package com.example.qlctcn.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.qlctcn.viewmodel.TransactionViewModel
import com.example.qlctcn.viewmodel.UserViewModel

// ===== COLOR PALETTE =====
private val ColorHeader = Color(0xFF76B7FF)
private val ColorBg = Color(0xFFF2FBFF)
private val ColorTextDark = Color(0xFF1C2A3A)
private val ColorIncome = Color(0xFF5CC8A1)
private val ColorExpense = Color(0xFFFF8A80)

@Composable
fun HomeScreen(
    userViewModel: UserViewModel,
    transactionViewModel: TransactionViewModel,
    onLogout: () -> Unit
) {
    val user = userViewModel.user.value

    // ✅ SỬA ĐỔI: Lấy số dư trực tiếp từ UserViewModel (Nguồn tin cậy nhất)
    // TransactionViewModel.addTransaction sẽ update UserViewModel, và biến này sẽ tự động thay đổi ngay lập tức
    val displayBalance = user?.balance ?: 0L

    // Tổng thu/chi lấy từ TransactionViewModel (đã được tối ưu tốc độ)
    val totalIncome = transactionViewModel.uiIncome
    val totalExpense = transactionViewModel.uiExpense

    var showEditNameDialog by remember { mutableStateOf(false) }
    var nameInput by remember { mutableStateOf("") }
    var showLogoutDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(ColorBg)) {

        // HEADER
        Box(
            modifier = Modifier.fillMaxWidth().background(ColorHeader).statusBarsPadding().padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Xin chào", color = Color.White)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(user?.name ?: "Người dùng", color = Color.White, style = MaterialTheme.typography.h6)
                        IconButton(onClick = { nameInput = user?.name.orEmpty(); showEditNameDialog = true }) { Icon(Icons.Default.Edit, null, tint = Color.White) }
                        IconButton(onClick = { showLogoutDialog = true }) { Icon(Icons.Default.Logout, null, tint = Color.White) }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ===== SỐ DƯ (BALANCE) =====
        Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), elevation = 6.dp) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccountBalanceWallet, null, tint = ColorTextDark, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Số dư hiện tại")
                    // ✅ Hiển thị số dư chuẩn từ User
                    Text(
                        text = "$displayBalance đ",
                        style = MaterialTheme.typography.h4
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ===== TỔNG QUAN THU CHI =====
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SummaryCard(Modifier.weight(1f), "Tổng thu", totalIncome, Icons.Default.ArrowUpward, ColorIncome)
            SummaryCard(Modifier.weight(1f), "Tổng chi", totalExpense, Icons.Default.ArrowDownward, ColorExpense)
        }
    }

    if (showEditNameDialog) {
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            title = { Text("Sửa tên") },
            text = { OutlinedTextField(value = nameInput, onValueChange = { nameInput = it }) },
            confirmButton = { TextButton(onClick = { if (nameInput.isNotBlank()) userViewModel.updateName(nameInput); showEditNameDialog = false }) { Text("Lưu") } },
            dismissButton = { TextButton(onClick = { showEditNameDialog = false }) { Text("Hủy") } }
        )
    }
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Đăng xuất") },
            text = { Text("Bạn có chắc chắn muốn đăng xuất?") },
            confirmButton = { TextButton(onClick = { showLogoutDialog = false; onLogout() }) { Text("Đăng xuất", color = Color.Red) } },
            dismissButton = { TextButton(onClick = { showLogoutDialog = false }) { Text("Hủy") } }
        )
    }
}

@Composable
fun SummaryCard(modifier: Modifier, title: String, amount: Long, icon: ImageVector, backgroundColor: Color) {
    Card(modifier = modifier, backgroundColor = backgroundColor) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = Color.White)
            Text(title, color = Color.White)
            Text("$amount đ", color = Color.White)
        }
    }
}