package com.grupo04.asistapp.domain.repository

import com.grupo04.asistapp.domain.model.Asistencia
import com.grupo04.asistapp.domain.model.Ausencia
import com.grupo04.asistapp.domain.model.ReporteTrabajador

interface AsistenciaRepository {
    suspend fun registrarIngreso(trabajadorId: String): Result<Asistencia>
    suspend fun registrarSalida(trabajadorId: String): Result<Asistencia>
    suspend fun getAsistenciaHoy(trabajadorId: String): Result<Asistencia?>
    suspend fun getHistorial(trabajadorId: String): Result<List<Asistencia>>
    suspend fun getAsistencias(): Result<List<Asistencia>>
    suspend fun getAusencias(trabajadorId: String): Result<List<Ausencia>>
    suspend fun getReporte(desde: String, hasta: String): Result<List<ReporteTrabajador>>
}
