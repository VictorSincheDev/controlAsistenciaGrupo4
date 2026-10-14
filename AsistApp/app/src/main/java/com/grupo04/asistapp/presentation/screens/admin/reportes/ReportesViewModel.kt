package com.grupo04.asistapp.presentation.screens.admin.reportes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo04.asistapp.domain.usecase.GetReporteUseCase
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

// HU10 - El administrador genera reportes de asistencia
@HiltViewModel
class ReportesViewModel @Inject constructor(
    private val getReporteUseCase: GetReporteUseCase,
    private val eventBus: UiEventBus
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReportesUiState(desde = Formato.inicioDeMes(), hasta = Formato.hoy()))
    val uiState = _uiState.asStateFlow()

    init {
        generarReporte()
    }

    fun onDesdeChange(value: String) = _uiState.update { it.copy(desde = value.take(10)) }
    fun onHastaChange(value: String) = _uiState.update { it.copy(hasta = value.take(10)) }

    fun generarReporte() {
        val state = _uiState.value
        if (!Formato.esFechaValida(state.desde) || !Formato.esFechaValida(state.hasta)) {
            viewModelScope.launch {
                eventBus.sendEvent(UiEvent.ShowSnackbar("Las fechas deben tener el formato AAAA-MM-DD", SnackbarType.WARNING))
            }
            return
        }
        if (state.desde > state.hasta) {
            viewModelScope.launch {
                eventBus.sendEvent(UiEvent.ShowSnackbar("La fecha inicial no puede ser mayor a la final", SnackbarType.WARNING))
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getReporteUseCase(state.desde, state.hasta)
                .onSuccess { items ->
                    _uiState.update { it.copy(items = items, generado = true, isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    eventBus.sendEvent(UiEvent.ShowSnackbar(error.message ?: "Error al generar el reporte", SnackbarType.ERROR))
                }
        }
    }
}
