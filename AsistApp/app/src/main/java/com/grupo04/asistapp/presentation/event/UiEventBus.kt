package com.grupo04.asistapp.presentation.event

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UiEventBus @Inject constructor() {
    private val _events = MutableSharedFlow<UiEvent>()
    val events = _events.asSharedFlow()

    suspend fun sendEvent(event: UiEvent) {
        _events.emit(event)
    }
}
