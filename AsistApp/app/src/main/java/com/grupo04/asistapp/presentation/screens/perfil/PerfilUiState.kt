package com.grupo04.asistapp.presentation.screens.perfil

import com.grupo04.asistapp.domain.model.Trabajador

data class PerfilUiState(
    val trabajador: Trabajador? = null,
    val showLogoutDialog: Boolean = false,
    val showReglamentoDialog: Boolean = false
)
