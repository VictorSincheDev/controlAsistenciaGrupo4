package com.grupo04.asistapp.presentation.screens.admin.reportes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.grupo04.asistapp.domain.model.ReporteTrabajador
import com.grupo04.asistapp.presentation.common.AvatarIniciales
import com.grupo04.asistapp.presentation.common.EmptyState
import com.grupo04.asistapp.presentation.common.Formato
import com.grupo04.asistapp.presentation.common.InfoCard
import com.grupo04.asistapp.presentation.common.LoadingOverlay
import com.grupo04.asistapp.presentation.common.SeccionTitulo
import com.grupo04.asistapp.ui.theme.Ambar
import com.grupo04.asistapp.ui.theme.AmbarFondo
import com.grupo04.asistapp.ui.theme.AzulClaro
import com.grupo04.asistapp.ui.theme.AzulMarino
import com.grupo04.asistapp.ui.theme.Borde
import com.grupo04.asistapp.ui.theme.Rojo
import com.grupo04.asistapp.ui.theme.RojoFondo
import com.grupo04.asistapp.ui.theme.SuperficieAlta
import com.grupo04.asistapp.ui.theme.SuperficieBaja
import com.grupo04.asistapp.ui.theme.TextoPrincipal
import com.grupo04.asistapp.ui.theme.TextoSecundario
import com.grupo04.asistapp.ui.theme.TextoTenue
import com.grupo04.asistapp.ui.theme.Verde
import com.grupo04.asistapp.ui.theme.VerdeFondo

@Composable
fun ReportesScreen(viewModel: ReportesViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    val asistencias = uiState.items.sumOf { it.diasAsistidos }
    val tardanzas = uiState.items.sumOf { it.tardanzas }
    val ausencias = uiState.items.sumOf { it.ausenciasInjustificadas }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                SeccionTitulo(icono = Icons.Default.DateRange, titulo = "Periodo del Reporte")
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = uiState.desde,
                        onValueChange = viewModel::onDesdeChange,
                        label = { Text("Desde") },
                        placeholder = { Text("AAAA-MM-DD") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = uiState.hasta,
                        onValueChange = viewModel::onHastaChange,
                        label = { Text("Hasta") },
                        placeholder = { Text("AAAA-MM-DD") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            item {
                Button(
                    onClick = { viewModel.generarReporte() },
                    colors = ButtonDefaults.buttonColors(containerColor = AzulMarino),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Icon(Icons.Default.Assessment, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("GENERAR REPORTE", fontWeight = FontWeight.Bold)
                }
            }

            if (uiState.generado) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            InfoCard(Icons.Default.Groups, "Trabajadores", "${uiState.items.size}", Modifier.weight(1f), AzulMarino, AzulClaro)
                            InfoCard(Icons.Default.CheckCircle, "Asistencias", "$asistencias", Modifier.weight(1f), Verde, VerdeFondo)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            InfoCard(Icons.Default.Schedule, "Tardanzas", "$tardanzas", Modifier.weight(1f), Ambar, AmbarFondo)
                            InfoCard(Icons.Default.Cancel, "Ausencias inj.", "$ausencias", Modifier.weight(1f), Rojo, RojoFondo)
                        }
                    }
                }
                item {
                    SeccionTitulo(
                        icono = Icons.Default.Groups,
                        titulo = "Detalle por Trabajador",
                        detalle = "${Formato.fechaCorta(uiState.desde)} - ${Formato.fechaCorta(uiState.hasta)}"
                    )
                }
                if (uiState.items.isEmpty() && !uiState.isLoading) {
                    item { EmptyState(Icons.Default.Assessment, "No hay datos para el periodo seleccionado") }
                }
                items(uiState.items, key = { it.trabajadorId }) { reporte ->
                    ReporteItem(reporte)
                }
            }
        }

        LoadingOverlay(isLoading = uiState.isLoading)
    }
}

@Composable
private fun ReporteItem(reporte: ReporteTrabajador) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Borde),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AvatarIniciales(nombre = reporte.nombres, size = 40.dp)
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = reporte.nombres, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextoPrincipal)
                    Text(text = "${reporte.area} • DNI ${reporte.dni}", fontSize = 11.sp, color = TextoTenue)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Puntualidad", fontSize = 12.sp, color = TextoSecundario, modifier = Modifier.weight(1f))
                Text(
                    text = "${reporte.puntualidad}%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (reporte.puntualidad >= 90) Verde else Ambar
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { reporte.puntualidad / 100f },
                color = if (reporte.puntualidad >= 90) Verde else Ambar,
                trackColor = SuperficieAlta,
                modifier = Modifier.fillMaxWidth().height(6.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Dato("${reporte.diasAsistidos}", "Asistió", AzulMarino, SuperficieBaja, Modifier.weight(1f))
                Dato("${reporte.aTiempo}", "A tiempo", Verde, VerdeFondo, Modifier.weight(1f))
                Dato("${reporte.tardanzas}", "Tard. (${reporte.minutosTardanza}m)", Ambar, AmbarFondo, Modifier.weight(1f))
                Dato("${reporte.ausenciasInjustificadas}", "Aus. inj.", Rojo, RojoFondo, Modifier.weight(1f))
            }
            if (reporte.ausenciasJustificadas > 0) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Verde, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${reporte.ausenciasJustificadas} ausencia(s) justificada(s) con permiso", fontSize = 11.sp, color = TextoSecundario)
                }
            }
        }
    }
}

@Composable
private fun Dato(valor: String, etiqueta: String, color: Color, fondo: Color, modifier: Modifier) {
    Surface(shape = RoundedCornerShape(10.dp), color = fondo, modifier = modifier) {
        Column(modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = valor, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = color)
            Text(text = etiqueta, fontSize = 9.sp, color = TextoSecundario, maxLines = 1)
        }
    }
}
