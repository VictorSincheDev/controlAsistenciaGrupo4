package com.grupo04.asistapp.presentation.screens.asistencia

import com.grupo04.asistapp.domain.model.Asistencia
import com.grupo04.asistapp.domain.model.Ausencia

data class AsistenciaUiState(
    val historial: List<Asistencia> = emptyList(),
    val ausencias: List<Ausencia> = emptyList(),
    val filtro: String = "TODOS",   // TODOS, A_TIEMPO, TARDANZA
    val isLoading: Boolean = false
) {
    val historialFiltrado: List<Asistencia>
        get() = if (filtro == "TODOS") historial else historial.filter { it.estado == filtro }
}
