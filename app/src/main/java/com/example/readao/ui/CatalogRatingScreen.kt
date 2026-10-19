package com.example.readao.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.readao.data.CatalogRating
import com.example.readao.viewmodel.CatalogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogRatingScreen(
    bookKey: String,
    bookTitle: String,
    viewModel: CatalogViewModel,
    onNavigateBack: () -> Unit
) {
    val ratings by viewModel.getRatingsForBook(bookKey)
        .collectAsStateWithLifecycle(initialValue = emptyList())

    var userRating by remember { mutableStateOf(0) }
    var userComment by remember { mutableStateOf("") }
    var showForm by remember { mutableStateOf(false) }

    val average = if (ratings.isEmpty()) 0.0
    else ratings.map { it.score }.average()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Отзывы: $bookTitle") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showForm = !showForm }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить отзыв")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Карточка со средним рейтингом
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "%.1f".format(average),
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold
                    )
                    RatingStars(rating = average, count = ratings.size)
                }
            }

            // Форма добавления отзыва
            if (showForm) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Ваша оценка:",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        InteractiveRatingBar(
                            rating = userRating,
                            onRatingChanged = { userRating = it }
                        )

                        OutlinedTextField(
                            value = userComment,
                            onValueChange = { userComment = it },
                            label = { Text("Комментарий (необязательно)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                if (userRating > 0) {
                                    viewModel.addRating(bookKey, userRating, userComment)
                                    userRating = 0
                                    userComment = ""
                                    showForm = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = userRating > 0
                        ) {
                            Text("Оставить отзыв")
                        }
                    }
                }
            }

            // Список отзывов
            if (ratings.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Пока нет отзывов. Будьте первым!")
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ratings, key = { it.id }) { rating ->
                        CatalogRatingCard(
                            rating = rating,
                            onDelete = {
                                viewModel.deleteRating(bookKey, rating.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CatalogRatingCard(rating: CatalogRating, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = rating.userName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                RatingStars(rating = rating.score.toDouble(), count = 0)
                if (rating.comment.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = rating.comment,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Удалить")
            }
        }
    }
}