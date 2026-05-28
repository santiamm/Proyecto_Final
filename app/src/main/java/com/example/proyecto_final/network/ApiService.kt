package com.example.proyecto_final.network

import com.example.proyecto_final.model.RemoteReport
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @GET("reports")
    suspend fun getReports(): List<RemoteReport>

    @POST("reports")
    suspend fun createReport(@Body report: RemoteReport): RemoteReport

    @PUT("reports/{id}")
    suspend fun updateReport(@Path("id") id: Int, @Body report: RemoteReport): RemoteReport

    @GET("reports/{id}")
    suspend fun getReportById(@Path("id") id: Int): RemoteReport

    @GET("categories")
    suspend fun getCategories(): List<String>

    @POST("users/register")
    suspend fun registerUser(@Body user: Map<String, String>): Map<String, String>
}
