package com.example.readao.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Query("SELECT * FROM books ORDER BY id DESC")
    fun getAllBooks(): Flow<List<Book>>

    @Transaction
    @Query("SELECT * FROM books ORDER BY id DESC")
    fun getAllBooksWithRatings(): Flow<List<BookWithRatings>>

    @Transaction
    @Query("SELECT * FROM books WHERE id = :id")
    suspend fun getBookWithRatingsById(id: Int): BookWithRatings?

    @Query("SELECT * FROM books WHERE id = :id")
    suspend fun getBookById(id: Int): Book?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: Book): Long

    @Update
    suspend fun updateBook(book: Book)

    @Delete
    suspend fun deleteBook(book: Book)

    @Query("SELECT COUNT(*) FROM books WHERE status = 'Finished'")
    fun getFinishedBooksCount(): Flow<Int>

    @Query("SELECT SUM(currentPage) FROM books WHERE status = 'Finished'")
    fun getTotalPagesRead(): Flow<Int?>
}