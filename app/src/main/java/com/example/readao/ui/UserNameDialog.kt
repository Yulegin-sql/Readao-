package com.example.readao.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun UserNameDialog(
    onConfirm: (name: String, email: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { /* нельзя закрыть */ },
        title = { Text("Добро пожаловать в Readao!") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Как вас зовут?",
                    style = MaterialTheme.typography.bodyMedium
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Имя") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text(
                    text = "Ваш email (для синхронизации):",
                    style = MaterialTheme.typography.bodyMedium
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = !it.contains("@") && it.isNotBlank()
                    },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = emailError,
                    supportingText = if (emailError) {
                        { Text("Введите корректный email") }
                    } else null
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && email.contains("@")) {
                        onConfirm(name.trim(), email.trim().lowercase())
                    }
                },
                enabled = name.isNotBlank() && email.contains("@")
            ) {
                Text("Продолжить")
            }
        }
    )
}