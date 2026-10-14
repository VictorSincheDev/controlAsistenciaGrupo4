package com.grupo04.asistapp.presentation.screens.permisos

import com.grupo04.asistapp.domain.model.Permiso

data class PermisosUiState(
    val permisos: List<Permiso> = emptyList(),
    val filtro: String = "TODOS",   // TODOS, PENDIENTE, APROBADO, RECHAZADO
    val isLoading: Boolean = false
) {
    val permisosFiltrados: List<Permiso>
        get() = if (filtro == "TODOS") permisos else permisos.filter { it.estado == filtro }
}
