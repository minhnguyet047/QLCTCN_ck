package com.example.qlctcn.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.qlctcn.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SetupProfileScreen(
    userViewModel: UserViewModel
) {
    var name by remember { mutableStateOf("") }
    var balance by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Thiết lập hồ sơ", style = MaterialTheme.typography.h6)

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Tên của bạn") }
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = balance,
            onValueChange = { balance = it },
            label = { Text("Số dư ban đầu") }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@Button
                if (name.isBlank()) return@Button

                userViewModel.createUser(
                    uid = uid,
                    name = name,
                    balance = balance.toLongOrNull() ?: 0L
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Xác nhận")
        }
    }
}
