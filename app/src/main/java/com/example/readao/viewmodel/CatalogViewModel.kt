package com.example.readao.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.readao.data.CatalogBook
import com.example.readao.data.CatalogRating
import com.example.readao.data.CatalogRepository
import com.example.readao.data.Book
import com.example.readao.data.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CatalogViewModel(application: Application) : AndroidViewModel(application) {

    private val catalogRepository = CatalogRepository(application)
    private val userPrefs = UserPreferences(application)

    // Реактивный поток книг из каталога
    val catalogBooks = catalogRepository.getCatalogBooks().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Имя и email пользователя (для добавления книги)
    val userName: String
        get() = userPrefs.userName

    val userEmail: String
        get() = userPrefs.userEmail

    val isAdmin: Boolean
        get() = userPrefs.isAdmin

    // ============================================================
    // ПОДЕЛИТЬСЯ КНИГОЙ В КАТАЛОГЕ
    // ============================================================
    fun shareBook(book: Book, coverUrl: String? = null) {
        viewModelScope.launch {
            catalogRepository.shareBookToCatalog(
                book = book,
                userName = userPrefs.userName,
                userEmail = userPrefs.userEmail,
                coverUrl = coverUrl
            )
        }
    }

    // ============================================================
    // УДАЛИТЬ ИЗ КАТАЛОГА (только для админа)
    // ============================================================
    fun deleteFromCatalog(bookKey: String) {
        viewModelScope.launch {
            catalogRepository.deleteFromCatalog(bookKey)
        }
    }

    // ============================================================
    // ПРОВЕРИТЬ, АДМИН ЛИ ПОЛЬЗОВАТЕЛЬ
    // ============================================================
    fun refreshAdminStatus() {
        viewModelScope.launch {
            val isAdminNow = catalogRepository.checkIsAdmin(userPrefs.userEmail)
            userPrefs.isAdmin = isAdminNow
        }
    }

    // ============================================================
    // ОТЗЫВЫ
    // ============================================================
    fun getRatingsForBook(bookKey: String) =
        catalogRepository.getRatingsForBook(bookKey)

    fun addRating(bookKey: String, score: Int, comment: String) {
        viewModelScope.launch {
            val rating = CatalogRating(
                userName = userPrefs.userName,
                userEmail = userPrefs.userEmail,
                score = score,
                comment = comment
            )
            catalogRepository.addRating(bookKey, rating)
        }
    }

    fun deleteRating(bookKey: String, ratingId: String) {
        viewModelScope.launch {
            catalogRepository.deleteRating(bookKey, ratingId)
        }
    }

    // ============================================================
    // Утилита для получения ключа книги
    // ============================================================
    fun makeBookKey(author: String, title: String): String {
        return CatalogRepository.makeKey(author, title)
    }

    // Для передачи списка CatalogBook
    fun filterCatalogBook(bookKey: String): CatalogBook? {
        return catalogBooks.value.find { it.bookKey == bookKey }
    }
}