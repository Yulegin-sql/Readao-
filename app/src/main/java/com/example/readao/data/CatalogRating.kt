package com.example.readao.data

import com.google.firebase.firestore.DocumentId

data class CatalogRating(
    @DocumentId
    val id: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val score: Int = 0,
    val comment: String = "",
    val timestamp: Long = System.currentTimeMillis()
)