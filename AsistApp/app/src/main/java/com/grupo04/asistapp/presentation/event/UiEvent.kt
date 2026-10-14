package com.grupo04.asistapp.presentation.event

import com.grupo04.asistapp.presentation.common.SnackbarType

interface UiEvent {
    data class ShowSnackbar(val message: String, val type: SnackbarType) : UiEvent
}
