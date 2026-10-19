package com.example.readao.data

import androidx.room.Embedded
import androidx.room.Relation

data class BookWithRatings(
    @Embedded val book: Book,
    @Relation(
        parentColumn = "id",
        entityColumn = "bookId"
    )
    val ratings: List<Rating>
) {
    val averageRating: Double
        get() = if (ratings.isEmpty()) 0.0
        else ratings.map { it.score }.average()

    val ratingsCount: Int
        get() = ratings.size
}