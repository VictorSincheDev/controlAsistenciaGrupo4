package com.grupo04.asistapp.data.repository

import com.grupo04.asistapp.data.remote.api.AsistenciaApiService
import com.grupo04.asistapp.data.remote.dto.MarcacionDto
import com.grupo04.asistapp.domain.model.Asistencia
import com.grupo04.asistapp.domain.model.Ausencia
import com.grupo04.asistapp.domain.model.ReporteTrabajador
import com.grupo04.asistapp.domain.repository.AsistenciaRepository
import javax.inject.Inject

class AsistenciaRepositoryImpl @Inject constructor(
    private val api: AsistenciaApiService
) : AsistenciaRepository {
    override suspend fun registrarIngreso(trabajadorId: String): Result<Asistencia> = runCatching {
        val response = api.registrarIngreso(MarcacionDto(trabajadorId))
        if (response.success && response.data != null) {
            response.data.toDomain()
        } else {
            throw Exception(response.mensaje ?: "No se pudo registrar el ingreso")
        }
    }

    override suspend fun registrarSalida(trabajadorId: String): Result<Asistencia> = runCatching {
        val response = api.registrarSalida(MarcacionDto(trabajadorId))
        if (response.success && response.data != null) {
            response.data.toDomain()
        } else {
            throw Exception(response.mensaje ?: "No se pudo registrar la salida")
        }
    }

    override suspend fun getAsistenciaHoy(trabajadorId: String): Result<Asistencia?> = runCatching {
        val response = api.getAsistenciaHoy(trabajadorId)
        if (response.success) {
            response.data?.toDomain()
        } else {
            throw Exception(response.mensaje ?: "Error al consultar la asistencia de hoy")
        }
    }

    override suspend fun getHistorial(trabajadorId: String): Result<List<Asistencia>> = runCatching {
        val response = api.getHistorial(trabajadorId)
        if (response.success && response.data != null) {
            response.data.items.map { it.toDomain() }
        } else {
            throw Exception(response.mensaje ?: "Error al cargar el historial")
        }
    }

    override suspend fun getAsistencias(): Result<List<Asistencia>> = runCatching {
        val response = api.getAsistencias()
        if (response.success && response.data != null) {
            response.data.items.map { it.toDomain() }
        } else {
            throw Exception(response.mensaje ?: "Error al cargar los registros")
        }
    }

    override suspend fun getAusencias(trabajadorId: String): Result<List<Ausencia>> = runCatching {
        val response = api.getAusencias(trabajadorId)
        if (response.success && response.data != null) {
            response.data.items.map { it.toDomain() }
        } else {
            throw Exception(response.mensaje ?: "Error al cargar las ausencias")
        }
    }

    override suspend fun getReporte(desde: String, hasta: String): Result<List<ReporteTrabajador>> = runCatching {
        val response = api.getReporte(desde, hasta)
        if (response.success && response.data != null) {
            response.data.items.map { it.toDomain() }
        } else {
            throw Exception(response.mensaje ?: "Error al generar el reporte")
        }
    }
}
