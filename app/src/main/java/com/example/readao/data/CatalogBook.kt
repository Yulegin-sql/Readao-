package com.example.readao.data

import com.google.firebase.firestore.DocumentId

data class CatalogBook(
    @DocumentId
    val bookKey: String = "",
    val title: String = "",
    val author: String = "",
    val genre: String = "",
    val totalPages: Int = 0,
    val coverUrl: String? = null,
    val addedBy: String = "",
    val addedByEmail: String = "",
    val addedAt: Long = 0L,
    val readersCount: Int = 1
)