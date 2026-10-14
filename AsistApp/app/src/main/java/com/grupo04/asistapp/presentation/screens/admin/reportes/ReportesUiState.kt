package com.grupo04.asistapp.presentation.screens.admin.reportes

import com.grupo04.asistapp.domain.model.ReporteTrabajador

data class ReportesUiState(
    val desde: String = "",
    val hasta: String = "",
    val items: List<ReporteTrabajador> = emptyList(),
    val generado: Boolean = false,
    val isLoading: Boolean = false
)
