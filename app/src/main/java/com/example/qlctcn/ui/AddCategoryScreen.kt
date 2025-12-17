package com.example.qlctcn.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.qlctcn.model.TransactionType
import com.example.qlctcn.viewmodel.CategoryViewModel
import com.example.qlctcn.viewmodel.ViewModelFactory
import kotlinx.coroutines.delay

@Composable
fun AddCategoryScreen(
    type: TransactionType,
    onDone: () -> Unit,
    viewModel: CategoryViewModel = viewModel(factory = ViewModelFactory())
) {
    var name by rememberSaveable { mutableStateOf("") }


    var selectedType by rememberSaveable { mutableStateOf(type) }

    var shouldClose by remember { mutableStateOf(false) }


    LaunchedEffect(shouldClose) {
        if (shouldClose) {
            delay(100)
            onDone()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Thêm danh mục")
                },
                elevation = 4.dp,
                modifier = Modifier.statusBarsPadding()
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ===== TÊN DANH MỤC =====
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Tên danh mục") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // ===== CHỌN LOẠI =====
            Text(
                text = "Loại danh mục",
                style = MaterialTheme.typography.subtitle2
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedType == TransactionType.EXPENSE,
                        onClick = {
                            selectedType = TransactionType.EXPENSE
                        }
                    )
                    Text("Chi tiêu")
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedType == TransactionType.INCOME,
                        onClick = {
                            selectedType = TransactionType.INCOME
                        }
                    )
                    Text("Thu nhập")
                }
            }

            // ===== BUTTONS =====
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {

                TextButton(
                    onClick = { shouldClose = true }
                ) {
                    Text("Hủy")
                }

                Spacer(modifier = Modifier.width(8.dp))

                TextButton(
                    onClick = {
                        if (name.isNotBlank()) {
                            viewModel.addCategory(
                                name.trim(),
                                selectedType
                            )
                            shouldClose = true
                        }
                    }
                ) {
                    Text("Thêm")
                }
            }
        }
    }
}
