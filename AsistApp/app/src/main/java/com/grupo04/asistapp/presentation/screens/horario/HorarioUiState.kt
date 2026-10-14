package com.grupo04.asistapp.presentation.screens.horario

import com.grupo04.asistapp.domain.model.Trabajador

data class HorarioUiState(
    val trabajador: Trabajador? = null,
    val isLoading: Boolean = false
)
