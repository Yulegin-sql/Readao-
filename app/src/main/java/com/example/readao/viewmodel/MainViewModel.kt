package com.example.readao.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.readao.BookTrackerApp
import com.example.readao.data.Book
import com.example.readao.data.BookWithRatings
import com.example.readao.data.Rating
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as BookTrackerApp).repository

    val books = repository.allBooks.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val booksWithRatings = repository.allBooksWithRatings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val finishedCount = repository.finishedCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val totalPages = repository.totalPages.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    fun onEvent(event: BookEvent) {
        viewModelScope.launch {
            when (event) {
                is BookEvent.AddBook -> repository.addBook(event.book)
                is BookEvent.UpdateBook -> repository.updateBook(event.book)
                is BookEvent.DeleteBook -> repository.deleteBook(event.book)
                is BookEvent.AddRating -> repository.addRating(event.rating)
                is BookEvent.DeleteRating -> repository.deleteRating(event.rating)
            }
        }
    }

    suspend fun getBookWithRatings(id: Int): BookWithRatings? =
        repository.getBookWithRatingsById(id)

    suspend fun getBookById(id: Int): Book? = repository.getBookById(id)

    fun getRatingsForBook(bookId: Int) = repository.getRatingsForBook(bookId)
    fun getAverageRating(bookId: Int) = repository.getAverageRating(bookId)
    fun getRatingsCount(bookId: Int) = repository.getRatingsCount(bookId)
}