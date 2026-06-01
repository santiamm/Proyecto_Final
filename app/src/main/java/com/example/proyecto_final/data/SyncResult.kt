package com.example.proyecto_final.data

data class SyncResult(
    val uploaded: Int,
    val downloaded: Int,
    val updated: Int,
    val failed: Int,
    val errorMessage: String = ""
) {
    val isSuccess: Boolean
        get() = failed == 0
}
