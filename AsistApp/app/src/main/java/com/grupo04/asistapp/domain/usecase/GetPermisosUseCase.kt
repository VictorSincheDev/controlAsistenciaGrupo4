package com.grupo04.asistapp.domain.usecase

import com.grupo04.asistapp.domain.model.Permiso
import com.grupo04.asistapp.domain.repository.PermisoRepository
import javax.inject.Inject

class GetPermisosUseCase @Inject constructor(private val repository: PermisoRepository) {
    suspend operator fun invoke(): Result<List<Permiso>> = repository.getPermisos()
}
