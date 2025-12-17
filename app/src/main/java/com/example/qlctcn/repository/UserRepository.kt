package com.example.qlctcn.repository

import com.example.qlctcn.model.User
import kotlinx.coroutines.tasks.await

class UserRepository : FirestoreRepository() {

    suspend fun create(user: User) {
        val uid = uidOrNull() ?: return

        firestore.collection("users")
            .document(uid)
            .set(user)
            .await()
    }

    suspend fun get(): User? {
        val uid = uidOrNull() ?: return null

        val doc = firestore.collection("users")
            .document(uid)
            .get()
            .await()

        return doc.toObject(User::class.java)
    }

    suspend fun update(user: User) {
        val uid = uidOrNull() ?: return

        firestore.collection("users")
            .document(uid)
            .set(user)
            .await()
    }

    suspend fun delete() {
        val uid = uidOrNull() ?: return

        firestore.collection("users")
            .document(uid)
            .delete()
            .await()
    }
}
