package com.grupo04.asistapp.presentation.screens.tardanzas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo04.asistapp.data.session.SesionManager
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

// HU05 - Consultar tardanzas
@HiltViewModel
class TardanzasViewModel @Inject constructor(
    private val sesionManager: SesionManager,
    private val getHistorialUseCase: GetHistorialUseCase,
    private val eventBus: UiEventBus
) : ViewModel() {
    private val _uiState = MutableStateFlow(TardanzasUiState())
    val uiState = _uiState.asStateFlow()

    fun cargarTardanzas() {
        val trabajador = sesionManager.trabajadorActual ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(trabajador = trabajador, isLoading = true) }
            getHistorialUseCase(trabajador.id)
                .onSuccess { lista ->
                    _uiState.update { it.copy(tardanzas = lista.filter { a -> a.esTardanza }, isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    eventBus.sendEvent(UiEvent.ShowSnackbar(error.message ?: "Error al cargar tus tardanzas", SnackbarType.ERROR))
                }
        }
    }
}
