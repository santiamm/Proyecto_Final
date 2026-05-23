package com.example.proyecto_final

import android.app.Application
import com.example.proyecto_final.data.AppDatabase
import com.example.proyecto_final.repository.AppRepository

class NodoCivicoApp : Application() {
    lateinit var repository: AppRepository

    override fun onCreate() {
        super.onCreate()
        val database = AppDatabase.getDatabase(this)
        repository = AppRepository(database.userDao(), database.reportDao())
    }
}