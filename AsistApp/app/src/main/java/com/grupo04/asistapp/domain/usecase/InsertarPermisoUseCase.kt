package com.grupo04.asistapp.domain.usecase

import com.grupo04.asistapp.domain.model.Permiso
import com.grupo04.asistapp.domain.repository.PermisoRepository
import javax.inject.Inject

class InsertarPermisoUseCase @Inject constructor(private val repository: PermisoRepository) {
    suspend operator fun invoke(permiso: Permiso): Result<Unit> = repository.insertarPermiso(permiso)
}
