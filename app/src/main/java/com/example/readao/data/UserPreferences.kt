package com.example.readao.data

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "readao_prefs",
        Context.MODE_PRIVATE
    )

    var userName: String
        get() = prefs.getString(KEY_NAME, "") ?: ""
        set(value) = prefs.edit().putString(KEY_NAME, value).apply()

    var userEmail: String
        get() = prefs.getString(KEY_EMAIL, "") ?: ""
        set(value) = prefs.edit().putString(KEY_EMAIL, value).apply()

    var isAdmin: Boolean
        get() = prefs.getBoolean(KEY_IS_ADMIN, false)
        set(value) = prefs.edit().putBoolean(KEY_IS_ADMIN, value).apply()

    val isRegistered: Boolean
        get() = userName.isNotBlank() && userEmail.isNotBlank()

    fun clear() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val KEY_NAME = "user_name"
        private const val KEY_EMAIL = "user_email"
        private const val KEY_IS_ADMIN = "is_admin"
    }
}