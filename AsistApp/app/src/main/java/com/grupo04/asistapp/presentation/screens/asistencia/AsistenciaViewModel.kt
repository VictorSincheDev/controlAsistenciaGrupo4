package com.grupo04.asistapp.presentation.screens.asistencia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo04.asistapp.data.session.SesionManager
import com.grupo04.asistapp.domain.usecase.GetAusenciasUseCase
import com.grupo04.asistapp.domain.usecase.GetHistorialUseCase
import com.grupo04.asistapp.presentation.common.SnackbarType
import com.grupo04.asistapp.presentation.event.UiEvent
import com.grupo04.asistapp.presentation.event.UiEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// HU04 - Consultar historial de asistencia
@HiltViewModel
class AsistenciaViewModel @Inject constructor(
    private val sesionManager: SesionManager,
    private val getHistorialUseCase: GetHistorialUseCase,
    private val getAusenciasUseCase: GetAusenciasUseCase,
    private val eventBus: UiEventBus
) : ViewModel() {
    private val _uiState = MutableStateFlow(AsistenciaUiState())
    val uiState = _uiState.asStateFlow()

    fun cargarHistorial() {
        val trabajador = sesionManager.trabajadorActual ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getHistorialUseCase(trabajador.id)
                .onSuccess { lista -> _uiState.update { it.copy(historial = lista) } }
                .onFailure { error ->
                    eventBus.sendEvent(UiEvent.ShowSnackbar(error.message ?: "Error al cargar el historial", SnackbarType.ERROR))
                }
            getAusenciasUseCase(trabajador.id)
                .onSuccess { lista -> _uiState.update { it.copy(ausencias = lista) } }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onFiltroChange(filtro: String) = _uiState.update { it.copy(filtro = filtro) }
}
