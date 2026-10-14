package com.grupo04.asistapp.presentation.screens.admin.registros

import com.grupo04.asistapp.domain.model.Asistencia

data class RegistrosUiState(
    val query: String = "",
    val fecha: String = "",
    val filtro: String = "TODOS",   // TODOS, A_TIEMPO, TARDANZA
    val allAsistencias: List<Asistencia> = emptyList(),
    val isLoading: Boolean = false
) {
    val filteredAsistencias: List<Asistencia>
        get() = allAsistencias.filter { a ->
            (query.isBlank() || a.trabajadorNombre.contains(query, ignoreCase = true) || a.area.contains(query, ignoreCase = true)) &&
                    (fecha.isBlank() || a.fecha.startsWith(fecha)) &&
                    (filtro == "TODOS" || a.estado == filtro)
        }
}
