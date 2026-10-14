package com.grupo04.asistapp.presentation.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo04.asistapp.data.session.SesionManager
import com.grupo04.asistapp.domain.usecase.LoginUseCase
import com.grupo04.asistapp.presentation.common.Formato
import com.grupo04.asistapp.presentation.common.SnackbarType
import com.grupo04.asistapp.presentation.event.UiEvent
import com.grupo04.asistapp.presentation.event.UiEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// HU01 - Iniciar sesión
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val sesionManager: SesionManager,
    private val eventBus: UiEventBus
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun onDniChange(value: String) = _uiState.update { it.copy(dni = value.filter { c -> c.isDigit() }.take(8)) }
    fun onPasswordChange(value: String) = _uiState.update { it.copy(password = value) }
    fun onTogglePassword() = _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }

    fun iniciarSesion(onSuccess: (esAdmin: Boolean) -> Unit) {
        val state = _uiState.value

        if (state.dni.isBlank() || state.password.isBlank()) {
            viewModelScope.launch {
                eventBus.sendEvent(UiEvent.ShowSnackbar("Ingresa tu DNI y contraseña", SnackbarType.WARNING))
            }
            return
        }
        if (state.dni.length != 8) {
            viewModelScope.launch {
                eventBus.sendEvent(UiEvent.ShowSnackbar("El DNI debe tener 8 dígitos", SnackbarType.WARNING))
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            loginUseCase(state.dni, state.password)
                .onSuccess { trabajador ->
                    _uiState.update { LoginUiState() }
                    sesionManager.iniciarSesion(trabajador)
                    eventBus.sendEvent(
                        UiEvent.ShowSnackbar("Bienvenido(a), ${Formato.nombreCorto(trabajador.nombres)}", SnackbarType.SUCCESS)
                    )
                    onSuccess(trabajador.esAdmin)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    eventBus.sendEvent(
                        UiEvent.ShowSnackbar(error.message ?: "No se pudo iniciar sesión", SnackbarType.ERROR)
                    )
                }
        }
    }
}
