package com.grupo04.asistapp.domain.usecase

import com.grupo04.asistapp.domain.model.Asistencia
import com.grupo04.asistapp.domain.repository.AsistenciaRepository
import javax.inject.Inject

class GetHistorialUseCase @Inject constructor(private val repository: AsistenciaRepository) {
    suspend operator fun invoke(trabajadorId: String): Result<List<Asistencia>> = repository.getHistorial(trabajadorId)
}
