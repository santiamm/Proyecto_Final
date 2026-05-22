package com.example.proyecto_final

import android.app.Application
import com.example.proyecto_final.data.AppDatabase
import com.example.proyecto_final.repository.AppRepository

class NodoCivicoApp : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { AppRepository(database.userDao(), database.reportDao()) }
}
