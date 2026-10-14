package com.grupo04.asistapp.presentation.screens.permisoform

import com.grupo04.asistapp.domain.model.TipoPermiso
import com.grupo04.asistapp.domain.model.Trabajador

data class PermisoFormUiState(
    val trabajador: Trabajador? = null,
    val tipo: String = "",
    val fechaInicio: String = "",
    val fechaFin: String = "",
    val motivo: String = "",
    val tipos: List<TipoPermiso> = emptyList(),
    val isLoading: Boolean = false
)
