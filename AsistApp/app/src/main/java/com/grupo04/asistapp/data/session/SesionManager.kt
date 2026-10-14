package com.grupo04.asistapp.data.session

import com.grupo04.asistapp.domain.model.Trabajador
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

// Guarda en memoria al trabajador que inició sesión (HU01)
@Singleton
class SesionManager @Inject constructor() {
    private val _trabajador = MutableStateFlow<Trabajador?>(null)
    val trabajador = _trabajador.asStateFlow()

    val trabajadorActual: Trabajador? get() = _trabajador.value

    fun iniciarSesion(trabajador: Trabajador) {
        _trabajador.value = trabajador
    }

    fun cerrarSesion() {
        _trabajador.value = null
    }
}
