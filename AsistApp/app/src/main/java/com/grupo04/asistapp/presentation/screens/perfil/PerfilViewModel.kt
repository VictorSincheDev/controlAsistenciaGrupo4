package com.grupo04.asistapp.presentation.screens.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo04.asistapp.data.session.SesionManager
import com.grupo04.asistapp.presentation.common.SnackbarType
import com.grupo04.asistapp.presentation.event.UiEvent
import com.grupo04.asistapp.presentation.event.UiEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PerfilViewModel @Inject constructor(
    private val sesionManager: SesionManager,
    private val eventBus: UiEventBus
) : ViewModel() {
    private val _uiState = MutableStateFlow(PerfilUiState(trabajador = sesionManager.trabajadorActual))
    val uiState = _uiState.asStateFlow()

    fun showLogoutDialog() = _uiState.update { it.copy(showLogoutDialog = true) }
    fun dismissLogoutDialog() = _uiState.update { it.copy(showLogoutDialog = false) }
    fun showReglamento() = _uiState.update { it.copy(showReglamentoDialog = true) }
    fun dismissReglamento() = _uiState.update { it.copy(showReglamentoDialog = false) }

    // Al limpiar la sesión, MainActivity redirige al Login
    fun cerrarSesion() {
        dismissLogoutDialog()
        viewModelScope.launch {
            eventBus.sendEvent(UiEvent.ShowSnackbar("Sesión cerrada correctamente", SnackbarType.INFO))
        }
        sesionManager.cerrarSesion()
    }
}
