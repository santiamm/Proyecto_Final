package com.example.proyecto_final.repository

import com.example.proyecto_final.data.ReportDao
import com.example.proyecto_final.data.ReportEntity
import com.example.proyecto_final.data.SyncManager
import com.example.proyecto_final.data.SyncResult
import com.example.proyecto_final.data.UserDao
import com.example.proyecto_final.data.UserEntity
import kotlinx.coroutines.flow.Flow

class AppRepository(private val userDao: UserDao, private val reportDao: ReportDao) {

    // Usuario
    suspend fun registerLocalUser(email: String, pass: String): Boolean {
        if (userDao.getUserByEmail(email) != null) return false
        userDao.insertUser(UserEntity(email = email, passwordHash = pass))
        return true
    }

    suspend fun loginLocalUser(email: String, pass: String): Boolean {
        val user = userDao.getUserByEmail(email)
        return user != null && user.passwordHash == pass
    }

    // Reportes
    suspend fun createReport(title: String, desc: String, cat: String, prio: String) {
        reportDao.insertReport(ReportEntity(title = title, description = desc, category = cat, priority = prio))
    }

    fun getAllReports(): Flow<List<ReportEntity>> = reportDao.getAllReports()

    suspend fun updateReportStatus(reportId: Int, newStatus: String) = reportDao.updateReportStatus(reportId, newStatus)

    suspend fun deleteReport(reportId: Int) = reportDao.deleteReportById(reportId)

    // ✅ NUEVO: actualizar reporte completo
    suspend fun updateReport(report: ReportEntity) = reportDao.updateReport(report)

    suspend fun syncReports(): SyncResult {
        val syncManager = SyncManager()
        return syncManager.syncReports(reportDao)
    }
}
