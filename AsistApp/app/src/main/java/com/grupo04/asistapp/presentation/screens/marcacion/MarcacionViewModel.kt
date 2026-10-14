package com.grupo04.asistapp.presentation.screens.marcacion

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo04.asistapp.data.session.SesionManager
import com.grupo04.asistapp.domain.usecase.GetAsistenciaHoyUseCase
import com.grupo04.asistapp.domain.usecase.RegistrarIngresoUseCase
import com.grupo04.asistapp.domain.usecase.RegistrarSalidaUseCase
import com.grupo04.asistapp.presentation.common.Formato
import com.grupo04.asistapp.presentation.common.SnackbarType
import com.grupo04.asistapp.presentation.event.UiEvent
import com.grupo04.asistapp.presentation.event.UiEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// HU02 - Registrar ingreso  |  HU03 - Registrar salida
@HiltViewModel
class MarcacionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sesionManager: SesionManager,
    private val getAsistenciaHoyUseCase: GetAsistenciaHoyUseCase,
    private val registrarIngresoUseCase: RegistrarIngresoUseCase,
    private val registrarSalidaUseCase: RegistrarSalidaUseCase,
    private val eventBus: UiEventBus
) : ViewModel() {
    private val _uiState = MutableStateFlow(MarcacionUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val tipo: String = savedStateHandle["tipo"] ?: "ingreso"
        _uiState.update { it.copy(tipo = tipo, trabajador = sesionManager.trabajadorActual) }
        iniciarReloj()
        cargarAsistenciaHoy()
    }

    // Reloj en pantalla (la hora oficial la pone el servidor al registrar)
    private fun iniciarReloj() {
        viewModelScope.launch {
            while (true) {
                _uiState.update { it.copy(horaActual = Formato.horaActual()) }
                delay(1000)
            }
        }
    }

    private fun cargarAsistenciaHoy() {
        val trabajador = sesionManager.trabajadorActual ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getAsistenciaHoyUseCase(trabajador.id)
                .onSuccess { asistencia -> _uiState.update { it.copy(asistenciaHoy = asistencia, isLoading = false) } }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    eventBus.sendEvent(UiEvent.ShowSnackbar(error.message ?: "Error al consultar tu asistencia", SnackbarType.ERROR))
                }
        }
    }

    fun showConfirmDialog() = _uiState.update { it.copy(showConfirmDialog = true) }
    fun dismissConfirmDialog() = _uiState.update { it.copy(showConfirmDialog = false) }

    fun confirmarMarcacion() {
        val trabajador = sesionManager.trabajadorActual ?: return
        val esIngreso = _uiState.value.esIngreso
        viewModelScope.launch {
            dismissConfirmDialog()
            _uiState.update { it.copy(isLoading = true) }
            val result = if (esIngreso) registrarIngresoUseCase(trabajador.id) else registrarSalidaUseCase(trabajador.id)

            result.onSuccess { asistencia ->
                _uiState.update { it.copy(isLoading = false, resultado = asistencia, asistenciaHoy = asistencia) }
                val mensaje = if (esIngreso) "Ingreso registrado a las ${Formato.hora12(asistencia.horaIngreso)}"
                else "Salida registrada a las ${Formato.hora12(asistencia.horaSalida)}"
                eventBus.sendEvent(UiEvent.ShowSnackbar(mensaje, SnackbarType.SUCCESS))
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false) }
                eventBus.sendEvent(UiEvent.ShowSnackbar(error.message ?: "No se pudo registrar la marcación", SnackbarType.ERROR))
            }
        }
    }
}
