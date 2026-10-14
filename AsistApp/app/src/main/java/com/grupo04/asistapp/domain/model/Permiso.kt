package com.grupo04.asistapp.domain.model

data class Permiso(
    val id: String = "",
    val codigo: String = "",
    val trabajadorId: String,
    val trabajadorNombre: String = "",
    val area: String = "",
    val tipo: String,
    val fechaInicio: String,
    val fechaFin: String,
    val motivo: String,
    val estado: String = "PENDIENTE",
    val observacion: String = "",
    val revisadoPor: String = "",
    val fechaSolicitud: String = ""
)
