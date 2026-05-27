package com.example.proyecto_final.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("nodo_civico_session", Context.MODE_PRIVATE)
    private val namePrefs: SharedPreferences = context.getSharedPreferences("nodo_civico_user_name", Context.MODE_PRIVATE)

    fun saveLoginSession(email: String) {
        prefs.edit().putBoolean("IS_LOGGED_IN", true).putString("USER_EMAIL", email).apply()
        if (getUserName().isEmpty()) {
            saveUserName(email.split("@").first())
        }
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean("IS_LOGGED_IN", false)
    }

    fun getUserEmail(): String {
        return prefs.getString("USER_EMAIL", "vecino@nodocivico.com") ?: "vecino@nodocivico.com"
    }

    fun saveUserName(name: String) {
        namePrefs.edit().putString("USER_NAME", name).apply()
    }

    fun getUserName(): String {
        return namePrefs.getString("USER_NAME", "Vecino") ?: "Vecino"
    }

    fun logout() {
        prefs.edit().clear().apply()
    }
}