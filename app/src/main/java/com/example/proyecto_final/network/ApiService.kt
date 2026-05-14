package com.example.proyecto_final.network

import com.example.proyecto_final.model.Category
import com.example.proyecto_final.model.Report

interface ApiService {
    fun getReports(): List<Report>
    fun getCategories(): List<Category>
}