package com.grupo04.asistapp.data.repository

import com.grupo04.asistapp.data.remote.api.TrabajadorApiService
import com.grupo04.asistapp.data.remote.dto.LoginDto
import com.grupo04.asistapp.domain.model.Trabajador
import com.grupo04.asistapp.domain.repository.TrabajadorRepository
import javax.inject.Inject

class TrabajadorRepositoryImpl @Inject constructor(
    private val api: TrabajadorApiService
) : TrabajadorRepository {
    override suspend fun login(dni: String, password: String): Result<Trabajador> = runCatching {
        val response = api.login(LoginDto(dni = dni, password = password))
        if (response.success && response.data != null) {
            response.data.toDomain()
        } else {
            throw Exception(response.mensaje ?: "No se pudo iniciar sesión")
        }
    }

    override suspend fun getTrabajadorById(id: String): Result<Trabajador> = runCatching {
        val response = api.getTrabajadorById(id)
        if (response.success && response.data != null) {
            response.data.toDomain()
        } else {
            throw Exception(response.mensaje ?: "No se pudo obtener los datos del trabajador")
        }
    }
}
