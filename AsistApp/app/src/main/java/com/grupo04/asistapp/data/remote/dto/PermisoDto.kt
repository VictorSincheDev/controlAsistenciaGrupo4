package com.grupo04.asistapp.data.remote.dto

import com.grupo04.asistapp.domain.model.Permiso

data class PermisoDto(
    val id: String?,
    val codigo: String?,
    val trabajadorId: String?,
    val trabajadorNombre: String?,
    val area: String?,
    val tipo: String?,
    val fechaInicio: String?,
    val fechaFin: String?,
    val motivo: String?,
    val estado: String?,
    val observacion: String?,
    val revisadoPor: String?,
    val fechaSolicitud: String?
) {
    fun toDomain(): Permiso {
        return Permiso(
            id = id.orEmpty(),
            codigo = codigo.orEmpty(),
            trabajadorId = trabajadorId.orEmpty(),
            trabajadorNombre = trabajadorNombre.orEmpty(),
            area = area.orEmpty(),
            tipo = tipo.orEmpty(),
            fechaInicio = fechaInicio.orEmpty(),
            fechaFin = fechaFin.orEmpty(),
            motivo = motivo.orEmpty(),
            estado = estado ?: "PENDIENTE",
            observacion = observacion.orEmpty(),
            revisadoPor = revisadoPor.orEmpty(),
            fechaSolicitud = fechaSolicitud.orEmpty()
        )
    }
}
