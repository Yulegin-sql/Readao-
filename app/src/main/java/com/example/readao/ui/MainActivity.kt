package com.example.readao.ui

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.readao.data.Book
import com.example.readao.data.UserPreferences
import com.example.readao.ui.theme.ReadaoTheme
import com.example.readao.viewmodel.BookEvent
import com.example.readao.viewmodel.CatalogViewModel
import com.example.readao.viewmodel.MainViewModel
import com.example.readao.viewmodel.TimerViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализация Firebase ДО создания Compose UI
        com.google.firebase.FirebaseApp.initializeApp(this)

        setContent {
            ReadaoTheme {
                ReadaoApp()
            }
        }
    }
}

@Composable
fun ReadaoApp() {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }

    var isRegistered by remember { mutableStateOf(userPrefs.isRegistered) }

    if (!isRegistered) {
        UserNameDialog(
            onConfirm = { name, email ->
                userPrefs.userName = name
                userPrefs.userEmail = email
                isRegistered = true
            }
        )
    } else {
        ReadaoNavigation()
    }
}

@Composable
fun ReadaoNavigation() {
    val navController = rememberNavController()
    val mainViewModel: MainViewModel = viewModel()
    val catalogViewModel: CatalogViewModel = viewModel()
    val timerViewModel: TimerViewModel = viewModel()

    var selectedTab by remember { mutableStateOf(AppTab.MY_BOOKS) }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedTab = selectedTab,
                onTabSelected = { tab -> selectedTab = tab }
            )
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "main",
            modifier = Modifier.padding(padding)
        ) {
            // ГЛАВНЫЙ ЭКРАН — переключает вкладки
            composable("main") {
                when (selectedTab) {
                    AppTab.MY_BOOKS -> {
                        MainScreen(
                            viewModel = mainViewModel,
                            onAddClick = { navController.navigate("add_edit/0") },
                            onBookClick = { id -> navController.navigate("add_edit/$id") },
                            onRatingClick = { id, title ->
                                navController.navigate("ratings/local/$id/${Uri.encode(title)}")
                            }
                        )
                    }
                    AppTab.CATALOG -> {
                        CatalogScreen(
                            viewModel = catalogViewModel,
                            onBookClick = { catalogBook ->
                                val key = catalogBook.bookKey
                                val title = Uri.encode(catalogBook.title)
                                navController.navigate("ratings/catalog/$key/$title")
                            },
                            onAddToLibrary = { catalogBook ->
                                val newBook = Book(
                                    title = catalogBook.title,
                                    author = catalogBook.author,
                                    genre = catalogBook.genre,
                                    totalPages = catalogBook.totalPages,
                                    currentPage = 0,
                                    status = Book.STATUS_READING
                                )
                                mainViewModel.onEvent(BookEvent.AddBook(newBook))
                            }
                        )
                    }
                    AppTab.TIMER -> {
                        TimerScreen(viewModel = timerViewModel)
                    }
                }
            }

            // ЭКРАН ДОБАВЛЕНИЯ/РЕДАКТИРОВАНИЯ
            composable("add_edit/{bookId}") { backStackEntry ->
                val bookId = backStackEntry.arguments?.getString("bookId")?.toIntOrNull() ?: 0
                AddEditScreen(
                    bookId = bookId,
                    viewModel = mainViewModel,
                    catalogViewModel = catalogViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // ОТЗЫВЫ — ЛОКАЛЬНАЯ КНИГА
            composable("ratings/local/{bookId}/{title}") { backStackEntry ->
                val bookId = backStackEntry.arguments?.getString("bookId")?.toIntOrNull() ?: 0
                val title = Uri.decode(backStackEntry.arguments?.getString("title") ?: "")
                RatingScreen(
                    bookId = bookId,
                    bookTitle = title,
                    viewModel = mainViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // ОТЗЫВЫ — КАТАЛОЖНАЯ КНИГА
            composable("ratings/catalog/{bookKey}/{title}") { backStackEntry ->
                val bookKey = backStackEntry.arguments?.getString("bookKey") ?: ""
                val title = Uri.decode(backStackEntry.arguments?.getString("title") ?: "")
                CatalogRatingScreen(
                    bookKey = bookKey,
                    bookTitle = title,
                    viewModel = catalogViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}