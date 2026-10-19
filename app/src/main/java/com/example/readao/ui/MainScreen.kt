package com.example.readao.ui

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.readao.data.Book
import com.example.readao.data.BookWithRatings
import com.example.readao.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onAddClick: () -> Unit,
    onBookClick: (Int) -> Unit,
    onRatingClick: (Int, String) -> Unit
) {
    val booksWithRatings by viewModel.booksWithRatings.collectAsStateWithLifecycle()
    val finishedCount by viewModel.finishedCount.collectAsStateWithLifecycle()
    val totalPages by viewModel.totalPages.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Readao — мои книги") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Добавить книгу")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            StatisticsCard(
                finishedCount = finishedCount,
                totalPages = totalPages ?: 0
            )

            if (booksWithRatings.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = booksWithRatings,
                        key = { item -> item.book.id }
                    ) { item ->
                        BookCardWithRating(
                            item = item,
                            onClick = { onBookClick(item.book.id) },
                            onRatingClick = {
                                onRatingClick(item.book.id, item.book.title)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatisticsCard(finishedCount: Int, totalPages: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(label = "Прочитано книг", value = finishedCount.toString())
            StatItem(label = "Всего страниц", value = totalPages.toString())
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun BookCardWithRating(
    item: BookWithRatings,
    onClick: () -> Unit,
    onRatingClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.book.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = item.book.author,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${item.book.currentPage}/${item.book.totalPages} стр. • " +
                            Book.statusLabel(item.book.status),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )

                // ПРАВКА 1: даты начала и окончания
                if (item.book.status == Book.STATUS_FINISHED &&
                    item.book.startDate != null &&
                    item.book.finishDate != null
                ) {
                    val diff = item.book.finishDate - item.book.startDate
                    val days = diff / (1000 * 60 * 60 * 24)
                    val hours = diff / (1000 * 60 * 60)
                    val minutes = diff / (1000 * 60)

                    val durationText = when {
                        days >= 1 -> "за $days дн."
                        hours >= 1 -> "за $hours ч"
                        minutes >= 1 -> "за $minutes мин"
                        else -> "только что"
                    }

                    val dateFormat = java.text.SimpleDateFormat(
                        "dd.MM.yyyy",
                        java.util.Locale.getDefault()
                    )
                    val startStr = dateFormat.format(java.util.Date(item.book.startDate))
                    val finishStr = dateFormat.format(java.util.Date(item.book.finishDate))

                    Text(
                        text = "📅 $startStr → $finishStr ($durationText)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }

                // ПРАВКА 2: время чтения по таймеру
                if (item.book.totalReadingTimeMs > 0) {
                    Text(
                        text = "⏱️ Время чтения: ${formatReadingTime(item.book.totalReadingTimeMs)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                RatingStars(
                    rating = item.averageRating,
                    count = item.ratingsCount,
                    modifier = Modifier.clickable { onRatingClick() }
                )
            }
        }
    }
}

@Composable
fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Нет добавленных книг.\nНажмите «+», чтобы добавить.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

// Формат времени чтения (4 ч 25 мин)
private fun formatReadingTime(ms: Long): String {
    val totalMinutes = ms / (1000 * 60)
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return when {
        hours > 0 && minutes > 0 -> "$hours ч $minutes мин"
        hours > 0 -> "$hours ч"
        minutes > 0 -> "$minutes мин"
        else -> "меньше минуты"
    }
}