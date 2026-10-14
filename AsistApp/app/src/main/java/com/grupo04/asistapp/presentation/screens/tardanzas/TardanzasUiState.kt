package com.grupo04.asistapp.presentation.screens.tardanzas

import com.grupo04.asistapp.domain.model.Asistencia
import com.grupo04.asistapp.domain.model.Trabajador

data class TardanzasUiState(
    val trabajador: Trabajador? = null,
    val tardanzas: List<Asistencia> = emptyList(),
    val isLoading: Boolean = false
)
