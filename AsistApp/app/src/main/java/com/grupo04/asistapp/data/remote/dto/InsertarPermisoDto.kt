package com.grupo04.asistapp.data.remote.dto

data class InsertarPermisoDto(
    val trabajadorId: String,
    val tipo: String,
    val fechaInicio: String,
    val fechaFin: String,
    val motivo: String
)
