package com.grupo04.asistapp.presentation.screens.tardanzas

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.grupo04.asistapp.domain.model.Asistencia
import com.grupo04.asistapp.presentation.common.EmptyState
import com.grupo04.asistapp.presentation.common.EstadoBadge
import com.grupo04.asistapp.presentation.common.Formato
import com.grupo04.asistapp.presentation.common.LoadingOverlay
import com.grupo04.asistapp.presentation.common.SeccionTitulo
import com.grupo04.asistapp.presentation.common.Semaforo
import com.grupo04.asistapp.ui.theme.Ambar
import com.grupo04.asistapp.ui.theme.AmbarFondo
import com.grupo04.asistapp.ui.theme.AmbarTexto
import com.grupo04.asistapp.ui.theme.AzulClaro
import com.grupo04.asistapp.ui.theme.AzulCorporativo
import com.grupo04.asistapp.ui.theme.AzulMarino
import com.grupo04.asistapp.ui.theme.Borde
import com.grupo04.asistapp.ui.theme.SuperficieAlta
import com.grupo04.asistapp.ui.theme.TextoPrincipal
import com.grupo04.asistapp.ui.theme.TextoSecundario
import com.grupo04.asistapp.ui.theme.TextoTenue
import com.grupo04.asistapp.ui.theme.Verde
import com.grupo04.asistapp.ui.theme.VerdeFondo
import com.grupo04.asistapp.ui.theme.VerdeTexto

private const val LIMITE_MINUTOS_MES = 60
private const val MAX_TARDANZAS_BONO = 2

@Composable
fun TardanzasScreen(
    onSolicitarJustificacion: () -> Unit,
    viewModel: TardanzasViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarTardanzas()
    }

    val trabajador = uiState.trabajador
    val delMes = uiState.tardanzas.filter { it.fecha.startsWith(Formato.mesActual()) }
    val minutosMes = delMes.sumOf { it.minutosTardanza }
    val bonoEnRiesgo = delMes.size >= MAX_TARDANZAS_BONO

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Resumen del mes
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Borde),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = AzulCorporativo, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = Formato.nombreMesActual(), fontWeight = FontWeight.Bold, color = AzulMarino, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Surface(shape = RoundedCornerShape(12.dp), color = AmbarFondo, modifier = Modifier.weight(1f)) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(text = "${delMes.size}", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Ambar)
                                    Text(text = "Tardanzas en el mes", fontSize = 11.sp, color = AmbarTexto)
                                }
                            }
                            Surface(shape = RoundedCornerShape(12.dp), color = AzulClaro.copy(alpha = 0.6f), modifier = Modifier.weight(1f)) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(text = "$minutosMes min", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = AzulMarino)
                                    Text(text = "Acumulados", fontSize = 11.sp, color = AzulCorporativo)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Tolerancia mensual acumulada: $minutosMes min / $LIMITE_MINUTOS_MES min máx.",
                            fontSize = 12.sp,
                            color = TextoSecundario
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { (minutosMes.toFloat() / LIMITE_MINUTOS_MES).coerceIn(0f, 1f) },
                            color = if (minutosMes >= LIMITE_MINUTOS_MES) Color(0xFFBA1A1A) else Ambar,
                            trackColor = SuperficieAlta,
                            modifier = Modifier.fillMaxWidth().height(8.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (bonoEnRiesgo) AmbarFondo else VerdeFondo,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (bonoEnRiesgo) Icons.Default.Warning else Icons.Default.ThumbUp,
                                    contentDescription = null,
                                    tint = if (bonoEnRiesgo) Ambar else Verde,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = if (bonoEnRiesgo) "Bono de puntualidad: En riesgo" else "Bono de puntualidad: Activo",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (bonoEnRiesgo) AmbarTexto else VerdeTexto
                                    )
                                    Text(text = "Máximo $MAX_TARDANZAS_BONO tardanzas permitidas por ciclo", fontSize = 11.sp, color = TextoSecundario)
                                }
                            }
                        }
                    }
                }
            }

            // Política de ingreso
            if (trabajador != null) {
                item {
                    val limite = Formato.hora12(Formato.minutosAHora(Formato.aMinutos(trabajador.horaIngreso) + trabajador.toleranciaMin))
                    Surface(shape = RoundedCornerShape(14.dp), color = AzulClaro.copy(alpha = 0.5f), modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = AzulCorporativo, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(text = "Política de Ingreso", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = AzulMarino)
                                Text(
                                    text = "Tolerancia de ingreso: hasta las $limite. Después se computa tardanza por cada minuto " +
                                            "respecto al horario de contrato (${Formato.hora12(trabajador.horaIngreso)}).",
                                    fontSize = 12.sp,
                                    color = TextoSecundario
                                )
                            }
                        }
                    }
                }
            }

            item {
                SeccionTitulo(icono = Icons.Default.Schedule, titulo = "Registro Detallado", detalle = "${uiState.tardanzas.size} registros")
            }

            if (uiState.tardanzas.isEmpty() && !uiState.isLoading) {
                item { EmptyState(Icons.Default.ThumbUp, "¡Excelente! No tienes tardanzas registradas") }
            }

            items(uiState.tardanzas, key = { it.id }) { tardanza ->
                TardanzaItem(tardanza)
            }

            item {
                Button(
                    onClick = onSolicitarJustificacion,
                    colors = ButtonDefaults.buttonColors(containerColor = AzulMarino),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Assignment, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Solicitar Justificación a RRHH", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Plazo máximo para descargo regular: 48 horas tras la incidencia",
                    fontSize = 11.sp,
                    color = TextoTenue,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        LoadingOverlay(isLoading = uiState.isLoading)
    }
}

@Composable
private fun TardanzaItem(tardanza: Asistencia) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Borde),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${Formato.fechaCorta(tardanza.fecha)} (${Formato.diaSemana(tardanza.fecha)})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextoPrincipal,
                    modifier = Modifier.weight(1f)
                )
                EstadoBadge("+${tardanza.minutosTardanza} min", Semaforo.AMBAR)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Schedule, contentDescription = null, tint = TextoTenue, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Marcación: ${Formato.hora12(tardanza.horaIngreso)} (Horario base: ${Formato.hora12(tardanza.horarioIngreso)})",
                    fontSize = 12.sp,
                    color = TextoSecundario
                )
            }
        }
    }
}
