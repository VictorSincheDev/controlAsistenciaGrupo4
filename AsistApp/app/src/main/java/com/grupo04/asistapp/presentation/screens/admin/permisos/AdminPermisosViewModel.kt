package com.grupo04.asistapp.presentation.screens.admin.permisos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo04.asistapp.data.session.SesionManager
import com.grupo04.asistapp.domain.model.Permiso
import com.grupo04.asistapp.domain.usecase.ActualizarEstadoPermisoUseCase
import com.grupo04.asistapp.domain.usecase.GetPermisosUseCase
import com.grupo04.asistapp.presentation.common.SnackbarType
import com.grupo04.asistapp.presentation.event.UiEvent
import com.grupo04.asistapp.presentation.event.UiEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// El administrador aprueba o rechaza las solicitudes de permiso (complemento de HU08)
@HiltViewModel
class AdminPermisosViewModel @Inject constructor(
    private val sesionManager: SesionManager,
    private val getPermisosUseCase: GetPermisosUseCase,
    private val actualizarEstadoPermisoUseCase: ActualizarEstadoPermisoUseCase,
    private val eventBus: UiEventBus
) : ViewModel() {
    private val _uiState = MutableStateFlow(AdminPermisosUiState())
    val uiState = _uiState.asStateFlow()

    fun cargarPermisos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getPermisosUseCase()
                .onSuccess { lista -> _uiState.update { it.copy(permisos = lista, isLoading = false) } }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    eventBus.sendEvent(UiEvent.ShowSnackbar(error.message ?: "Error al cargar los permisos", SnackbarType.ERROR))
                }
        }
    }

    fun onFiltroChange(value: String) = _uiState.update { it.copy(filtro = value) }
    fun onObservacionChange(value: String) = _uiState.update { it.copy(observacion = value) }

    fun seleccionar(permiso: Permiso, accion: String) =
        _uiState.update { it.copy(permisoSeleccionado = permiso, accion = accion, observacion = "") }

    fun dismissDialog() = _uiState.update { it.copy(permisoSeleccionado = null, accion = "", observacion = "") }

    fun confirmar() {
        val state = _uiState.value
        val permiso = state.permisoSeleccionado ?: return
        if (state.accion == "RECHAZADO" && state.observacion.isBlank()) {
            viewModelScope.launch {
                eventBus.sendEvent(UiEvent.ShowSnackbar("Indica el motivo del rechazo", SnackbarType.WARNING))
            }
            return
        }
        val revisor = sesionManager.trabajadorActual?.nombres.orEmpty()

        viewModelScope.launch {
            dismissDialog()
            _uiState.update { it.copy(isLoading = true) }
            actualizarEstadoPermisoUseCase(permiso.id, state.accion, state.observacion.trim(), revisor)
                .onSuccess {
                    val mensaje = if (state.accion == "APROBADO") "Permiso aprobado" else "Permiso rechazado"
                    eventBus.sendEvent(UiEvent.ShowSnackbar(mensaje, SnackbarType.SUCCESS))
                    cargarPermisos()
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    eventBus.sendEvent(UiEvent.ShowSnackbar(error.message ?: "Error al actualizar el permiso", SnackbarType.ERROR))
                }
        }
    }
}
