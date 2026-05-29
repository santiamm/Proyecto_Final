package com.example.proyecto_final

import android.app.Application
import com.example.proyecto_final.data.AppDatabase
import com.example.proyecto_final.data.SyncManager
import com.example.proyecto_final.di.appModule
import com.example.proyecto_final.utils.ConnectivityReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class NodoCivicoApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@NodoCivicoApp)
            modules(appModule)
        }

        ConnectivityReceiver.onNetworkConnected = { context ->

            CoroutineScope(Dispatchers.IO).launch {

                val database = AppDatabase.getDatabase(context)

                val syncManager = SyncManager(context)

                syncManager.syncReports(database.reportDao())
            }
        }
    }
}