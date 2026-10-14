package com.grupo04.asistapp.domain.repository

import com.grupo04.asistapp.domain.model.Trabajador

interface TrabajadorRepository {
    suspend fun login(dni: String, password: String): Result<Trabajador>
    suspend fun getTrabajadorById(id: String): Result<Trabajador>
}
