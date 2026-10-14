package com.grupo04.asistapp.data.remote.api

import com.grupo04.asistapp.data.remote.dto.ActualizarPermisoDto
import com.grupo04.asistapp.data.remote.dto.ApiResponseDto
import com.grupo04.asistapp.data.remote.dto.InsertarPermisoDto
import com.grupo04.asistapp.data.remote.dto.PermisoDto
import com.grupo04.asistapp.data.remote.dto.PermisosDataDto
import com.grupo04.asistapp.data.remote.dto.TiposPermisoDataDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PermisoApiService {
    @GET("tipos-permiso")
    suspend fun getTiposPermiso(): ApiResponseDto<TiposPermisoDataDto>

    @GET("permisos")
    suspend fun getPermisos(): ApiResponseDto<PermisosDataDto>

    @GET("permisos/trabajador/{trabajadorId}")
    suspend fun getPermisosTrabajador(@Path("trabajadorId") trabajadorId: String): ApiResponseDto<PermisosDataDto>

    @POST("permisos")
    suspend fun insertarPermiso(@Body permiso: InsertarPermisoDto): ApiResponseDto<PermisoDto>

    @PUT("permisos/{id}")
    suspend fun actualizarPermiso(@Path("id") id: String, @Body permiso: ActualizarPermisoDto): ApiResponseDto<PermisoDto>
}
