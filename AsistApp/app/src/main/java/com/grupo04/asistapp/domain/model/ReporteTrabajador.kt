package com.grupo04.asistapp.domain.model

data class ReporteTrabajador(
    val trabajadorId: String,
    val nombres: String,
    val dni: String,
    val area: String,
    val diasAsistidos: Int,
    val aTiempo: Int,
    val tardanzas: Int,
    val minutosTardanza: Int,
    val ausenciasInjustificadas: Int,
    val ausenciasJustificadas: Int
) {
    val puntualidad: Int get() = if (diasAsistidos == 0) 0 else (aTiempo * 100) / diasAsistidos
}
