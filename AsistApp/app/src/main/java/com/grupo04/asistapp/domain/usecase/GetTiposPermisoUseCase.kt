package com.grupo04.asistapp.domain.usecase

import com.grupo04.asistapp.domain.model.TipoPermiso
import com.grupo04.asistapp.domain.repository.PermisoRepository
import javax.inject.Inject

class GetTiposPermisoUseCase @Inject constructor(private val repository: PermisoRepository) {
    suspend operator fun invoke(): Result<List<TipoPermiso>> = repository.getTiposPermiso()
}
