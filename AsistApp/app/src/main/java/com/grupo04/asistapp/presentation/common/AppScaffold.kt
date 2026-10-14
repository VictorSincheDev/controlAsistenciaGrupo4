package com.grupo04.asistapp.presentation.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.grupo04.asistapp.presentation.event.UiEvent
import com.grupo04.asistapp.presentation.event.UiEventBus
import com.grupo04.asistapp.presentation.navigation.RutasNav
import com.grupo04.asistapp.ui.theme.Fondo
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AppScaffold(
    navController: NavHostController,
    eventBus: UiEventBus,
    esAdmin: Boolean,
    content: @Composable (PaddingValues) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarType by remember { mutableStateOf(SnackbarType.SUCCESS) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val enLogin = navBackStackEntry?.destination?.route == RutasNav.LOGIN

    LaunchedEffect(Unit) {
        eventBus.events.collectLatest { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    snackbarType = event.type
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        containerColor = Fondo,
        snackbarHost = { CustomSnackbarHost(hostState = snackbarHostState, currentType = snackbarType) },
        topBar = { if (!enLogin) AppTopBar(navController) },
        bottomBar = { if (!enLogin) AppBottomBar(navController, esAdmin) }
    ) { paddingValues ->
        content(paddingValues)
    }
}
