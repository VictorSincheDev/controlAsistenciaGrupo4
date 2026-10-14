package com.grupo04.asistapp.domain.usecase

import com.grupo04.asistapp.domain.repository.PermisoRepository
import javax.inject.Inject

class ActualizarEstadoPermisoUseCase @Inject constructor(private val repository: PermisoRepository) {
    suspend operator fun invoke(id: String, estado: String, observacion: String, revisadoPor: String): Result<Unit> = repository.actualizarEstado(id, estado, observacion, revisadoPor)
}
