package com.example.proyecto_final

import android.app.Application
import com.example.proyecto_final.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class NodoCivicoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@NodoCivicoApp)
            modules(appModule)
        }
    }
}