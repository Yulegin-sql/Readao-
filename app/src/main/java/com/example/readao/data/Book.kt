package com.example.readao.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Book(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val author: String,
    val genre: String,
    val totalPages: Int,
    val currentPage: Int = 0,
    val status: String = STATUS_READING,

    // НОВЫЕ ПОЛЯ:
    val sharedInCatalog: Boolean = false,        // поделиться в каталоге
    val coverUri: String? = null,                 // обложка (локальный URI)
    val coverUrl: String? = null,                 // обложка (Firebase Storage URL)
    val startDate: Long? = null,                  // дата начала чтения
    val finishDate: Long? = null,                 // дата окончания чтения
    val totalReadingTimeMs: Long = 0              // время чтения (мс) — для таймера
) {
    companion object {
        const val STATUS_READING = "Reading"
        const val STATUS_FINISHED = "Finished"
        const val STATUS_ABANDONED = "Abandoned"

        fun statusLabel(status: String): String = when (status) {
            STATUS_READING -> "В процессе"
            STATUS_FINISHED -> "Прочитано"
            STATUS_ABANDONED -> "Отложено"
            else -> "Неизвестно"
        }
    }
}