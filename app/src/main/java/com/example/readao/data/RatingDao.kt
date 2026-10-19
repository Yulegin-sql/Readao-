package com.example.readao.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RatingDao {

    @Query("SELECT * FROM ratings WHERE bookId = :bookId ORDER BY timestamp DESC")
    fun getRatingsForBook(bookId: Int): Flow<List<Rating>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRating(rating: Rating)

    @Update
    suspend fun updateRating(rating: Rating)

    @Delete
    suspend fun deleteRating(rating: Rating)

    @Query("SELECT AVG(score) FROM ratings WHERE bookId = :bookId")
    fun getAverageRating(bookId: Int): Flow<Double?>

    @Query("SELECT COUNT(*) FROM ratings WHERE bookId = :bookId")
    fun getRatingsCount(bookId: Int): Flow<Int>
}