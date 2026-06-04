package com.example.proyecto_final.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?
}

@Dao
interface ReportDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ReportEntity)

    @Query("SELECT * FROM reports ORDER BY timestamp DESC")
    fun getAllReports(): Flow<List<ReportEntity>>

    @Query("UPDATE reports SET status = :newStatus WHERE id = :reportId")
    suspend fun updateReportStatus(reportId: Int, newStatus: String)

    @Delete
    suspend fun deleteReport(report: ReportEntity)

    @Query("DELETE FROM reports WHERE id = :reportId")
    suspend fun deleteReportById(reportId: Int)


    @Update
    suspend fun updateReport(report: ReportEntity)

    @Query("SELECT * FROM reports WHERE isSynced = 0 ORDER BY timestamp ASC")
    suspend fun getUnsyncedReports(): List<ReportEntity>

    @Query("UPDATE reports SET isSynced = 1, remoteId = :remoteId WHERE id = :reportId")
    suspend fun markAsSynced(reportId: Int, remoteId: Int)

    @Query("SELECT * FROM reports WHERE remoteId = :remoteId LIMIT 1")
    suspend fun getReportByRemoteId(remoteId: Int): ReportEntity?
}
