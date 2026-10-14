package com.grupo04.asistapp.data.remote.dto

import com.grupo04.asistapp.domain.model.ReporteTrabajador

data class ReporteDto(
    val trabajadorId: String?,
    val nombres: String?,
    val dni: String?,
    val area: String?,
    val diasAsistidos: Int?,
    val aTiempo: Int?,
    val tardanzas: Int?,
    val minutosTardanza: Int?,
    val ausenciasInjustificadas: Int?,
    val ausenciasJustificadas: Int?
) {
    fun toDomain(): ReporteTrabajador {
        return ReporteTrabajador(
            trabajadorId = trabajadorId.orEmpty(),
            nombres = nombres.orEmpty(),
            dni = dni.orEmpty(),
            area = area.orEmpty(),
            diasAsistidos = diasAsistidos ?: 0,
            aTiempo = aTiempo ?: 0,
            tardanzas = tardanzas ?: 0,
            minutosTardanza = minutosTardanza ?: 0,
            ausenciasInjustificadas = ausenciasInjustificadas ?: 0,
            ausenciasJustificadas = ausenciasJustificadas ?: 0
        )
    }
}
