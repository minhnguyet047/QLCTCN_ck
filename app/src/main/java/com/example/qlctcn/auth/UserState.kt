package com.example.qlctcn.auth

sealed class UserState {
    object Loading : UserState()
    object NotLoggedIn : UserState()
    object NeedSetup : UserState()
    object Ready : UserState()
}