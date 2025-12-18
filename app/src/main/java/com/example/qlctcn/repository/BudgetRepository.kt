package com.example.qlctcn.repository

import com.example.qlctcn.model.Budget
import kotlinx.coroutines.tasks.await

class BudgetRepository : FirestoreRepository() {

    suspend fun create(budget: Budget) {
        val uid = uidOrNull() ?: return

        val docRef = if (budget.id.isNotEmpty()) {
            firestore.collection("budgets").document(budget.id)
        } else {
            firestore.collection("budgets").document()
        }

        docRef.set(
            budget.copy(
                id = docRef.id,
                userId = uid
            )
        ).await()
    }

    suspend fun getByMonth(month: String): List<Budget> {
        val uid = uidOrNull() ?: return emptyList()

        val snapshot = firestore.collection("budgets")
            .whereEqualTo("userId", uid)
            .whereEqualTo("month", month)
            .get()
            .await()

        return snapshot.toObjects(Budget::class.java)
    }

    suspend fun getByCategoryAndMonth(
        categoryId: String,
        month: String
    ): Budget? {
        val uid = uidOrNull() ?: return null

        val snapshot = firestore.collection("budgets")
            .whereEqualTo("userId", uid)
            .whereEqualTo("categoryId", categoryId)
            .whereEqualTo("month", month)
            .get()
            .await()

        return snapshot.documents.firstOrNull()
            ?.toObject(Budget::class.java)
    }

    suspend fun update(budget: Budget) {
        firestore.collection("budgets")
            .document(budget.id)
            .set(budget)
            .await()
    }

    suspend fun delete(id: String) {
        firestore.collection("budgets")
            .document(id)
            .delete()
            .await()
    }
}
