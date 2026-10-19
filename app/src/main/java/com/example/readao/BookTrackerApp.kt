package com.example.readao

import android.app.Application
import com.google.firebase.FirebaseApp
import com.example.readao.data.BookDatabase
import com.example.readao.data.BookRepository

class BookTrackerApp : Application() {

    val database by lazy { BookDatabase.getInstance(this) }
    val repository by lazy {
        BookRepository(database.bookDao(), database.ratingDao())
    }

    override fun onCreate() {
        super.onCreate()
        // Инициализация Firebase
        FirebaseApp.initializeApp(this)
    }
}