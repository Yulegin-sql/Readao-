package com.example.readao.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.readao.data.CatalogBook
import com.example.readao.data.CatalogRating
import com.example.readao.viewmodel.CatalogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    viewModel: CatalogViewModel,
    onBookClick: (CatalogBook) -> Unit,
    onAddToLibrary: (CatalogBook) -> Unit
) {
    val catalogBooks by viewModel.catalogBooks.collectAsStateWithLifecycle()
    val isAdmin = viewModel.isAdmin

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Каталог книг") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            )
        }
    ) { padding ->
        if (catalogBooks.isEmpty()) {
            EmptyCatalogState(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = catalogBooks,
                    key = { it.bookKey }
                ) { book ->
                    CatalogBookCardWithRatings(
                        book = book,
                        isAdmin = isAdmin,
                        viewModel = viewModel,
                        onClick = { onBookClick(book) },
                        onAddToLibrary = { onAddToLibrary(book) },
                        onDelete = {
                            if (isAdmin) viewModel.deleteFromCatalog(book.bookKey)
                        }
                    )
                }
            }
        }
    }
}

/**
 * Карточка книги с подгрузкой рейтинга из Firestore.
 */
@Composable
fun CatalogBookCardWithRatings(
    book: CatalogBook,
    isAdmin: Boolean,
    viewModel: CatalogViewModel,
    onClick: () -> Unit,
    onAddToLibrary: () -> Unit,
    onDelete: () -> Unit
) {
    var ratings by remember { mutableStateOf<List<CatalogRating>>(emptyList()) }

    LaunchedEffect(book.bookKey) {
        viewModel.getRatingsForBook(book.bookKey).collect { list ->
            ratings = list
        }
    }

    val avgRating = if (ratings.isEmpty()) 0.0
    else ratings.map { it.score }.average()
    val ratingsCount = ratings.size

    CatalogBookCard(
        book = book,
        isAdmin = isAdmin,
        avgRating = avgRating,
        ratingsCount = ratingsCount,
        onClick = onClick,
        onAddToLibrary = onAddToLibrary,
        onDelete = onDelete
    )
}

@Composable
fun CatalogBookCard(
    book: CatalogBook,
    isAdmin: Boolean,
    avgRating: Double,
    ratingsCount: Int,
    onClick: () -> Unit,
    onAddToLibrary: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Обложка
            if (!book.coverUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = book.coverUrl,
                    contentDescription = null,
                    modifier = Modifier.size(70.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                    contentDescription = null,
                    modifier = Modifier.size(70.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1
                )
                Text(
                    text = "${book.genre} • ${book.totalPages} стр.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Счётчик читателей
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Читают: ${book.readersCount}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // ★ Рейтинг и количество отзывов
                Spacer(modifier = Modifier.height(4.dp))
                RatingStars(
                    rating = avgRating,
                    count = ratingsCount
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Кнопки
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(onClick = onAddToLibrary) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "В мою библиотеку",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                if (isAdmin) {
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Удалить",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyCatalogState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Каталог пуст.\nПоделитесь книгой, чтобы она появилась здесь.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}