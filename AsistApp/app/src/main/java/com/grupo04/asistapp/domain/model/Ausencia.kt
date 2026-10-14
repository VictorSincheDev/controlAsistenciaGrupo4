package com.grupo04.asistapp.domain.model

data class Ausencia(
    val fecha: String,
    val dia: String,
    val tipo: String,
    val motivo: String
) {
    val esJustificada: Boolean get() = tipo == "JUSTIFICADA"
}
