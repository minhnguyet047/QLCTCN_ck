package com.example.qlctcn.repository

import com.example.qlctcn.model.Category
import com.example.qlctcn.model.TransactionType
import kotlinx.coroutines.tasks.await

class CategoryRepository : FirestoreRepository() {

    suspend fun create(category: Category) {
        val uid = uidOrNull() ?: return


        val docRef = if (category.id.isNotEmpty()) {
            firestore.collection("categories").document(category.id)
        } else {
            firestore.collection("categories").document()
        }

        docRef.set(
            category.copy(
                id = docRef.id,
                userId = uid
            )
        ).await()
    }

    suspend fun getByType(type: TransactionType): List<Category> {
        val uid = uidOrNull() ?: return emptyList()

        val snapshot = firestore.collection("categories")
            .whereEqualTo("userId", uid)
            .whereEqualTo("type", type)
            .get()
            .await()

        return snapshot.toObjects(Category::class.java)
    }

    suspend fun update(category: Category) {
        firestore.collection("categories")
            .document(category.id)
            .set(category)
            .await()
    }

    suspend fun delete(id: String) {
        firestore.collection("categories")
            .document(id)
            .delete()
            .await()
    }
}