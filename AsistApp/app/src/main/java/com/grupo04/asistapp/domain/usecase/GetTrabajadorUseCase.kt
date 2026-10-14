package com.grupo04.asistapp.domain.usecase

import com.grupo04.asistapp.domain.model.Trabajador
import com.grupo04.asistapp.domain.repository.TrabajadorRepository
import javax.inject.Inject

class GetTrabajadorUseCase @Inject constructor(private val repository: TrabajadorRepository) {
    suspend operator fun invoke(id: String): Result<Trabajador> = repository.getTrabajadorById(id)
}
