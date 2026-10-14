package com.grupo04.asistapp.data.remote.dto

import com.grupo04.asistapp.domain.model.Trabajador

data class TrabajadorDto(
    val id: String?,
    val dni: String?,
    val nombres: String?,
    val cargo: String?,
    val area: String?,
    val sede: String?,
    val correo: String?,
    val supervisor: String?,
    val rol: String?,
    val fechaIngresoEmpresa: String?,
    val horaIngreso: String?,
    val horaSalida: String?,
    val horaSalidaSabado: String?,
    val toleranciaMin: Int?,
    val refrigerio: String?,
    val turno: String?,
    val regimen: String?
) {
    fun toDomain(): Trabajador {
        return Trabajador(
            id = id.orEmpty(),
            dni = dni.orEmpty(),
            nombres = nombres.orEmpty(),
            cargo = cargo.orEmpty(),
            area = area.orEmpty(),
            sede = sede.orEmpty(),
            correo = correo.orEmpty(),
            supervisor = supervisor.orEmpty(),
            rol = rol ?: "TRABAJADOR",
            fechaIngresoEmpresa = fechaIngresoEmpresa.orEmpty(),
            horaIngreso = horaIngreso ?: "08:00",
            horaSalida = horaSalida ?: "17:00",
            horaSalidaSabado = horaSalidaSabado.orEmpty(),
            toleranciaMin = toleranciaMin ?: 10,
            refrigerio = refrigerio.orEmpty(),
            turno = turno.orEmpty(),
            regimen = regimen.orEmpty()
        )
    }
}
