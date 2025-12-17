package com.example.qlctcn.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.qlctcn.model.Category
import com.example.qlctcn.model.TransactionType
import com.example.qlctcn.ui.navigation.Screen
import com.example.qlctcn.viewmodel.CategoryViewModel

@Composable
fun CategoryScreen(
    navController: NavController,
    viewModel: CategoryViewModel
) {
    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }

    //  STATE CHO DIALOG XÓA
    var showDeleteDialog by remember { mutableStateOf(false) }
    var categoryToDelete by remember { mutableStateOf<Category?>(null) }

    LaunchedEffect(selectedType) {
        viewModel.loadCategories(selectedType)
    }

    val categories = viewModel.categories
    val activeColor = if (selectedType == TransactionType.EXPENSE) Color(0xFFFF8A80) else Color(0xFF5CC8A1)

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF2FBFF))) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header & Tabs (Giữ nguyên)
            Box(modifier = Modifier.fillMaxWidth().background(Color(0xFF76B7FF)).statusBarsPadding().padding(16.dp)) {
                Text("Danh sách danh mục", style = MaterialTheme.typography.h6, color = Color.White)
            }
            TabRow(selectedTabIndex = if (selectedType == TransactionType.EXPENSE) 0 else 1, backgroundColor = Color.White, contentColor = activeColor) {
                Tab(selected = selectedType == TransactionType.EXPENSE, onClick = { selectedType = TransactionType.EXPENSE; selectedCategoryId = null }, text = { Text("Chi tiêu") })
                Tab(selected = selectedType == TransactionType.INCOME, onClick = { selectedType = TransactionType.INCOME; selectedCategoryId = null }, text = { Text("Thu nhập") })
            }
            Spacer(modifier = Modifier.height(12.dp))

            // List
            LazyColumn(
                modifier = Modifier.padding(16.dp).weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(categories) { category ->
                    CategoryCard(
                        category = category,
                        isSelected = category.id == selectedCategoryId,
                        backgroundColor = activeColor,
                        onSelect = { selectedCategoryId = category.id },
                        onEdit = {
                            navController.navigate(Screen.EditCategory.createRoute(category.id))
                        },

                        onDelete = {
                            categoryToDelete = category
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }

        // FAB (Giữ nguyên)
        FloatingActionButton(
            onClick = { navController.navigate(Screen.AddCategory.createRoute(selectedType)) },
            backgroundColor = activeColor,
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) { Icon(Icons.Default.Add, null, tint = Color.White) }
    }


    if (showDeleteDialog && categoryToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Xóa danh mục?") },
            text = { Text("Bạn có chắc muốn xóa '${categoryToDelete!!.name}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteCategory(categoryToDelete!!)
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
fun CategoryCard(
    category: Category,
    isSelected: Boolean,
    backgroundColor: Color,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onSelect() },
        elevation = if (isSelected) 8.dp else 2.dp,
        backgroundColor = if (isSelected) backgroundColor else Color.White
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Category, null, tint = if (isSelected) Color.White else backgroundColor)
                Spacer(modifier = Modifier.width(12.dp))
                Text(category.name, color = if (isSelected) Color.White else Color.Black)
            }


            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, null, tint = if (isSelected) Color.White else Color.Gray)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, null, tint = if (isSelected) Color.White else Color.Gray)
                }
            }
        }
    }
}