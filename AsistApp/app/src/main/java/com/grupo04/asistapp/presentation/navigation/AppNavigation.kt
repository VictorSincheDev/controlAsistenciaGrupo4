package com.grupo04.asistapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.grupo04.asistapp.presentation.screens.admin.permisos.AdminPermisosScreen
import com.grupo04.asistapp.presentation.screens.admin.registros.RegistrosScreen
import com.grupo04.asistapp.presentation.screens.admin.reportes.ReportesScreen
import com.grupo04.asistapp.presentation.screens.asistencia.AsistenciaScreen
import com.grupo04.asistapp.presentation.screens.ausencias.AusenciasScreen
import com.grupo04.asistapp.presentation.screens.horario.HorarioScreen
import com.grupo04.asistapp.presentation.screens.inicio.InicioScreen
import com.grupo04.asistapp.presentation.screens.login.LoginScreen
import com.grupo04.asistapp.presentation.screens.marcacion.MarcacionScreen
import com.grupo04.asistapp.presentation.screens.perfil.PerfilScreen
import com.grupo04.asistapp.presentation.screens.permisoform.PermisoFormScreen
import com.grupo04.asistapp.presentation.screens.permisos.PermisosScreen
import com.grupo04.asistapp.presentation.screens.tardanzas.TardanzasScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = RutasNav.LOGIN,
        modifier = Modifier
    ) {
        // HU01
        composable(RutasNav.LOGIN) {
            LoginScreen(
                onLoginSuccess = { esAdmin ->
                    navController.navigate(if (esAdmin) RutasNav.REGISTROS else RutasNav.INICIO) {
                        popUpTo(RutasNav.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // ---------------- Trabajador ----------------
        composable(BottomNavItem.Inicio.route) {
            InicioScreen(
                onMarcar = { tipo -> navController.navigate(BottomNavItem.Marcacion.passTipo(tipo)) },
                onHistorial = { navController.navigate(BottomNavItem.Asistencia.route) },
                onTardanzas = { navController.navigate(RutasNav.TARDANZAS) },
                onHorario = { navController.navigate(RutasNav.HORARIO) },
                onSolicitarPermiso = { navController.navigate(BottomNavItem.PermisoForm.passFecha()) }
            )
        }

        // HU02 y HU03
        composable(
            route = BottomNavItem.Marcacion.route,
            arguments = listOf(navArgument("tipo") { type = NavType.StringType })
        ) {
            MarcacionScreen(onVolverInicio = { navController.popBackStack() })
        }

        // HU04
        composable(BottomNavItem.Asistencia.route) {
            AsistenciaScreen(
                onTardanzas = { navController.navigate(RutasNav.TARDANZAS) },
                onAusencias = { navController.navigate(RutasNav.AUSENCIAS) }
            )
        }

        // HU05
        composable(RutasNav.TARDANZAS) {
            TardanzasScreen(
                onSolicitarJustificacion = { navController.navigate(BottomNavItem.PermisoForm.passFecha()) }
            )
        }

        // HU06
        composable(RutasNav.AUSENCIAS) {
            AusenciasScreen(
                onJustificar = { fecha -> navController.navigate(BottomNavItem.PermisoForm.passFecha(fecha)) }
            )
        }

        // HU07
        composable(RutasNav.HORARIO) {
            HorarioScreen()
        }

        // HU08
        composable(BottomNavItem.Permisos.route) {
            PermisosScreen(
                onNuevoPermiso = { navController.navigate(BottomNavItem.PermisoForm.passFecha()) }
            )
        }

        composable(
            route = BottomNavItem.PermisoForm.route,
            arguments = listOf(navArgument("fecha") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) {
            PermisoFormScreen(onSuccess = { navController.popBackStack() })
        }

        composable(BottomNavItem.Perfil.route) {
            PerfilScreen(onHorario = { navController.navigate(RutasNav.HORARIO) })
        }

        // ---------------- Administrador ----------------
        // HU09
        composable(BottomNavItem.Registros.route) {
            RegistrosScreen()
        }

        // HU10
        composable(BottomNavItem.Reportes.route) {
            ReportesScreen()
        }

        composable(BottomNavItem.AdminPermisos.route) {
            AdminPermisosScreen()
        }
    }
}
