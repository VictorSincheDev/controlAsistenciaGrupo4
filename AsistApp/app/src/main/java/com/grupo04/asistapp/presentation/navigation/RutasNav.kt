package com.grupo04.asistapp.presentation.navigation

object RutasNav {
    const val LOGIN = "login"

    // Trabajador
    const val INICIO = "inicio"
    const val MARCACION = "marcacion/{tipo}"
    const val ASISTENCIA = "asistencia"
    const val TARDANZAS = "tardanzas"
    const val AUSENCIAS = "ausencias"
    const val HORARIO = "horario"
    const val PERMISOS = "permisos"
    const val PERMISO_FORM = "permiso_form?fecha={fecha}"
    const val PERFIL = "perfil"

    // Administrador
    const val REGISTROS = "admin_registros"
    const val REPORTES = "admin_reportes"
    const val ADMIN_PERMISOS = "admin_permisos"

    fun getTitulo(ruta: String?, tipoMarcacion: String?): String {
        return when {
            ruta == INICIO -> "AsistApp"
            ruta?.startsWith("marcacion") == true ->
                if (tipoMarcacion == "salida") "Registrar Salida de Turno" else "Registrar Ingreso"
            ruta == ASISTENCIA -> "Mi Historial de Asistencia"
            ruta == TARDANZAS -> "Mis Tardanzas"
            ruta == AUSENCIAS -> "Mis Ausencias"
            ruta == HORARIO -> "Mi Horario Laboral"
            ruta == PERMISOS -> "Mis Permisos"
            ruta?.startsWith("permiso_form") == true -> "Nueva Solicitud de Permiso"
            ruta == PERFIL -> "Mi Perfil"
            ruta == REGISTROS -> "Registros de Asistencia"
            ruta == REPORTES -> "Reportes de Asistencia"
            ruta == ADMIN_PERMISOS -> "Gestión de Permisos"
            else -> "AsistApp"
        }
    }

    // Pantallas secundarias que muestran la flecha de regreso
    fun mostrarBotonBack(ruta: String?): Boolean {
        return ruta?.startsWith("marcacion") == true ||
                ruta?.startsWith("permiso_form") == true ||
                ruta == TARDANZAS || ruta == AUSENCIAS || ruta == HORARIO
    }
}
