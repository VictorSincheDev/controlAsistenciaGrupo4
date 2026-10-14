package com.grupo04.asistapp.domain.model

data class Asistencia(
    val id: String = "",
    val trabajadorId: String,
    val trabajadorNombre: String,
    val area: String,
    val sede: String,
    val fecha: String,
    val horaIngreso: String,
    val horaSalida: String,
    val horarioIngreso: String,
    val horarioSalida: String,
    val estado: String,
    val minutosTardanza: Int,
    val minutosTrabajados: Int
) {
    val esTardanza: Boolean get() = estado == "TARDANZA"
    val tieneSalida: Boolean get() = horaSalida.isNotBlank()
}
