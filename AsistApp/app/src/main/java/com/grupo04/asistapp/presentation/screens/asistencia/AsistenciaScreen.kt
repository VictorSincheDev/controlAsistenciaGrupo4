package com.grupo04.asistapp.presentation.screens.asistencia

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.grupo04.asistapp.presentation.common.AsistenciaItem
import com.grupo04.asistapp.presentation.common.EmptyState
import com.grupo04.asistapp.presentation.common.InfoCard
import com.grupo04.asistapp.presentation.common.LoadingOverlay
import com.grupo04.asistapp.presentation.common.SeccionTitulo
import com.grupo04.asistapp.ui.theme.Ambar
import com.grupo04.asistapp.ui.theme.AmbarFondo
import com.grupo04.asistapp.ui.theme.AzulClaro
import com.grupo04.asistapp.ui.theme.AzulMarino
import com.grupo04.asistapp.ui.theme.Rojo
import com.grupo04.asistapp.ui.theme.RojoFondo
import com.grupo04.asistapp.ui.theme.Verde
import com.grupo04.asistapp.ui.theme.VerdeFondo

@Composable
fun AsistenciaScreen(
    onTardanzas: () -> Unit,
    onAusencias: () -> Unit,
    viewModel: AsistenciaViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarHistorial()
    }

    val total = uiState.historial.size
    val aTiempo = uiState.historial.count { !it.esTardanza }
    val tardanzas = total - aTiempo
    val porcentaje = if (total == 0) 0 else (aTiempo * 100) / total
    val injustificadas = uiState.ausencias.count { !it.esJustificada }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                SeccionTitulo(icono = Icons.Default.Insights, titulo = "Métricas del Período", detalle = "Total: $total jornadas")
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        InfoCard(Icons.Default.CheckCircle, "A tiempo", "$aTiempo días ($porcentaje%)", Modifier.weight(1f), Verde, VerdeFondo)
                        InfoCard(Icons.Default.Schedule, "Tardanzas", "$tardanzas registradas", Modifier.weight(1f), Ambar, AmbarFondo)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        InfoCard(Icons.Default.Cancel, "Ausencias", "$injustificadas injustificadas", Modifier.weight(1f), Rojo, RojoFondo)
                        InfoCard(Icons.Default.Today, "Jornadas", "$total registradas", Modifier.weight(1f), AzulMarino, AzulClaro)
                    }
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onTardanzas, shape = RoundedCornerShape(12.dp), modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.History, contentDescription = null, tint = Ambar)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Mis Tardanzas", color = AzulMarino, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                    OutlinedButton(onClick = onAusencias, shape = RoundedCornerShape(12.dp), modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.EventBusy, contentDescription = null, tint = Rojo)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Mis Ausencias", color = AzulMarino, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }
            }
            item {
                SeccionTitulo(icono = Icons.Default.History, titulo = "Marcaciones Recientes")
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("TODOS" to "Todos", "A_TIEMPO" to "A tiempo", "TARDANZA" to "Tardanzas").forEach { (valor, texto) ->
                        FilterChip(
                            selected = uiState.filtro == valor,
                            onClick = { viewModel.onFiltroChange(valor) },
                            label = { Text(texto) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = AzulClaro)
                        )
                    }
                }
            }
            if (uiState.historialFiltrado.isEmpty() && !uiState.isLoading) {
                item { EmptyState(Icons.Default.Today, "Aún no tienes marcaciones registradas") }
            }
            items(uiState.historialFiltrado, key = { it.id }) { asistencia ->
                AsistenciaItem(asistencia = asistencia)
            }
        }

        LoadingOverlay(isLoading = uiState.isLoading)
    }
}
