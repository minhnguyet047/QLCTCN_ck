package com.example.qlctcn.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.qlctcn.model.TransactionType
import com.example.qlctcn.ui.navigation.BottomNavigationBar
import com.example.qlctcn.ui.navigation.Screen
import com.example.qlctcn.viewmodel.BudgetViewModel
import com.example.qlctcn.viewmodel.CategoryViewModel
import com.example.qlctcn.viewmodel.TransactionViewModel
import com.example.qlctcn.viewmodel.UserViewModel
import com.example.qlctcn.viewmodel.ViewModelFactory

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(
    userViewModel: UserViewModel,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val budgetViewModel: BudgetViewModel = viewModel(factory = ViewModelFactory())
    val categoryViewModel: CategoryViewModel = viewModel(factory = ViewModelFactory())
    // TransactionViewModel cần userViewModel để cập nhật số dư
    val transactionViewModel: TransactionViewModel = viewModel(factory = ViewModelFactory(userViewModel))

    LaunchedEffect(Unit) { transactionViewModel.loadTransactions() }

    val items = listOf(Screen.Home, Screen.Transactions, Screen.Categories, Screen.Budgets, Screen.Overview)

    Scaffold(
        bottomBar = { BottomNavigationBar(navController, items) }
    ) { paddingValues ->
        NavHost(navController = navController, startDestination = Screen.Home.route, modifier = Modifier.padding(paddingValues)) {

            // Các màn hình chính - Truyền Shared ViewModel
            composable(Screen.Home.route) {
                HomeScreen(userViewModel, transactionViewModel, onLogout)
            }
            composable(Screen.Transactions.route) {
                TransactionListScreen(
                    navController = navController,
                    onAddClick = { navController.navigate(Screen.AddTransaction.route) },
                    transactionViewModel = transactionViewModel 
                )
            }
            composable(Screen.Categories.route) {
                CategoryScreen(navController, categoryViewModel)
            }
            composable(Screen.Budgets.route) {
                BudgetScreen(navController, budgetViewModel, transactionViewModel)
            }
            composable(Screen.Overview.route) {
                OverviewScreen(userViewModel, transactionViewModel)
            }

            // Các màn hình chức năng - Truyền Shared ViewModel
            composable(Screen.AddTransaction.route) {
                AddTransactionScreen(
                    onDone = { navController.popBackStack() },
                    categoryViewModel = categoryViewModel,       
                    transactionViewModel = transactionViewModel 
                )
            }

            composable(
                Screen.AddCategory.route,
                arguments = listOf(navArgument("type") { type = NavType.StringType })
            ) {
                val type = TransactionType.valueOf(it.arguments?.getString("type")!!)
                AddCategoryScreen(type, { navController.popBackStack() }, categoryViewModel)
            }

            composable(Screen.AddBudget.route) {
                AddBudgetScreen({ navController.popBackStack() }, budgetViewModel, categoryViewModel)
            }

            composable("edit_transaction/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: return@composable
                EditTransactionScreen(
                    transactionId = id,
                    onDone = { navController.popBackStack() },
                    categoryViewModel = categoryViewModel,
                    transactionViewModel = transactionViewModel
                )
            }

            composable("edit_category/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: return@composable
                EditCategoryScreen(
                    categoryId = id,
                    onDone = { navController.popBackStack() },
                    viewModel = categoryViewModel
                )
            }

            composable("edit_budget/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: return@composable
                EditBudgetScreen(
                    budgetId = id,
                    onDone = { navController.popBackStack() },
                    viewModel = budgetViewModel
                )
            }
        }
    }
}
