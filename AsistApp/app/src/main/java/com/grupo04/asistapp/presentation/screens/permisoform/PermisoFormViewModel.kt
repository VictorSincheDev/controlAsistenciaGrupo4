package com.grupo04.asistapp.presentation.screens.permisoform

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo04.asistapp.data.session.SesionManager
import com.grupo04.asistapp.domain.model.Permiso
import com.grupo04.asistapp.domain.usecase.GetTiposPermisoUseCase
import com.grupo04.asistapp.domain.usecase.InsertarPermisoUseCase
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

// HU08 - Solicitar un permiso desde la aplicación
@HiltViewModel
class PermisoFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sesionManager: SesionManager,
    private val getTiposPermisoUseCase: GetTiposPermisoUseCase,
    private val insertarPermisoUseCase: InsertarPermisoUseCase,
    private val eventBus: UiEventBus
) : ViewModel() {
    private val _uiState = MutableStateFlow(PermisoFormUiState())
    val uiState = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(trabajador = sesionManager.trabajadorActual) }
        cargarTipos()

        // Si viene desde "Mis Ausencias", la fecha ya llega cargada
        val fecha: String? = savedStateHandle["fecha"]
        if (!fecha.isNullOrEmpty() && fecha != "{fecha}") {
            _uiState.update { it.copy(fechaInicio = fecha, fechaFin = fecha) }
        }
    }

    private fun cargarTipos() {
        viewModelScope.launch {
            getTiposPermisoUseCase()
                .onSuccess { tipos -> _uiState.update { it.copy(tipos = tipos) } }
                .onFailure { error ->
                    eventBus.sendEvent(UiEvent.ShowSnackbar(error.message ?: "Error al cargar los tipos de permiso", SnackbarType.ERROR))
                }
        }
    }

    fun onTipoChange(value: String) = _uiState.update { it.copy(tipo = value) }
    fun onFechaInicioChange(value: String) = _uiState.update { it.copy(fechaInicio = value.take(10)) }
    fun onFechaFinChange(value: String) = _uiState.update { it.copy(fechaFin = value.take(10)) }
    fun onMotivoChange(value: String) = _uiState.update { it.copy(motivo = value) }

    fun enviarSolicitud(onSuccess: () -> Unit) {
        val state = _uiState.value
        val trabajador = state.trabajador ?: return

        if (state.tipo.isBlank() || state.fechaInicio.isBlank() || state.fechaFin.isBlank() || state.motivo.isBlank()) {
            viewModelScope.launch {
                eventBus.sendEvent(UiEvent.ShowSnackbar("Todos los campos son obligatorios", SnackbarType.WARNING))
            }
            return
        }
        if (!Formato.esFechaValida(state.fechaInicio) || !Formato.esFechaValida(state.fechaFin)) {
            viewModelScope.launch {
                eventBus.sendEvent(UiEvent.ShowSnackbar("Las fechas deben tener el formato AAAA-MM-DD", SnackbarType.WARNING))
            }
            return
        }
        if (state.fechaFin < state.fechaInicio) {
            viewModelScope.launch {
                eventBus.sendEvent(UiEvent.ShowSnackbar("La fecha de fin no puede ser anterior a la de inicio", SnackbarType.WARNING))
            }
            return
        }

        val permiso = Permiso(
            trabajadorId = trabajador.id,
            tipo = state.tipo,
            fechaInicio = state.fechaInicio,
            fechaFin = state.fechaFin,
            motivo = state.motivo.trim()
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            insertarPermisoUseCase(permiso)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    eventBus.sendEvent(
                        UiEvent.ShowSnackbar("¡Solicitud enviada! RRHH revisará tu permiso", SnackbarType.SUCCESS)
                    )
                    onSuccess()
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    eventBus.sendEvent(UiEvent.ShowSnackbar(error.message ?: "Error al enviar la solicitud", SnackbarType.ERROR))
                }
        }
    }

    fun cancelar(onSuccess: () -> Unit) {
        onSuccess()
    }
}
