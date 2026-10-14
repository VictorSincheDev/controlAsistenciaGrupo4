package com.grupo04.asistapp.data.remote.dto

import com.grupo04.asistapp.domain.model.Ausencia

data class AusenciaDto(
    val fecha: String?,
    val dia: String?,
    val tipo: String?,
    val motivo: String?
) {
    fun toDomain(): Ausencia {
        return Ausencia(
            fecha = fecha.orEmpty(),
            dia = dia.orEmpty(),
            tipo = tipo ?: "INJUSTIFICADA",
            motivo = motivo.orEmpty()
        )
    }
}
