package com.example.qlctcn

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.qlctcn.ui.RootScreen
import com.example.qlctcn.ui.theme.QLCTCNTheme
import com.example.qlctcn.viewmodel.UserViewModel
import com.example.qlctcn.viewmodel.ViewModelFactory
import com.example.qlctcn.auth.AuthViewModel

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            QLCTCNTheme {

                val userViewModel: UserViewModel =
                    viewModel(factory = ViewModelFactory())

                val authViewModel: AuthViewModel =
                    viewModel(factory = ViewModelFactory())

                RootScreen(
                    userViewModel = userViewModel,
                    authViewModel = authViewModel
                )
            }
        }
    }
}
