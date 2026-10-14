package com.grupo04.asistapp.presentation.screens.permisos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo04.asistapp.data.session.SesionManager
import com.grupo04.asistapp.domain.usecase.GetPermisosTrabajadorUseCase
import com.grupo04.asistapp.presentation.common.SnackbarType
import com.grupo04.asistapp.presentation.event.UiEvent
import com.grupo04.asistapp.presentation.event.UiEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// HU08 - Mis solicitudes de permiso
@HiltViewModel
class PermisosViewModel @Inject constructor(
    private val sesionManager: SesionManager,
    private val getPermisosTrabajadorUseCase: GetPermisosTrabajadorUseCase,
    private val eventBus: UiEventBus
) : ViewModel() {
    private val _uiState = MutableStateFlow(PermisosUiState())
    val uiState = _uiState.asStateFlow()

    fun cargarPermisos() {
        val trabajador = sesionManager.trabajadorActual ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getPermisosTrabajadorUseCase(trabajador.id)
                .onSuccess { lista -> _uiState.update { it.copy(permisos = lista, isLoading = false) } }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    eventBus.sendEvent(UiEvent.ShowSnackbar(error.message ?: "Error al cargar tus permisos", SnackbarType.ERROR))
                }
        }
    }

    fun onFiltroChange(filtro: String) = _uiState.update { it.copy(filtro = filtro) }

}
