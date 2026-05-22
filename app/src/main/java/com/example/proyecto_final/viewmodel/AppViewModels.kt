package com.example.proyecto_final.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyecto_final.data.ReportEntity
import com.example.proyecto_final.repository.AppRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReportViewModel(
    private val repository: AppRepository
) : ViewModel() {

    val allReports: StateFlow<List<ReportEntity>> =
        repository.getAllReports()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    fun saveReport(
        title: String,
        desc: String,
        cat: String,
        prio: String,
        onComplete: () -> Unit
    ) {

        viewModelScope.launch {

            repository.createReport(
                title,
                desc,
                cat,
                prio
            )

            onComplete()
        }
    }
}

class AppViewModelFactory(
    private val repository: AppRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(ReportViewModel::class.java)) {
            return ReportViewModel(repository) as T
        }

        throw IllegalArgumentException(
            "Clase ViewModel desconocida"
        )
    }
}