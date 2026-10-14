package com.grupo04.asistapp.presentation.screens.ausencias

import com.grupo04.asistapp.domain.model.Ausencia

data class AusenciasUiState(
    val ausencias: List<Ausencia> = emptyList(),
    val isLoading: Boolean = false
)
