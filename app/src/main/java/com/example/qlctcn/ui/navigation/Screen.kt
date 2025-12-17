package com.example.qlctcn.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.qlctcn.model.TransactionType

sealed class Screen(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Transactions : Screen("transactions", "Giao dịch", Icons.Default.List)
    object Categories : Screen("categories", "Danh mục", Icons.Default.Folder)
    object Budgets : Screen("budgets", "Ngân sách", Icons.Default.AccountBalance)
    object Overview : Screen("overview", "Tổng quan", Icons.Default.List)

    object AddTransaction : Screen("add_transaction", "Add", Icons.Default.Add)

    object AddCategory : Screen("add_category/{type}", "AddCategory", Icons.Default.Add) {
        fun createRoute(type: TransactionType) = "add_category/${type.name}"
    }

    object AddBudget : Screen("add_budget", "AddBudget", Icons.Default.Add)


    object EditCategory : Screen("edit_category/{id}", "EditCategory", Icons.Default.Edit) {
        fun createRoute(id: String) = "edit_category/$id"
    }


    object EditBudget : Screen("edit_budget/{id}", "EditBudget", Icons.Default.Edit) {
        fun createRoute(id: String) = "edit_budget/$id"
    }
}