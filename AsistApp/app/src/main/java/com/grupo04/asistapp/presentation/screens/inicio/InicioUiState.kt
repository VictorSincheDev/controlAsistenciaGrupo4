package com.grupo04.asistapp.presentation.screens.inicio

import com.grupo04.asistapp.domain.model.Asistencia
import com.grupo04.asistapp.domain.model.Ausencia
import com.grupo04.asistapp.domain.model.Trabajador

data class InicioUiState(
    val trabajador: Trabajador? = null,
    val asistenciaHoy: Asistencia? = null,
    val historial: List<Asistencia> = emptyList(),
    val ausencias: List<Ausencia> = emptyList(),
    val isLoading: Boolean = false
)
