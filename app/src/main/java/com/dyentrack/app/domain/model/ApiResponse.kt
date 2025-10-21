package com.dyentrack.app.domain.model

data class ApiResponse<T>(
    val message: String,
    val data: T,
    val timestamp: String,
    val status: Int,
    val error: String,
    val path : String
)