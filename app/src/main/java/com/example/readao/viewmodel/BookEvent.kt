package com.example.readao.viewmodel

import com.example.readao.data.Book
import com.example.readao.data.Rating

sealed class BookEvent {
    data class AddBook(val book: Book) : BookEvent()
    data class UpdateBook(val book: Book) : BookEvent()
    data class DeleteBook(val book: Book) : BookEvent()
    data class AddRating(val rating: Rating) : BookEvent()
    data class DeleteRating(val rating: Rating) : BookEvent()
}