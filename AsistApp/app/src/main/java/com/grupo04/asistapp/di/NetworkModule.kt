package com.grupo04.asistapp.di

import com.grupo04.asistapp.data.remote.api.AsistenciaApiService
import com.grupo04.asistapp.data.remote.api.PermisoApiService
import com.grupo04.asistapp.data.remote.api.TrabajadorApiService
import com.grupo04.asistapp.data.repository.AsistenciaRepositoryImpl
import com.grupo04.asistapp.data.repository.PermisoRepositoryImpl
import com.grupo04.asistapp.data.repository.TrabajadorRepositoryImpl
import com.grupo04.asistapp.domain.repository.AsistenciaRepository
import com.grupo04.asistapp.domain.repository.PermisoRepository
import com.grupo04.asistapp.domain.repository.TrabajadorRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    // TODO: reemplaza por la URL de cada API Gateway (deben terminar en "/")
    private const val TRABAJADORES_BASE_URL = "https://3zel8b8aak.execute-api.us-east-1.amazonaws.com/"
    private const val ASISTENCIAS_BASE_URL = "https://7eah03ia4j.execute-api.us-east-1.amazonaws.com/"
    private const val PERMISOS_BASE_URL = "https://g5w81kw0p2.execute-api.us-east-1.amazonaws.com/"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }).build()
    }

    // --- Retrofit para Trabajadores (login y perfil) ---
    @Provides
    @Singleton
    @TrabajadoresRetrofit
    fun provideTrabajadoresRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(TRABAJADORES_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideTrabajadorApiService(@TrabajadoresRetrofit retrofit: Retrofit): TrabajadorApiService {
        return retrofit.create(TrabajadorApiService::class.java)
    }

    // --- Retrofit para Asistencias (marcaciones, ausencias y reportes) ---
    @Provides
    @Singleton
    @AsistenciasRetrofit
    fun provideAsistenciasRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ASISTENCIAS_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAsistenciaApiService(@AsistenciasRetrofit retrofit: Retrofit): AsistenciaApiService {
        return retrofit.create(AsistenciaApiService::class.java)
    }

    // --- Retrofit para Permisos ---
    @Provides
    @Singleton
    @PermisosRetrofit
    fun providePermisosRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(PERMISOS_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun providePermisoApiService(@PermisosRetrofit retrofit: Retrofit): PermisoApiService {
        return retrofit.create(PermisoApiService::class.java)
    }

    // --- Repositorios ---
    @Provides
    @Singleton
    fun provideTrabajadorRepository(api: TrabajadorApiService): TrabajadorRepository {
        return TrabajadorRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideAsistenciaRepository(api: AsistenciaApiService): AsistenciaRepository {
        return AsistenciaRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun providePermisoRepository(api: PermisoApiService): PermisoRepository {
        return PermisoRepositoryImpl(api)
    }
}
