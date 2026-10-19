package com.example.readao.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.readao.data.Rating
import com.example.readao.viewmodel.BookEvent
import com.example.readao.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingScreen(
    bookId: Int,
    bookTitle: String,
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val ratings by viewModel.getRatingsForBook(bookId)
        .collectAsStateWithLifecycle(initialValue = emptyList())

    var userName by remember { mutableStateOf("") }
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
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
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
                        OutlinedTextField(
                            value = userName,
                            onValueChange = { userName = it },
                            label = { Text("Ваше имя") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("Ваша оценка:", style = MaterialTheme.typography.bodyMedium)
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
                                if (userName.isNotBlank() && userRating > 0) {
                                    viewModel.onEvent(
                                        BookEvent.AddRating(
                                            Rating(
                                                bookId = bookId,
                                                userName = userName,
                                                score = userRating,
                                                comment = userComment
                                            )
                                        )
                                    )
                                    userName = ""
                                    userRating = 0
                                    userComment = ""
                                    showForm = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Оставить отзыв")
                        }
                    }
                }
            }

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
                        RatingCard(
                            rating = rating,
                            onDelete = {
                                viewModel.onEvent(BookEvent.DeleteRating(rating))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RatingCard(rating: Rating, onDelete: () -> Unit) {
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