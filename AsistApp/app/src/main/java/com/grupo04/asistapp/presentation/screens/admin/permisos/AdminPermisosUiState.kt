package com.grupo04.asistapp.presentation.screens.admin.permisos

import com.grupo04.asistapp.domain.model.Permiso

data class AdminPermisosUiState(
    val permisos: List<Permiso> = emptyList(),
    val filtro: String = "PENDIENTE",   // TODOS, PENDIENTE, APROBADO, RECHAZADO
    val permisoSeleccionado: Permiso? = null,
    val accion: String = "",            // APROBADO o RECHAZADO
    val observacion: String = "",
    val isLoading: Boolean = false
) {
    val permisosFiltrados: List<Permiso>
        get() = if (filtro == "TODOS") permisos else permisos.filter { it.estado == filtro }
}
