package com.grupo04.asistapp.presentation.screens.horario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo04.asistapp.data.session.SesionManager
import com.grupo04.asistapp.domain.usecase.GetTrabajadorUseCase
import com.grupo04.asistapp.presentation.common.SnackbarType
import com.grupo04.asistapp.presentation.event.UiEvent
import com.grupo04.asistapp.presentation.event.UiEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// HU07 - Consultar horario laboral (se vuelve a consultar en la nube por si RRHH lo cambió)
@HiltViewModel
class HorarioViewModel @Inject constructor(
    private val sesionManager: SesionManager,
    private val getTrabajadorUseCase: GetTrabajadorUseCase,
    private val eventBus: UiEventBus
) : ViewModel() {
    private val _uiState = MutableStateFlow(HorarioUiState())
    val uiState = _uiState.asStateFlow()

    fun cargarHorario() {
        val actual = sesionManager.trabajadorActual ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(trabajador = actual, isLoading = true) }
            getTrabajadorUseCase(actual.id)
                .onSuccess { trabajador ->
                    sesionManager.iniciarSesion(trabajador)
                    _uiState.update { it.copy(trabajador = trabajador, isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    eventBus.sendEvent(UiEvent.ShowSnackbar(error.message ?: "Error al actualizar tu horario", SnackbarType.ERROR))
                }
        }
    }
}
