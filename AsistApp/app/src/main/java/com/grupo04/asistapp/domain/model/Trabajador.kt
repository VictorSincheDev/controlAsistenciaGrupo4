package com.grupo04.asistapp.domain.model

data class Trabajador(
    val id: String,
    val dni: String,
    val nombres: String,
    val cargo: String,
    val area: String,
    val sede: String,
    val correo: String,
    val supervisor: String,
    val rol: String,
    val fechaIngresoEmpresa: String,
    val horaIngreso: String,
    val horaSalida: String,
    val horaSalidaSabado: String,
    val toleranciaMin: Int,
    val refrigerio: String,
    val turno: String,
    val regimen: String
) {
    val esAdmin: Boolean get() = rol == "ADMIN"
    val trabajaSabado: Boolean get() = horaSalidaSabado.isNotBlank()
}
