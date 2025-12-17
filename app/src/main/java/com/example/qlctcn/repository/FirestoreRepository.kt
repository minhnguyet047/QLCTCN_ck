package com.example.qlctcn.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

open class FirestoreRepository {

    protected val firestore: FirebaseFirestore =
        FirebaseFirestore.getInstance()

    protected val auth: FirebaseAuth =
        FirebaseAuth.getInstance()

    protected fun uidOrNull(): String? {
        return auth.currentUser?.uid
    }
}
