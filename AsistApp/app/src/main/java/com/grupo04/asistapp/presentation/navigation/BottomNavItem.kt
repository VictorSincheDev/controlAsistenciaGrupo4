package com.grupo04.asistapp.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    // Navegación del trabajador (4 secciones del prototipo de Stitch)
    object Inicio : BottomNavItem(RutasNav.INICIO, "Inicio", Icons.Default.Home)
    object Asistencia : BottomNavItem(RutasNav.ASISTENCIA, "Asistencia", Icons.Default.CalendarMonth)
    object Permisos : BottomNavItem(RutasNav.PERMISOS, "Permisos", Icons.AutoMirrored.Filled.Assignment)
    object Perfil : BottomNavItem(RutasNav.PERFIL, "Perfil", Icons.Default.Person)

    // Navegación del administrador
    object Registros : BottomNavItem(RutasNav.REGISTROS, "Registros", Icons.Default.HowToReg)
    object Reportes : BottomNavItem(RutasNav.REPORTES, "Reportes", Icons.Default.Assessment)
    object AdminPermisos : BottomNavItem(RutasNav.ADMIN_PERMISOS, "Permisos", Icons.AutoMirrored.Filled.Assignment)

    // Rutas con parámetros
    object Marcacion : BottomNavItem(RutasNav.MARCACION, "Marcación", Icons.Default.Fingerprint) {
        fun passTipo(tipo: String): String = "marcacion/$tipo"
    }
    object PermisoForm : BottomNavItem(RutasNav.PERMISO_FORM, "Solicitar", Icons.AutoMirrored.Filled.Assignment) {
        fun passFecha(fecha: String? = null): String = if (fecha != null) "permiso_form?fecha=$fecha" else "permiso_form"
    }
}
