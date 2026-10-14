package com.grupo04.asistapp.presentation.screens.horario

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.ViewWeek
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.grupo04.asistapp.domain.model.Trabajador
import com.grupo04.asistapp.presentation.common.AvatarIniciales
import com.grupo04.asistapp.presentation.common.EstadoBadge
import com.grupo04.asistapp.presentation.common.Formato
import com.grupo04.asistapp.presentation.common.InfoCard
import com.grupo04.asistapp.presentation.common.LoadingOverlay
import com.grupo04.asistapp.presentation.common.SeccionTitulo
import com.grupo04.asistapp.presentation.common.Semaforo
import com.grupo04.asistapp.ui.theme.AzulClaro
import com.grupo04.asistapp.ui.theme.AzulCorporativo
import com.grupo04.asistapp.ui.theme.AzulMarino
import com.grupo04.asistapp.ui.theme.Borde
import com.grupo04.asistapp.ui.theme.SuperficieBaja
import com.grupo04.asistapp.ui.theme.TextoPrincipal
import com.grupo04.asistapp.ui.theme.TextoSecundario
import com.grupo04.asistapp.ui.theme.TextoTenue

private fun horasEntre(inicio: String, fin: String): Int =
    if (fin.isBlank()) 0 else (Formato.aMinutos(fin) - Formato.aMinutos(inicio)) / 60

@Composable
fun HorarioScreen(viewModel: HorarioViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarHorario()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        uiState.trabajador?.let { trabajador ->
            val refrigerio = if (trabajador.refrigerio.isNotBlank()) 1 else 0
            val horasDia = horasEntre(trabajador.horaIngreso, trabajador.horaSalida) - refrigerio
            val horasSabado = horasEntre(trabajador.horaIngreso, trabajador.horaSalidaSabado)
            val horasSemana = horasDia * 5 + horasSabado

            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Ficha del colaborador
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Borde),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AvatarIniciales(nombre = trabajador.nombres)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = Formato.nombreCorto(trabajador.nombres), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextoPrincipal)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Badge, contentDescription = null, tint = TextoTenue, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "DNI: ${trabajador.dni}", fontSize = 12.sp, color = TextoTenue)
                                }
                            }
                            EstadoBadge("Activo", Semaforo.VERDE)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Borde)
                        Spacer(modifier = Modifier.height(12.dp))
                        FilaDato(Icons.Default.Work, "Régimen", trabajador.regimen.ifBlank { "Régimen General" })
                        FilaDato(Icons.Default.Timer, "Turno", trabajador.turno.ifBlank { "-" })
                        FilaDato(Icons.Default.LocationOn, "Sede", trabajador.sede)
                    }
                }

                // 2. Jornada semanal
                InfoCard(
                    icon = Icons.Default.Timer,
                    label = "Jornada Semanal",
                    value = "${horasSemana}h en total",
                    modifier = Modifier.fillMaxWidth()
                )

                BloqueHorario(
                    icono = Icons.Default.ViewWeek,
                    titulo = "Lunes a Viernes",
                    detalle = "${Formato.hora12(trabajador.horaIngreso)} a ${Formato.hora12(trabajador.horaSalida)}",
                    nota = if (trabajador.refrigerio.isNotBlank()) "Refrigerio: ${trabajador.refrigerio}" else ""
                )
                BloqueHorario(
                    icono = Icons.Default.Today,
                    titulo = "Sábado",
                    detalle = if (trabajador.trabajaSabado) "${Formato.hora12(trabajador.horaIngreso)} a ${Formato.hora12(trabajador.horaSalidaSabado)}" else "Día de descanso",
                    nota = if (trabajador.trabajaSabado) "Media jornada" else ""
                )
                BloqueHorario(
                    icono = Icons.Default.Bedtime,
                    titulo = "Domingo",
                    detalle = "Día de descanso semanal obligatorio",
                    nota = ""
                )

                // 3. Cronograma de la semana en curso
                SeccionTitulo(icono = Icons.Default.CalendarMonth, titulo = "Cronograma Diario", detalle = "Semana en curso")
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Borde),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(vertical = 6.dp)) {
                        Formato.semanaActual().forEachIndexed { i, fecha ->
                            DiaCronograma(trabajador, fecha, i)
                        }
                    }
                }

                // 4. Aviso de jefatura
                Surface(shape = RoundedCornerShape(14.dp), color = AzulClaro.copy(alpha = 0.5f), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = AzulCorporativo, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Tolerancia de ingreso: ${trabajador.toleranciaMin} minutos",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = AzulMarino
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ManageAccounts, contentDescription = null, tint = AzulCorporativo, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Supervisor a cargo: ${trabajador.supervisor}", fontSize = 12.sp, color = TextoSecundario)
                        }
                    }
                }
            }
        }

        LoadingOverlay(isLoading = uiState.isLoading)
    }
}

@Composable
private fun FilaDato(icono: ImageVector, etiqueta: String, valor: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icono, contentDescription = null, tint = AzulCorporativo, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "$etiqueta:", fontSize = 12.sp, color = TextoTenue)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = valor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextoPrincipal)
    }
}

@Composable
private fun BloqueHorario(icono: ImageVector, titulo: String, detalle: String, nota: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Borde),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(10.dp), color = SuperficieBaja, modifier = Modifier.size(40.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icono, contentDescription = null, tint = AzulMarino, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = titulo, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextoPrincipal)
                Text(text = detalle, fontSize = 13.sp, color = TextoSecundario)
                if (nota.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LunchDining, contentDescription = null, tint = TextoTenue, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = nota, fontSize = 11.sp, color = TextoTenue)
                    }
                }
            }
        }
    }
}

@Composable
private fun DiaCronograma(trabajador: Trabajador, fecha: String, indice: Int) {
    val esHoy = fecha == Formato.hoy()
    val salida = when (indice) {
        6 -> ""
        5 -> trabajador.horaSalidaSabado
        else -> trabajador.horaSalida
    }
    Surface(color = if (esHoy) AzulClaro.copy(alpha = 0.5f) else Color.Transparent, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.width(44.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = Formato.diaSemana(fecha).take(3).uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextoTenue)
                Text(text = Formato.numeroDia(fecha), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AzulMarino)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = Formato.diaSemana(fecha) + if (esHoy) " • Hoy" else "",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextoPrincipal
                )
                Text(
                    text = if (salida.isBlank()) "Descanso" else "${trabajador.horaIngreso} - $salida",
                    fontSize = 12.sp,
                    color = TextoSecundario
                )
            }
            if (salida.isBlank()) EstadoBadge("Descanso", Semaforo.AZUL)
            else {
                val refrigerio = if (indice < 5 && trabajador.refrigerio.isNotBlank()) 1 else 0
                Text(text = "${horasEntre(trabajador.horaIngreso, salida) - refrigerio} hrs", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AzulCorporativo)
            }
        }
    }
}
