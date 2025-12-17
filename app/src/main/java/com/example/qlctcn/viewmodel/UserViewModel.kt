package com.example.qlctcn.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qlctcn.auth.UserState
import com.example.qlctcn.model.User
import com.example.qlctcn.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import com.google.firebase.Timestamp


class UserViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    val user = mutableStateOf<User?>(null)
    val userState = mutableStateOf<UserState>(UserState.Loading)

    /** Gọi khi app start hoặc sau login */
    fun checkUserProfile() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        if (firebaseUser == null) {
            userState.value = UserState.NotLoggedIn
            return
        }

        viewModelScope.launch {
            val profile = userRepository.get()

            if (profile == null || profile.name.isBlank()) {
                userState.value = UserState.NeedSetup
            } else {
                user.value = profile
                userState.value = UserState.Ready
            }
        }
    }

    /** CHỈ GỌI 1 LẦN – LÚC SETUP PROFILE */
    fun createUser(uid: String, name: String, balance: Long) {
        val newUser = User(
            uid = uid,
            name = name,
            balance = balance,
            createdAt = Timestamp.now()
        )

        viewModelScope.launch {
            userRepository.create(newUser)
            user.value = newUser
            userState.value = UserState.Ready
        }
    }

    fun updateBalance(delta: Long) {
        val currentUser = user.value ?: return

        // 1. Tính toán số dư mới
        val newBalance = currentUser.balance + delta
        val updatedUser = currentUser.copy(balance = newBalance)

        // 2. CẬP NHẬT GIAO DIỆN NGAY LẬP TỨC (0 độ trễ)
        user.value = updatedUser

        // 3. Sau đó mới âm thầm gửi lên Firestore (Chạy ngầm)
        viewModelScope.launch {
            try {
                userRepository.update(updatedUser)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateName(newName: String) {
        val current = user.value ?: return
        val updated = current.copy(name = newName)
        user.value = updated

        viewModelScope.launch {
            userRepository.update(updated)
        }
    }

    fun onLogout() {
        user.value = null
        userState.value = UserState.NotLoggedIn
    }
}
