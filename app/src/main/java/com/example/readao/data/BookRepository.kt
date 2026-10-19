package com.example.readao.data

import kotlinx.coroutines.flow.Flow

class BookRepository(
    private val bookDao: BookDao,
    private val ratingDao: RatingDao
) {

    val allBooks: Flow<List<Book>> = bookDao.getAllBooks()
    val allBooksWithRatings: Flow<List<BookWithRatings>> = bookDao.getAllBooksWithRatings()
    val finishedCount: Flow<Int> = bookDao.getFinishedBooksCount()
    val totalPages: Flow<Int?> = bookDao.getTotalPagesRead()

    suspend fun getBookById(id: Int): Book? = bookDao.getBookById(id)
    suspend fun getBookWithRatingsById(id: Int): BookWithRatings? =
        bookDao.getBookWithRatingsById(id)

    suspend fun addBook(book: Book): Long = bookDao.insertBook(book)
    suspend fun updateBook(book: Book) = bookDao.updateBook(book)
    suspend fun deleteBook(book: Book) = bookDao.deleteBook(book)

    fun getRatingsForBook(bookId: Int): Flow<List<Rating>> =
        ratingDao.getRatingsForBook(bookId)

    fun getAverageRating(bookId: Int): Flow<Double?> =
        ratingDao.getAverageRating(bookId)

    fun getRatingsCount(bookId: Int): Flow<Int> =
        ratingDao.getRatingsCount(bookId)

    suspend fun addRating(rating: Rating) = ratingDao.insertRating(rating)
    suspend fun updateRating(rating: Rating) = ratingDao.updateRating(rating)
    suspend fun deleteRating(rating: Rating) = ratingDao.deleteRating(rating)
}