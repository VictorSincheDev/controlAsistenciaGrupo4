package com.grupo04.asistapp.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TrabajadoresRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AsistenciasRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PermisosRetrofit
