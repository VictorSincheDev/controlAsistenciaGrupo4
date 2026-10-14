package com.grupo04.asistapp.domain.usecase

import com.grupo04.asistapp.domain.model.Asistencia
import com.grupo04.asistapp.domain.repository.AsistenciaRepository
import javax.inject.Inject

class GetAsistenciasUseCase @Inject constructor(private val repository: AsistenciaRepository) {
    suspend operator fun invoke(): Result<List<Asistencia>> = repository.getAsistencias()
}
