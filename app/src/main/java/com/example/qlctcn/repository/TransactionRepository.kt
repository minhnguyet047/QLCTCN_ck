package com.example.qlctcn.repository

import com.example.qlctcn.model.Transaction
import kotlinx.coroutines.tasks.await

class TransactionRepository : FirestoreRepository() {

    suspend fun create(transaction: Transaction) {
        val uid = uidOrNull() ?: return


        val docRef = if (transaction.id.isNotEmpty()) {
            firestore.collection("transactions").document(transaction.id)
        } else {
            firestore.collection("transactions").document()
        }

        docRef.set(
            transaction.copy(
                id = docRef.id,
                userId = uid
            )
        ).await()
    }

    suspend fun getAll(): List<Transaction> {
        val uid = uidOrNull() ?: return emptyList()

        val snapshot = firestore.collection("transactions")
            .whereEqualTo("userId", uid)
            .get()
            .await()

        return snapshot.toObjects(Transaction::class.java)
    }

    suspend fun update(transaction: Transaction) {
        firestore.collection("transactions")
            .document(transaction.id)
            .set(transaction)
            .await()
    }

    suspend fun delete(id: String) {
        firestore.collection("transactions")
            .document(id)
            .delete()
            .await()
    }
}