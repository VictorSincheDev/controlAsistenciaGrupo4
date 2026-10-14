package com.grupo04.asistapp.domain.usecase

import com.grupo04.asistapp.domain.model.Permiso
import com.grupo04.asistapp.domain.repository.PermisoRepository
import javax.inject.Inject

class GetPermisosTrabajadorUseCase @Inject constructor(private val repository: PermisoRepository) {
    suspend operator fun invoke(trabajadorId: String): Result<List<Permiso>> = repository.getPermisosTrabajador(trabajadorId)
}
