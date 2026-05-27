package com.example.proyecto_final.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_final.data.ReportEntity
import com.example.proyecto_final.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ReportViewModel(
    private val repository: AppRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val mutex = Mutex()
    private val filterKey = "report_filter"

    val currentFilter: StateFlow<String> = savedStateHandle.getStateFlow(filterKey, "Todos")

    val allReports: StateFlow<List<ReportEntity>> = combine(
        repository.getAllReports(),
        currentFilter
    ) { reports, filter ->
        when (filter) {
            "Abiertos" -> reports.filter { it.status == "Abierto" }
            "En proceso" -> reports.filter { it.status == "En proceso" }
            "Cerrados" -> reports.filter { it.status == "Cerrado" }
            else -> reports
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setFilter(filter: String) {
        savedStateHandle[filterKey] = filter
    }

    fun saveReport(title: String, desc: String, cat: String, prio: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            mutex.withLock {
                repository.createReport(title, desc, cat, prio)
            }
            onComplete()
        }
    }

    fun markAsResolved(reportId: Int) {
        viewModelScope.launch {
            mutex.withLock {
                repository.updateReportStatus(reportId, "Cerrado")
            }
        }
    }

    fun markAsInProgress(reportId: Int) {
        viewModelScope.launch {
            mutex.withLock {
                repository.updateReportStatus(reportId, "En proceso")
            }
        }
    }

    fun deleteReport(reportId: Int) {
        viewModelScope.launch {
            mutex.withLock {
                repository.deleteReport(reportId)
            }
        }
    }

    fun updateReport(report: ReportEntity, onComplete: () -> Unit) {
        viewModelScope.launch {
            mutex.withLock {
                repository.updateReport(report)
            }
            onComplete()
        }
    }
}

class AuthViewModel(private val repository: AppRepository) : ViewModel() {
    val authResult = MutableStateFlow<Boolean?>(null)

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            authResult.value = repository.loginLocalUser(email, pass)
        }
    }

    fun register(email: String, pass: String) {
        viewModelScope.launch {
            authResult.value = repository.registerLocalUser(email, pass)
        }
    }

    fun resetAuthResult() {
        authResult.value = null
    }
}