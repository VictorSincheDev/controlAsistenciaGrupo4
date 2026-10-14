package com.grupo04.asistapp.presentation.common

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.grupo04.asistapp.presentation.navigation.BottomNavItem
import com.grupo04.asistapp.presentation.navigation.RutasNav
import com.grupo04.asistapp.ui.theme.AzulClaro
import com.grupo04.asistapp.ui.theme.AzulMarino
import com.grupo04.asistapp.ui.theme.TextoTenue

@Composable
fun AppBottomBar(navController: NavHostController, esAdmin: Boolean) {
    val opciones = if (esAdmin) {
        listOf(BottomNavItem.Registros, BottomNavItem.Reportes, BottomNavItem.AdminPermisos, BottomNavItem.Perfil)
    } else {
        listOf(BottomNavItem.Inicio, BottomNavItem.Asistencia, BottomNavItem.Permisos, BottomNavItem.Perfil)
    }
    val rutaInicio = if (esAdmin) RutasNav.REGISTROS else RutasNav.INICIO

    NavigationBar(containerColor = Color.White) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        opciones.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AzulMarino,
                    selectedTextColor = AzulMarino,
                    indicatorColor = AzulClaro,
                    unselectedIconColor = TextoTenue,
                    unselectedTextColor = TextoTenue
                ),
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(rutaInicio) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
