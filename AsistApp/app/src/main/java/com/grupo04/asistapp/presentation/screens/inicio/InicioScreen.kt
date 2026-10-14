package com.grupo04.asistapp.presentation.screens.inicio

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VerifiedUser
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.grupo04.asistapp.domain.model.Trabajador
import com.grupo04.asistapp.presentation.common.EstadoBadge
import com.grupo04.asistapp.presentation.common.Formato
import com.grupo04.asistapp.presentation.common.LoadingOverlay
import com.grupo04.asistapp.presentation.common.SeccionTitulo
import com.grupo04.asistapp.presentation.common.Semaforo
import com.grupo04.asistapp.presentation.common.semaforoAsistencia
import com.grupo04.asistapp.presentation.common.textoAsistencia
import com.grupo04.asistapp.ui.theme.Ambar
import com.grupo04.asistapp.ui.theme.AmbarFondo
import com.grupo04.asistapp.ui.theme.AzulClaro
import com.grupo04.asistapp.ui.theme.AzulCorporativo
import com.grupo04.asistapp.ui.theme.AzulMarino
import com.grupo04.asistapp.ui.theme.AzulSuave
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
import java.time.LocalDate

// Hora de salida programada para una fecha (sábado media jornada, domingo descanso)
fun salidaProgramada(trabajador: Trabajador, fecha: String): String {
    val dia = runCatching { LocalDate.parse(fecha).dayOfWeek.value }.getOrDefault(1)
    return when (dia) {
        7 -> ""
        6 -> trabajador.horaSalidaSabado
        else -> trabajador.horaSalida
    }
}

@Composable
fun InicioScreen(
    onMarcar: (tipo: String) -> Unit,
    onHistorial: () -> Unit,
    onTardanzas: () -> Unit,
    onHorario: () -> Unit,
    onSolicitarPermiso: () -> Unit,
    viewModel: InicioViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarDatos()
    }

    val trabajador = uiState.trabajador
    val hoy = Formato.hoy()
    val asistencia = uiState.asistenciaHoy
    val mes = Formato.mesActual()
    val delMes = uiState.historial.filter { it.fecha.startsWith(mes) }
    val tardanzasMes = delMes.filter { it.esTardanza }
    val ausenciasMes = uiState.ausencias.filter { it.fecha.startsWith(mes) && !it.esJustificada }
    val puntualidad = if (delMes.isEmpty()) 100 else (delMes.count { !it.esTardanza } * 100) / delMes.size

    Box(modifier = Modifier.fillMaxSize()) {
        if (trabajador != null) {
            val salidaHoy = asistencia?.horarioSalida?.ifBlank { null } ?: salidaProgramada(trabajador, hoy)
            val esDescanso = salidaHoy.isBlank()

            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Saludo
                Card(shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                            .background(Brush.horizontalGradient(listOf(AzulMarino, Color(0xFF134074))))
                            .padding(18.dp)
                    ) {
                        Text(
                            text = "${Formato.saludo()}, ${Formato.nombreCorto(trabajador.nombres)}!",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = AzulSuave, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Área: ${trabajador.area} • ${trabajador.cargo}", color = AzulClaro, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(shape = RoundedCornerShape(10.dp), color = Color.White.copy(alpha = 0.12f)) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = Formato.fechaLarga(hoy), color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                }

                // 2. Mi asistencia de hoy
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Borde),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = AzulCorporativo, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "MI ASISTENCIA DE HOY",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextoSecundario,
                                letterSpacing = 1.sp,
                                modifier = Modifier.weight(1f)
                            )
                            when {
                                asistencia != null -> EstadoBadge(textoAsistencia(asistencia.estado), semaforoAsistencia(asistencia.estado))
                                esDescanso -> EstadoBadge("Descanso", Semaforo.AZUL)
                                else -> EstadoBadge("Sin marcar", Semaforo.AZUL)
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (asistencia != null) Formato.hora12(asistencia.horaIngreso) else "--:--",
                            fontSize = 34.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = AzulMarino
                        )
                        val limite = Formato.hora12(Formato.minutosAHora(Formato.aMinutos(trabajador.horaIngreso) + trabajador.toleranciaMin))
                        Text(
                            text = if (esDescanso) "Hoy es tu día de descanso semanal" else "Tolerancia de ingreso: hasta las $limite",
                            fontSize = 12.sp,
                            color = TextoTenue
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Borde)
                        Spacer(modifier = Modifier.height(12.dp))
                        FilaHoy(
                            icono = Icons.AutoMirrored.Filled.Login,
                            titulo = if (asistencia != null) "Entrada registrada" else "Entrada programada",
                            valor = Formato.hora12(asistencia?.horaIngreso ?: trabajador.horaIngreso),
                            ok = asistencia != null
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        FilaHoy(
                            icono = Icons.AutoMirrored.Filled.Logout,
                            titulo = if (asistencia?.tieneSalida == true) "Salida registrada" else "Salida prevista",
                            valor = if (asistencia?.tieneSalida == true) Formato.hora12(asistencia.horaSalida)
                            else if (esDescanso) "Descanso" else Formato.hora12(salidaHoy),
                            ok = asistencia?.tieneSalida == true
                        )
                    }
                }

                // 3. Botones de marcación (HU02 y HU03)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    BotonMarcacion(
                        icono = Icons.AutoMirrored.Filled.Login,
                        titulo = "Registrar Ingreso",
                        detalle = if (asistencia != null) "Marcado ${Formato.hora12(asistencia.horaIngreso)}" else "Disponible",
                        habilitado = asistencia == null && !esDescanso,
                        principal = true,
                        modifier = Modifier.weight(1f),
                        onClick = { onMarcar("ingreso") }
                    )
                    BotonMarcacion(
                        icono = Icons.AutoMirrored.Filled.Logout,
                        titulo = "Registrar Salida",
                        detalle = when {
                            asistencia == null -> "Primero marca ingreso"
                            asistencia.tieneSalida -> "Marcado ${Formato.hora12(asistencia.horaSalida)}"
                            else -> "Habilitado"
                        },
                        habilitado = asistencia != null && !asistencia.tieneSalida,
                        principal = false,
                        modifier = Modifier.weight(1f),
                        onClick = { onMarcar("salida") }
                    )
                }

                // 4. Accesos operativos
                SeccionTitulo(icono = Icons.Default.Insights, titulo = "Accesos Operativos")
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AccesoCard(Icons.Default.CalendarMonth, "Mi Asistencia", "Historial mensual", AzulClaro, AzulCorporativo, Modifier.weight(1f), onHistorial)
                    AccesoCard(Icons.Default.History, "Mis Tardanzas", "${tardanzasMes.size} en el mes", AmbarFondo, Ambar, Modifier.weight(1f), onTardanzas)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AccesoCard(Icons.Default.Timer, "Mi Horario", "${trabajador.horaIngreso} - ${trabajador.horaSalida}", SuperficieAlta, AzulMarino, Modifier.weight(1f), onHorario)
                    AccesoCard(Icons.Default.AddTask, "Solicitar Permiso", "Nueva solicitud RRHH", VerdeFondo, Verde, Modifier.weight(1f), onSolicitarPermiso)
                }

                // 5. Resumen del mes
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Borde),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SeccionTitulo(icono = Icons.Default.Insights, titulo = "Resumen del Mes", detalle = Formato.nombreMesActual())
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Resumen("${delMes.size}", "Días laborados", AzulMarino, SuperficieBaja, Modifier.weight(1f))
                            Resumen("${ausenciasMes.size}", "Ausencias inj.", Rojo, RojoFondo, Modifier.weight(1f))
                            Resumen("$puntualidad%", "Puntualidad", Verde, VerdeFondo, Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        LoadingOverlay(isLoading = uiState.isLoading)
    }
}

@Composable
private fun FilaHoy(icono: ImageVector, titulo: String, valor: String, ok: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(shape = RoundedCornerShape(8.dp), color = if (ok) VerdeFondo else SuperficieBaja, modifier = Modifier.size(32.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icono, contentDescription = null, tint = if (ok) Verde else TextoTenue, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = titulo, fontSize = 13.sp, color = TextoSecundario, modifier = Modifier.weight(1f))
        Text(text = valor, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextoPrincipal)
        Spacer(modifier = Modifier.width(6.dp))
        Icon(
            imageVector = if (ok) Icons.Default.CheckCircle else Icons.Default.HourglassEmpty,
            contentDescription = null,
            tint = if (ok) Verde else TextoTenue,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun BotonMarcacion(
    icono: ImageVector,
    titulo: String,
    detalle: String,
    habilitado: Boolean,
    principal: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val fondo = when {
        !habilitado -> SuperficieAlta
        principal -> AzulMarino
        else -> AzulCorporativo
    }
    val contenido = if (habilitado) Color.White else TextoTenue
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = fondo,
        modifier = modifier.height(110.dp).clickable(enabled = habilitado, onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(icono, contentDescription = null, tint = contenido, modifier = Modifier.size(28.dp))
            Column {
                Text(text = titulo, color = contenido, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = detalle, color = contenido.copy(alpha = 0.85f), fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun AccesoCard(
    icono: ImageVector,
    titulo: String,
    detalle: String,
    fondoIcono: Color,
    colorIcono: Color,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Borde),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Surface(shape = RoundedCornerShape(10.dp), color = fondoIcono, modifier = Modifier.size(38.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icono, contentDescription = null, tint = colorIcono, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = titulo, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextoPrincipal)
            Text(text = detalle, fontSize = 11.sp, color = TextoTenue)
        }
    }
}

@Composable
private fun Resumen(valor: String, etiqueta: String, color: Color, fondo: Color, modifier: Modifier) {
    Surface(shape = RoundedCornerShape(12.dp), color = fondo, modifier = modifier) {
        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = valor, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = color)
            Text(text = etiqueta, fontSize = 10.sp, color = TextoSecundario)
        }
    }
}
