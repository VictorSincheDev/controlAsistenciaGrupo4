package com.grupo04.asistapp.presentation.screens.login

data class LoginUiState(
    val dni: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val isLoading: Boolean = false
)
