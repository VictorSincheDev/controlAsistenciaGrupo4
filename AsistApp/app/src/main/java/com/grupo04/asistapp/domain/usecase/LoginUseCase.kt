package com.grupo04.asistapp.domain.usecase

import com.grupo04.asistapp.domain.model.Trabajador
import com.grupo04.asistapp.domain.repository.TrabajadorRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val repository: TrabajadorRepository) {
    suspend operator fun invoke(dni: String, password: String): Result<Trabajador> = repository.login(dni, password)
}
