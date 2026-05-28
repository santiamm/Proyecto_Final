package com.example.proyecto_final.model

data class RemoteReport(
    val id: Int = 0,
    val title: String,
    val description: String,
    val category: String,
    val priority: String,
    val status: String,
    val timestamp: Long
)
