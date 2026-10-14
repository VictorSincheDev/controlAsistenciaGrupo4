package com.grupo04.asistapp.data.remote.dto

import com.grupo04.asistapp.domain.model.Asistencia

data class AsistenciaDto(
    val id: String?,
    val trabajadorId: String?,
    val trabajadorNombre: String?,
    val area: String?,
    val sede: String?,
    val fecha: String?,
    val horaIngreso: String?,
    val horaSalida: String?,
    val horarioIngreso: String?,
    val horarioSalida: String?,
    val estado: String?,
    val minutosTardanza: Int?,
    val minutosTrabajados: Int?
) {
    fun toDomain(): Asistencia {
        return Asistencia(
            id = id.orEmpty(),
            trabajadorId = trabajadorId.orEmpty(),
            trabajadorNombre = trabajadorNombre.orEmpty(),
            area = area.orEmpty(),
            sede = sede.orEmpty(),
            fecha = fecha.orEmpty(),
            horaIngreso = horaIngreso.orEmpty(),
            horaSalida = horaSalida.orEmpty(),
            horarioIngreso = horarioIngreso.orEmpty(),
            horarioSalida = horarioSalida.orEmpty(),
            estado = estado ?: "A_TIEMPO",
            minutosTardanza = minutosTardanza ?: 0,
            minutosTrabajados = minutosTrabajados ?: 0
        )
    }
}
