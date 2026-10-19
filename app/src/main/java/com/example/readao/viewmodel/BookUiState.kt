package com.example.readao.viewmodel

data class BookUiState(
    val title: String = "",
    val author: String = "",
    val genre: String = "",
    val totalPages: String = "",
    val currentPage: String = "",
    val status: String = "Reading"
)