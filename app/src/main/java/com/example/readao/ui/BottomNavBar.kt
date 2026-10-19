package com.example.readao.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

enum class AppTab(
    val label: String,
    val icon: @Composable () -> Unit
) {
    MY_BOOKS(
        label = "Мои книги",
        icon = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                contentDescription = "Мои книги"
            )
        }
    ),
    CATALOG(
        label = "Каталог",
        icon = {
            Icon(
                imageVector = Icons.Default.Public,
                contentDescription = "Каталог"
            )
        }
    ),
    TIMER(
        label = "Таймер",
        icon = {
            Icon(
                imageVector = Icons.Default.Timer,
                contentDescription = "Таймер"
            )
        }
    )
}

@Composable
fun BottomNavBar(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit
) {
    NavigationBar {
        AppTab.entries.forEach { tab ->
            NavigationBarItem(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                icon = tab.icon,
                label = { Text(tab.label) }
            )
        }
    }
}