package com.grupo04.asistapp.domain.usecase

import com.grupo04.asistapp.domain.model.Ausencia
import com.grupo04.asistapp.domain.repository.AsistenciaRepository
import javax.inject.Inject

class GetAusenciasUseCase @Inject constructor(private val repository: AsistenciaRepository) {
    suspend operator fun invoke(trabajadorId: String): Result<List<Ausencia>> = repository.getAusencias(trabajadorId)
}
