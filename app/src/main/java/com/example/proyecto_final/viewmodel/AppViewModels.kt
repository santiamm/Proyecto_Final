package com.example.proyecto_final.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyecto_final.data.ReportEntity
import com.example.proyecto_final.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReportViewModel(private val repository: AppRepository) : ViewModel() {
    val allReports: StateFlow<List<ReportEntity>> = repository.getAllReports()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveReport(title: String, desc: String, cat: String, prio: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.createReport(title, desc, cat, prio)
            onComplete()
        }
    }

    fun markAsResolved(reportId: Int) {
        viewModelScope.launch {
            repository.updateReportStatus(reportId, "Cerrado")
        }
    }

    fun markAsInProgress(reportId: Int) {
        viewModelScope.launch {
            repository.updateReportStatus(reportId, "En proceso")
        }
    }

    fun deleteReport(reportId: Int) {
        viewModelScope.launch {
            repository.deleteReport(reportId)
        }
    }

    // ✅ NUEVO: editar reporte completo
    fun updateReport(report: ReportEntity, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.updateReport(report)
            onComplete()
        }
    }
}

// AuthViewModel y factory se quedan igual, solo agrego el método de "En proceso" opcional
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

class AppViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReportViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReportViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}