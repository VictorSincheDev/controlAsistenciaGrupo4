package com.grupo04.asistapp.data.remote.dto

import com.grupo04.asistapp.domain.model.TipoPermiso

data class TipoPermisoDto(val id: String?, val nombre: String?) {
    fun toDomain(): TipoPermiso {
        return TipoPermiso(
            id = id.orEmpty(),
            nombre = nombre.orEmpty()
        )
    }
}
