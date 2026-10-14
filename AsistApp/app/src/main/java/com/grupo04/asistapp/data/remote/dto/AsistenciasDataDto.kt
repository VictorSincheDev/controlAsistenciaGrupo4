package com.grupo04.asistapp.data.remote.dto

data class AsistenciasDataDto(
    val items: List<AsistenciaDto>,
    val nextKey: String?
)
