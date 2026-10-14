package com.grupo04.asistapp.domain.usecase

import com.grupo04.asistapp.domain.model.ReporteTrabajador
import com.grupo04.asistapp.domain.repository.AsistenciaRepository
import javax.inject.Inject

class GetReporteUseCase @Inject constructor(private val repository: AsistenciaRepository) {
    suspend operator fun invoke(desde: String, hasta: String): Result<List<ReporteTrabajador>> = repository.getReporte(desde, hasta)
}
