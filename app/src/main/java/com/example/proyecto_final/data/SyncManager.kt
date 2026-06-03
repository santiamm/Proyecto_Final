package com.example.proyecto_final.data

import com.example.proyecto_final.model.RemoteReport
import com.example.proyecto_final.network.RetrofitClient
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.HttpException
import java.io.IOException

class SyncManager {
    private val mutex = Mutex()
    private val api = RetrofitClient.instance

    suspend fun syncReports(reportDao: ReportDao): SyncResult {
        return mutex.withLock {
            var uploaded = 0
            var downloaded = 0
            var updated = 0
            var failed = 0
            val errors = mutableListOf<String>()

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
                    val response = runWithRetry {
                        if (local.remoteId > 0) {
                            try {
                                api.updateReport(local.remoteId, payload)
                            } catch (e: HttpException) {
                                if (e.code() == 404) {
                                    api.createReport(payload)
                                } else {
                                    throw e
                                }
                            }
                        } else {
                            api.createReport(payload)
                        }
                    }
                    reportDao.markAsSynced(local.id, response.id)
                    uploaded++
                } catch (e: Exception) {
                    failed++
                    errors.add("No se pudo subir '${local.title}': ${readableError(e)}")
                }
            }

            try {
                val remoteReports = runWithRetry { api.getReports() }
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
            } catch (e: Exception) {
                failed++
                errors.add("No se pudieron descargar reportes: ${readableError(e)}")
            }

            SyncResult(
                uploaded = uploaded,
                downloaded = downloaded,
                updated = updated,
                failed = failed,
                errorMessage = errors.joinToString("\n")
            )
        }
    }

    private suspend fun <T> runWithRetry(block: suspend () -> T): T {
        var lastError: Exception? = null
        repeat(3) { attempt ->
            try {
                return block()
            } catch (e: Exception) {
                lastError = e
                if (attempt < 2) kotlinx.coroutines.delay(1000L * (attempt + 1))
            }
        }
        throw lastError ?: IllegalStateException("Error desconocido")
    }

    private fun readableError(error: Exception): String {
        return when (error) {
            is HttpException -> {
                when (error.code()) {
                    401 -> "error de autenticación"
                    404 -> "recurso no encontrado en servidor"
                    500 -> "error interno del servidor"
                    else -> "error del servidor (HTTP ${error.code()})"
                }
            }
            is IOException -> "sin conexión con el servidor (revisa tu internet)"
            else -> error.message ?: "error inesperado"
        }
    }
}
