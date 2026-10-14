package com.grupo04.asistapp.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Business
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.grupo04.asistapp.presentation.navigation.RutasNav
import com.grupo04.asistapp.ui.theme.AzulMarino
import com.grupo04.asistapp.ui.theme.Borde
import com.grupo04.asistapp.ui.theme.TextoTenue

@Composable
fun AppTopBar(navController: NavHostController) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val ruta = navBackStackEntry.value?.destination?.route
    val tipo = navBackStackEntry.value?.arguments?.getString("tipo")
    val titulo = RutasNav.getTitulo(ruta, tipo)
    val mostrarBotonBack = RutasNav.mostrarBotonBack(ruta)

    Column(modifier = Modifier.background(Color.White).statusBarsPadding()) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, bottom = 8.dp, top = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (mostrarBotonBack) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Regresar",
                        tint = AzulMarino,
                        modifier = Modifier.size(26.dp)
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Business,
                    contentDescription = "Logo",
                    tint = AzulMarino,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Spacer(modifier = Modifier.width(4.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    color = AzulMarino,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Intercompany & Señor de Huanca S.A.C.",
                    color = TextoTenue,
                    fontSize = 11.sp
                )
            }

            // Botón de perfil
            if (ruta != RutasNav.PERFIL) {
                IconButton(onClick = { navController.navigate(RutasNav.PERFIL) { launchSingleTop = true } }) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Perfil",
                        tint = AzulMarino
                    )
                }
            }
        }

        HorizontalDivider(thickness = 1.dp, color = Borde)
    }
}
