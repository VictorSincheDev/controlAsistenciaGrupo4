package com.grupo04.asistapp.data.remote.dto

data class ApiResponseDto<T>(
    val success: Boolean,
    val data: T?,
    val mensaje: String?,
    val timestamp: String?
)
