package com.example.qlctcn.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.*
import androidx.compose.runtime.*
import com.example.qlctcn.auth.AuthViewModel
import com.example.qlctcn.auth.LoginScreen
import com.example.qlctcn.auth.RegisterScreen
import com.example.qlctcn.auth.SetupProfileScreen
import com.example.qlctcn.auth.UserState
import com.example.qlctcn.viewmodel.UserViewModel
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Box

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RootScreen(
    userViewModel: UserViewModel,
    authViewModel: AuthViewModel
) {
    val userState by userViewModel.userState

    // State cục bộ để quản lý việc đang xem màn hình Đăng nhập hay Đăng ký
    var currentAuthScreen by remember { mutableStateOf("LOGIN") }

    LaunchedEffect(Unit) {
        userViewModel.checkUserProfile()
    }

    when (userState) {
        UserState.Loading -> {
            Box(
                modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        UserState.NotLoggedIn -> {
            if (currentAuthScreen == "LOGIN") {
                LoginScreen(
                    authViewModel = authViewModel,
                    onLoginSuccess = { userViewModel.checkUserProfile() },
                    onNavigateToRegister = { currentAuthScreen = "REGISTER" }
                )
            } else {
                RegisterScreen(
                    authViewModel = authViewModel,
                    onRegisterSuccess = {

                        authViewModel.logout()
                        currentAuthScreen = "LOGIN"
                    },
                    onNavigateToLogin = { currentAuthScreen = "LOGIN" }
                )
            }
        }

        UserState.NeedSetup -> {
            SetupProfileScreen(userViewModel)
        }

        UserState.Ready -> {
            MainScreen(
                userViewModel = userViewModel,
                onLogout = {
                    authViewModel.logout()
                    userViewModel.onLogout()
                    currentAuthScreen = "LOGIN" // Reset về màn hình Login khi đăng xuất
                }
            )
        }
    }
}