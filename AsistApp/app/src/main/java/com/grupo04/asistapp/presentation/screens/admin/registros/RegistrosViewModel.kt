package com.grupo04.asistapp.presentation.screens.admin.registros

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo04.asistapp.domain.usecase.GetAsistenciasUseCase
import com.grupo04.asistapp.presentation.common.SnackbarType
import com.grupo04.asistapp.presentation.event.UiEvent
import com.grupo04.asistapp.presentation.event.UiEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// HU09 - El administrador consulta los registros de asistencia de los trabajadores
@HiltViewModel
class RegistrosViewModel @Inject constructor(
    private val getAsistenciasUseCase: GetAsistenciasUseCase,
    private val eventBus: UiEventBus
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegistrosUiState())
    val uiState = _uiState.asStateFlow()

    fun cargarRegistros() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getAsistenciasUseCase()
                .onSuccess { lista -> _uiState.update { it.copy(allAsistencias = lista, isLoading = false) } }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    eventBus.sendEvent(UiEvent.ShowSnackbar(error.message ?: "Error al cargar los registros", SnackbarType.ERROR))
                }
        }
    }

    fun onQueryChange(value: String) = _uiState.update { it.copy(query = value) }
    fun onFechaChange(value: String) = _uiState.update { it.copy(fecha = value.take(10)) }
    fun onFiltroChange(value: String) = _uiState.update { it.copy(filtro = value) }
}
