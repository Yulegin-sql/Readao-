package com.example.readao.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.readao.data.Book
import com.example.readao.viewmodel.BookEvent
import com.example.readao.viewmodel.CatalogViewModel
import com.example.readao.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(
    bookId: Int,
    viewModel: MainViewModel,
    catalogViewModel: CatalogViewModel,
    onNavigateBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("") }
    var totalPages by remember { mutableStateOf("") }
    var currentPage by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(Book.STATUS_READING) }
    var existingBook by remember { mutableStateOf<Book?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var coverUri by remember { mutableStateOf<String?>(null) }
    var shareInCatalog by remember { mutableStateOf(true) }

    val isEditMode = bookId != 0

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { coverUri = it.toString() }
    }

    LaunchedEffect(bookId) {
        if (isEditMode) {
            viewModel.getBookById(bookId)?.let { book ->
                existingBook = book
                title = book.title
                author = book.author
                genre = book.genre
                totalPages = book.totalPages.toString()
                currentPage = book.currentPage.toString()
                status = book.status
                shareInCatalog = book.sharedInCatalog
                coverUri = book.coverUri
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Редактирование" else "Новая книга") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = {
                    if (isEditMode) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Удалить")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ПРАВКА 3: кнопка выбора обложки
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                onClick = { imagePicker.launch("image/*") }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (!coverUri.isNullOrEmpty()) {
                        AsyncImage(
                            model = coverUri,
                            contentDescription = "Обложка",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Нажмите, чтобы выбрать обложку")
                        }
                    }
                }
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Название *") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = author,
                onValueChange = { author = it },
                label = { Text("Автор *") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = genre,
                onValueChange = { genre = it },
                label = { Text("Жанр") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = totalPages,
                onValueChange = { if (it.all { c -> c.isDigit() }) totalPages = it },
                label = { Text("Всего страниц *") },
                modifier = Modifier.fillMaxWidth()
            )

            if (isEditMode) {
                OutlinedTextField(
                    value = currentPage,
                    onValueChange = { if (it.all { c -> c.isDigit() }) currentPage = it },
                    label = { Text("Текущая страница") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Статус:", style = MaterialTheme.typography.titleSmall)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatusButton("В процессе", Book.STATUS_READING, status) {
                        status = it
                    }
                    StatusButton("Прочитано", Book.STATUS_FINISHED, status) {
                        status = it
                        currentPage = totalPages
                    }
                    StatusButton("Отложено", Book.STATUS_ABANDONED, status) {
                        status = it
                    }
                }

                // ПРАВКА 1 + ПРАВКА 2: даты и время чтения
                val bookForDates = existingBook
                if (bookForDates?.startDate != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val dateFormat = SimpleDateFormat(
                                "dd.MM.yyyy HH:mm",
                                Locale.getDefault()
                            )

                            Text(
                                text = "📅 Начато: " +
                                        dateFormat.format(Date(bookForDates.startDate)),
                                style = MaterialTheme.typography.bodyMedium
                            )

                            val finish = bookForDates.finishDate
                            if (finish != null) {
                                Text(
                                    text = "📅 Закончено: " +
                                            dateFormat.format(Date(finish)),
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                val diff = finish - bookForDates.startDate
                                val days = diff / (1000 * 60 * 60 * 24)
                                val hours = diff / (1000 * 60 * 60)
                                val minutes = diff / (1000 * 60)

                                val durationText = when {
                                    days >= 1 -> "$days дн."
                                    hours >= 1 -> "$hours ч"
                                    minutes >= 1 -> "$minutes мин"
                                    else -> "несколько секунд"
                                }

                                Text(
                                    text = "⏱️ Прочитано за $durationText",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // ПРАВКА 2: время чтения по таймеру
                            if (bookForDates.totalReadingTimeMs > 0) {
                                val totalMinutes = bookForDates.totalReadingTimeMs / (1000 * 60)
                                val hours = totalMinutes / 60
                                val minutes = totalMinutes % 60
                                val timeText = when {
                                    hours > 0 && minutes > 0 -> "$hours ч $minutes мин"
                                    hours > 0 -> "$hours ч"
                                    minutes > 0 -> "$minutes мин"
                                    else -> "меньше минуты"
                                }
                                Text(
                                    text = "⏱️ Время чтения по таймеру: $timeText",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = shareInCatalog,
                    onCheckedChange = { shareInCatalog = it }
                )
                Column {
                    Text("Поделиться в общем каталоге")
                    Text(
                        text = "Другие пользователи увидят эту книгу",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (title.isBlank() || author.isBlank() || totalPages.isBlank()) return@Button

                    val total = totalPages.toIntOrNull() ?: 0
                    val current = currentPage.toIntOrNull() ?: 0

                    val finalStatus = if (current >= total && total > 0) {
                        Book.STATUS_FINISHED
                    } else status

                    val finalCurrent = if (finalStatus == Book.STATUS_FINISHED) {
                        total
                    } else current

                    val now = System.currentTimeMillis()
                    val existing = existingBook

                    val newStartDate = when {
                        existing?.startDate != null -> existing.startDate
                        finalStatus == Book.STATUS_READING -> now
                        finalStatus == Book.STATUS_FINISHED -> now
                        else -> null
                    }

                    val newFinishDate = when {
                        finalStatus == Book.STATUS_FINISHED && existing?.finishDate == null -> now
                        finalStatus != Book.STATUS_FINISHED -> null
                        else -> existing?.finishDate
                    }

                    val book = existing?.copy(
                        title = title,
                        author = author,
                        genre = genre,
                        totalPages = total,
                        currentPage = finalCurrent,
                        status = finalStatus,
                        sharedInCatalog = shareInCatalog,
                        coverUri = coverUri,
                        startDate = newStartDate,
                        finishDate = newFinishDate
                    ) ?: Book(
                        title = title,
                        author = author,
                        genre = genre,
                        totalPages = total,
                        currentPage = finalCurrent,
                        status = finalStatus,
                        sharedInCatalog = shareInCatalog,
                        coverUri = coverUri,
                        startDate = newStartDate,
                        finishDate = newFinishDate
                    )

                    if (isEditMode) {
                        viewModel.onEvent(BookEvent.UpdateBook(book))
                    } else {
                        viewModel.onEvent(BookEvent.AddBook(book))
                    }

                    if (shareInCatalog) {
                        catalogViewModel.shareBook(book)
                    }

                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isEditMode) "Сохранить" else "Добавить")
            }

            OutlinedButton(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Отмена")
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Удалить книгу?") },
            text = { Text("Это действие нельзя отменить.") },
            confirmButton = {
                TextButton(onClick = {
                    existingBook?.let { viewModel.onEvent(BookEvent.DeleteBook(it)) }
                    showDeleteDialog = false
                    onNavigateBack()
                }) { Text("Удалить") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Отмена") }
            }
        )
    }
}

@Composable
fun StatusButton(
    label: String,
    value: String,
    currentStatus: String,
    onSelect: (String) -> Unit
) {
    FilterChip(
        selected = currentStatus == value,
        onClick = { onSelect(value) },
        label = { Text(label) }
    )
}