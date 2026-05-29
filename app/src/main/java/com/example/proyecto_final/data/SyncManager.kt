package com.example.proyecto_final.data

import com.example.proyecto_final.model.RemoteReport
import com.example.proyecto_final.network.RetrofitClient
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import android.content.Context

class SyncManager(private val context: Context) {
    private val mutex = Mutex()
    private val api = RetrofitClient.instance

    suspend fun syncReports(reportDao: ReportDao): SyncResult {
        return mutex.withLock {
            var uploaded = 0
            var downloaded = 0
            var updated = 0
            var failed = 0

            val unsynced = reportDao.getUnsyncedReports()
            for (local in unsynced) {
                val payload = RemoteReport(
                    id = local.remoteId,
                    title = local.title,
                    description = local.description,
                    category = local.category,
                    priority = local.priority,
                    status = local.status,
                    timestamp = local.timestamp
                )

                try {
                    val response = if (local.remoteId > 0) {
                        api.updateReport(local.remoteId, payload)
                    } else {
                        api.createReport(payload)
                    }
                    reportDao.markAsSynced(local.id, response.id)
                    uploaded++
                } catch (_: Exception) {
                    failed++
                }
            }

            try {
                val remoteReports = api.getReports()
                for (remote in remoteReports) {
                    val existing = reportDao.getReportByRemoteId(remote.id)
                    if (existing == null) {
                        val newReport = ReportEntity(
                            title = remote.title,
                            description = remote.description,
                            category = remote.category,
                            priority = remote.priority,
                            status = remote.status,
                            isSynced = true,
                            timestamp = remote.timestamp,
                            remoteId = remote.id
                        )
                        reportDao.insertReport(newReport)
                        downloaded++
                    } else if (existing.timestamp < remote.timestamp) {
                        val updatedReport = existing.copy(
                            title = remote.title,
                            description = remote.description,
                            category = remote.category,
                            priority = remote.priority,
                            status = remote.status,
                            timestamp = remote.timestamp,
                            isSynced = true,
                            remoteId = remote.id
                        )
                        reportDao.updateReport(updatedReport)
                        updated++
                    }
                }
            } catch (_: Exception) {
                failed++
            }

            SyncResult(uploaded = uploaded, downloaded = downloaded, updated = updated, failed = failed)
        }
    }
}
