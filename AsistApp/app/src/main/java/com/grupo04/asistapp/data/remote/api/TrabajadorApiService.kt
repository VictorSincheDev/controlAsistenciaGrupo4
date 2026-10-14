package com.grupo04.asistapp.data.remote.api

import com.grupo04.asistapp.data.remote.dto.ApiResponseDto
import com.grupo04.asistapp.data.remote.dto.LoginDto
import com.grupo04.asistapp.data.remote.dto.TrabajadorDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TrabajadorApiService {
    @POST("login")
    suspend fun login(@Body login: LoginDto): ApiResponseDto<TrabajadorDto>

    @GET("trabajadores/{id}")
    suspend fun getTrabajadorById(@Path("id") id: String): ApiResponseDto<TrabajadorDto>
}
