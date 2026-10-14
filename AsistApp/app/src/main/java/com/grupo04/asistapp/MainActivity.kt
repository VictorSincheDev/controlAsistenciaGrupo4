package com.grupo04.asistapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.grupo04.asistapp.data.session.SesionManager
import com.grupo04.asistapp.presentation.common.AppScaffold
import com.grupo04.asistapp.presentation.event.UiEventBus
import com.grupo04.asistapp.presentation.navigation.AppNavigation
import com.grupo04.asistapp.presentation.navigation.RutasNav
import com.grupo04.asistapp.ui.theme.AsistAppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var eventBus: UiEventBus

    @Inject
    lateinit var sesionManager: SesionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AsistAppTheme {
                val navController = rememberNavController()
                val trabajador by sesionManager.trabajador.collectAsState()

                // Sin sesión activa (o al cerrar sesión) siempre se vuelve al Login
                LaunchedEffect(trabajador) {
                    if (trabajador == null && navController.currentDestination?.route != RutasNav.LOGIN) {
                        navController.navigate(RutasNav.LOGIN) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                }

                AppScaffold(navController, eventBus, esAdmin = trabajador?.esAdmin == true) { paddingValues ->
                    Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                        AppNavigation(navController)
                    }
                }
            }
        }
    }
}
