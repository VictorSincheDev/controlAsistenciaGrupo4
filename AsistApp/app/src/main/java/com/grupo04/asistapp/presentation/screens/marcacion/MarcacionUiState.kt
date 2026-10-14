package com.grupo04.asistapp.presentation.screens.marcacion

import com.grupo04.asistapp.domain.model.Asistencia
import com.grupo04.asistapp.domain.model.Trabajador

data class MarcacionUiState(
    val tipo: String = "ingreso",            // "ingreso" (HU02) o "salida" (HU03)
    val trabajador: Trabajador? = null,
    val asistenciaHoy: Asistencia? = null,
    val horaActual: String = "",
    val resultado: Asistencia? = null,        // confirmación de la marcación realizada
    val showConfirmDialog: Boolean = false,
    val isLoading: Boolean = false
) {
    val esIngreso: Boolean get() = tipo != "salida"
}
