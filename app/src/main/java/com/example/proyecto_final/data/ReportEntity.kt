package com.example.proyecto_final.data
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reports")
data class ReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val category: String,
    val priority: String,
    val status: String = "Abierto",
    val isSynced: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
