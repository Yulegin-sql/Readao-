package com.example.readao.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.readao.BookTrackerApp
import com.example.readao.data.Book
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TimerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as BookTrackerApp).repository

    // Список книг (из Room)
    val books = repository.allBooks.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Выбранная книга
    private val _selectedBookId = MutableStateFlow<Int?>(null)
    val selectedBookId: StateFlow<Int?> = _selectedBookId.asStateFlow()

    // Время в миллисекундах (текущая сессия)
    private val _elapsedMs = MutableStateFlow(0L)
    val elapsedMs: StateFlow<Long> = _elapsedMs.asStateFlow()

    // Идёт ли таймер
    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private var timerJob: Job? = null

    fun selectBook(bookId: Int) {
        if (_isRunning.value) return  // нельзя менять во время работы
        _selectedBookId.value = bookId
        _elapsedMs.value = 0L
    }

    fun start() {
        if (_isRunning.value) return
        if (_selectedBookId.value == null) return

        _isRunning.value = true
        timerJob = viewModelScope.launch {
            while (_isRunning.value) {
                delay(1000)
                _elapsedMs.value += 1000
            }
        }
    }

    fun pause() {
        _isRunning.value = false
        timerJob?.cancel()
        timerJob = null
    }

    fun stop() {
        val bookId = _selectedBookId.value ?: return
        val timeToAdd = _elapsedMs.value

        // Останавливаем
        _isRunning.value = false
        timerJob?.cancel()
        timerJob = null

        // Сохраняем в БД
        if (timeToAdd > 0) {
            viewModelScope.launch {
                val book = books.value.find { it.id == bookId }
                book?.let {
                    val updated = it.copy(
                        totalReadingTimeMs = it.totalReadingTimeMs + timeToAdd
                    )
                    repository.updateBook(updated)
                }
            }
        }

        _elapsedMs.value = 0L
    }

    fun reset() {
        _isRunning.value = false
        timerJob?.cancel()
        timerJob = null
        _elapsedMs.value = 0L
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}