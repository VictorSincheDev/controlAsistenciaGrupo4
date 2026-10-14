package com.grupo04.asistapp.domain.repository

import com.grupo04.asistapp.domain.model.Permiso
import com.grupo04.asistapp.domain.model.TipoPermiso

interface PermisoRepository {
    suspend fun getTiposPermiso(): Result<List<TipoPermiso>>
    suspend fun getPermisos(): Result<List<Permiso>>
    suspend fun getPermisosTrabajador(trabajadorId: String): Result<List<Permiso>>
    suspend fun insertarPermiso(permiso: Permiso): Result<Unit>
    suspend fun actualizarEstado(id: String, estado: String, observacion: String, revisadoPor: String): Result<Unit>
}
