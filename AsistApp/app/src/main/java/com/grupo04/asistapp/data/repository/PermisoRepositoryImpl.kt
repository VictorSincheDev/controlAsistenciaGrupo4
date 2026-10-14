package com.grupo04.asistapp.data.repository

import com.grupo04.asistapp.data.remote.api.PermisoApiService
import com.grupo04.asistapp.data.remote.dto.ActualizarPermisoDto
import com.grupo04.asistapp.data.remote.dto.InsertarPermisoDto
import com.grupo04.asistapp.domain.model.Permiso
import com.grupo04.asistapp.domain.model.TipoPermiso
import com.grupo04.asistapp.domain.repository.PermisoRepository
import javax.inject.Inject

class PermisoRepositoryImpl @Inject constructor(
    private val api: PermisoApiService
) : PermisoRepository {
    override suspend fun getTiposPermiso(): Result<List<TipoPermiso>> = runCatching {
        val response = api.getTiposPermiso()
        if (response.success && response.data != null) {
            response.data.items.map { it.toDomain() }
        } else {
            throw Exception(response.mensaje ?: "Error al cargar los tipos de permiso")
        }
    }

    override suspend fun getPermisos(): Result<List<Permiso>> = runCatching {
        val response = api.getPermisos()
        if (response.success && response.data != null) {
            response.data.items.map { it.toDomain() }
        } else {
            throw Exception(response.mensaje ?: "Error al cargar los permisos")
        }
    }

    override suspend fun getPermisosTrabajador(trabajadorId: String): Result<List<Permiso>> = runCatching {
        val response = api.getPermisosTrabajador(trabajadorId)
        if (response.success && response.data != null) {
            response.data.items.map { it.toDomain() }
        } else {
            throw Exception(response.mensaje ?: "Error al cargar tus permisos")
        }
    }

    override suspend fun insertarPermiso(permiso: Permiso): Result<Unit> = runCatching {
        val dto = InsertarPermisoDto(
            trabajadorId = permiso.trabajadorId,
            tipo = permiso.tipo,
            fechaInicio = permiso.fechaInicio,
            fechaFin = permiso.fechaFin,
            motivo = permiso.motivo
        )
        val response = api.insertarPermiso(dto)
        if (!response.success) {
            throw Exception(response.mensaje ?: "Error al enviar la solicitud")
        }
    }

    override suspend fun actualizarEstado(id: String, estado: String, observacion: String, revisadoPor: String): Result<Unit> = runCatching {
        val dto = ActualizarPermisoDto(estado = estado, observacion = observacion, revisadoPor = revisadoPor)
        val response = api.actualizarPermiso(id, dto)
        if (!response.success) {
            throw Exception(response.mensaje ?: "Error al actualizar el permiso")
        }
    }

}
