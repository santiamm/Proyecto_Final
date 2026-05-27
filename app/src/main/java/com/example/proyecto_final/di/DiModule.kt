package com.example.proyecto_final.di

import com.example.proyecto_final.data.AppDatabase
import com.example.proyecto_final.repository.AppRepository
import com.example.proyecto_final.viewmodel.AuthViewModel
import com.example.proyecto_final.viewmodel.ReportViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { AppDatabase.getDatabase(androidContext()) }
    single { get<AppDatabase>().userDao() }
    single { get<AppDatabase>().reportDao() }
    single { AppRepository(get(), get()) }
    viewModel { ReportViewModel(get(), get()) }
    viewModel { AuthViewModel(get()) }
}