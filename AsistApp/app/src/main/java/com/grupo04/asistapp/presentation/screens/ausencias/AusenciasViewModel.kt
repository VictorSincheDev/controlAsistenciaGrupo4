package com.grupo04.asistapp.presentation.screens.ausencias

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo04.asistapp.data.session.SesionManager
import com.grupo04.asistapp.domain.usecase.GetAusenciasUseCase
import com.grupo04.asistapp.presentation.common.SnackbarType
import com.grupo04.asistapp.presentation.event.UiEvent
import com.grupo04.asistapp.presentation.event.UiEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// HU06 - Consultar ausencias
@HiltViewModel
class AusenciasViewModel @Inject constructor(
    private val sesionManager: SesionManager,
    private val getAusenciasUseCase: GetAusenciasUseCase,
    private val eventBus: UiEventBus
) : ViewModel() {
    private val _uiState = MutableStateFlow(AusenciasUiState())
    val uiState = _uiState.asStateFlow()

    fun cargarAusencias() {
        val trabajador = sesionManager.trabajadorActual ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getAusenciasUseCase(trabajador.id)
                .onSuccess { lista -> _uiState.update { it.copy(ausencias = lista, isLoading = false) } }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    eventBus.sendEvent(UiEvent.ShowSnackbar(error.message ?: "Error al cargar tus ausencias", SnackbarType.ERROR))
                }
        }
    }
}
