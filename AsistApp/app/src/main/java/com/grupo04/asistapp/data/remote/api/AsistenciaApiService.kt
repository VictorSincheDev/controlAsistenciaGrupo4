package com.grupo04.asistapp.data.remote.api

import com.grupo04.asistapp.data.remote.dto.ApiResponseDto
import com.grupo04.asistapp.data.remote.dto.AsistenciaDto
import com.grupo04.asistapp.data.remote.dto.AsistenciasDataDto
import com.grupo04.asistapp.data.remote.dto.AusenciasDataDto
import com.grupo04.asistapp.data.remote.dto.MarcacionDto
import com.grupo04.asistapp.data.remote.dto.ReportesDataDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AsistenciaApiService {
    @POST("asistencias/ingreso")
    suspend fun registrarIngreso(@Body marcacion: MarcacionDto): ApiResponseDto<AsistenciaDto>

    @POST("asistencias/salida")
    suspend fun registrarSalida(@Body marcacion: MarcacionDto): ApiResponseDto<AsistenciaDto>

    @GET("asistencias/hoy/{trabajadorId}")
    suspend fun getAsistenciaHoy(@Path("trabajadorId") trabajadorId: String): ApiResponseDto<AsistenciaDto>

    @GET("asistencias/trabajador/{trabajadorId}")
    suspend fun getHistorial(@Path("trabajadorId") trabajadorId: String): ApiResponseDto<AsistenciasDataDto>

    @GET("asistencias")
    suspend fun getAsistencias(): ApiResponseDto<AsistenciasDataDto>

    @GET("ausencias/{trabajadorId}")
    suspend fun getAusencias(@Path("trabajadorId") trabajadorId: String): ApiResponseDto<AusenciasDataDto>

    @GET("reportes/{desde}/{hasta}")
    suspend fun getReporte(@Path("desde") desde: String, @Path("hasta") hasta: String): ApiResponseDto<ReportesDataDto>
}
