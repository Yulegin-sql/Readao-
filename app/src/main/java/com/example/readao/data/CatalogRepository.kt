package com.example.readao.data

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class CatalogRepository(private val context: Context) {

    init {
        // Гарантируем, что Firebase инициализирован ДО любого запроса
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }
    }

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val catalogCollection by lazy {
        firestore.collection("catalog")
    }

    private val adminsCollection by lazy {
        firestore.collection("admins")
    }

    // ============================================================
    // ПОЛУЧЕНИЕ КАТАЛОГА (реактивный поток)
    // ============================================================
    fun getCatalogBooks(): Flow<List<CatalogBook>> = callbackFlow {
        val listener = catalogCollection
            .orderBy("addedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val books = snapshot?.documents?.mapNotNull {
                    it.toObject(CatalogBook::class.java)
                } ?: emptyList()
                trySend(books)
            }
        awaitClose { listener.remove() }
    }

    // ============================================================
    // ДОБАВЛЕНИЕ / ОБНОВЛЕНИЕ КНИГИ В КАТАЛОГЕ
    // ============================================================
    suspend fun shareBookToCatalog(
        book: Book,
        userName: String,
        userEmail: String,
        coverUrl: String? = null
    ) {
        val key = makeBookKey(book.author, book.title)

        try {
            val doc = catalogCollection.document(key).get().await()

            if (doc.exists()) {
                val current = doc.getLong("readersCount") ?: 0L
                catalogCollection.document(key)
                    .update("readersCount", current + 1)
                    .await()
            } else {
                val catalogBook = CatalogBook(
                    bookKey = key,
                    title = book.title,
                    author = book.author,
                    genre = book.genre,
                    totalPages = book.totalPages,
                    coverUrl = coverUrl,
                    addedBy = userName,
                    addedByEmail = userEmail,
                    addedAt = System.currentTimeMillis(),
                    readersCount = 1
                )
                catalogCollection.document(key).set(catalogBook).await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ============================================================
    // УДАЛЕНИЕ ИЗ КАТАЛОГА (только для админа)
    // ============================================================
    suspend fun deleteFromCatalog(bookKey: String) {
        try {
            catalogCollection.document(bookKey).delete().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ============================================================
    // ПРОВЕРКА: АДМИН ЛИ ПОЛЬЗОВАТЕЛЬ
    // ============================================================
    suspend fun checkIsAdmin(email: String): Boolean {
        return try {
            val doc = adminsCollection.document(email).get().await()
            doc.exists() && (doc.getBoolean("active") ?: false)
        } catch (e: Exception) {
            false
        }
    }

    // ============================================================
    // ОТЗЫВЫ
    // ============================================================
    fun getRatingsForBook(bookKey: String): Flow<List<CatalogRating>> = callbackFlow {
        val listener = catalogCollection.document(bookKey)
            .collection("ratings")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val ratings = snapshot?.documents?.mapNotNull {
                    it.toObject(CatalogRating::class.java)
                } ?: emptyList()
                trySend(ratings)
            }
        awaitClose { listener.remove() }
    }

    suspend fun addRating(bookKey: String, rating: CatalogRating) {
        try {
            catalogCollection.document(bookKey)
                .collection("ratings")
                .add(rating)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun deleteRating(bookKey: String, ratingId: String) {
        try {
            catalogCollection.document(bookKey)
                .collection("ratings")
                .document(ratingId)
                .delete()
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ============================================================
    // УТИЛИТА: ключ книги
    // ============================================================
    private fun makeBookKey(author: String, title: String): String {
        val cleanAuthor = author.trim().lowercase().replace(" ", "_")
        val cleanTitle = title.trim().lowercase().replace(" ", "_")
        return "${cleanAuthor}__${cleanTitle}"
    }

    companion object {
        fun makeKey(author: String, title: String): String {
            val cleanAuthor = author.trim().lowercase().replace(" ", "_")
            val cleanTitle = title.trim().lowercase().replace(" ", "_")
            return "${cleanAuthor}__${cleanTitle}"
        }
    }
}