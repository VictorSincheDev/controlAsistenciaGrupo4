package com.grupo04.asistapp.presentation.screens.marcacion

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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.grupo04.asistapp.presentation.common.AvatarIniciales
import com.grupo04.asistapp.presentation.common.EstadoBadge
import com.grupo04.asistapp.presentation.common.Formato
import com.grupo04.asistapp.presentation.common.LoadingOverlay
import com.grupo04.asistapp.presentation.common.MetaBadge
import com.grupo04.asistapp.presentation.common.Semaforo
import com.grupo04.asistapp.presentation.common.semaforoAsistencia
import com.grupo04.asistapp.presentation.common.textoAsistencia
import com.grupo04.asistapp.presentation.screens.inicio.salidaProgramada
import com.grupo04.asistapp.ui.theme.AzulClaro
import com.grupo04.asistapp.ui.theme.AzulCorporativo
import com.grupo04.asistapp.ui.theme.AzulMarino
import com.grupo04.asistapp.ui.theme.Borde
import com.grupo04.asistapp.ui.theme.SuperficieAlta
import com.grupo04.asistapp.ui.theme.SuperficieBaja
import com.grupo04.asistapp.ui.theme.TextoPrincipal
import com.grupo04.asistapp.ui.theme.TextoSecundario
import com.grupo04.asistapp.ui.theme.TextoTenue
import com.grupo04.asistapp.ui.theme.Verde
import com.grupo04.asistapp.ui.theme.VerdeFondo
import com.grupo04.asistapp.ui.theme.VerdeTexto

@Composable
fun MarcacionScreen(
    onVolverInicio: () -> Unit,
    viewModel: MarcacionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val trabajador = uiState.trabajador
    val asistencia = uiState.asistenciaHoy
    val resultado = uiState.resultado

    Box(modifier = Modifier.fillMaxSize()) {
        if (trabajador != null) {
            val hoy = Formato.hoy()
            val minutosAhora = Formato.aMinutos(uiState.horaActual)
            val salidaHoy = asistencia?.horarioSalida?.ifBlank { null } ?: salidaProgramada(trabajador, hoy)

            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Colaborador
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Borde),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        AvatarIniciales(nombre = trabajador.nombres)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = Formato.nombreCorto(trabajador.nombres), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextoPrincipal)
                            Text(text = "${trabajador.cargo} • DNI: ${trabajador.dni}", fontSize = 11.sp, color = TextoTenue)
                        }
                        MetaBadge(text = trabajador.turno.ifBlank { "TURNO" }.uppercase())
                    }
                }

                // 2. Reloj
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = AzulMarino),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = AzulClaro, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = Formato.fechaLarga(hoy), color = AzulClaro, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = uiState.horaActual, color = Color.White, fontSize = 44.sp, fontWeight = FontWeight.ExtraBold)
                        Spacer(modifier = Modifier.height(6.dp))
                        if (uiState.esIngreso) {
                            Text(
                                text = "Turno oficial: ${Formato.hora12(trabajador.horaIngreso)}",
                                color = AzulClaro,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            val dentro = minutosAhora - Formato.aMinutos(trabajador.horaIngreso) <= trabajador.toleranciaMin
                            EstadoBadge(
                                texto = if (dentro) "DENTRO DE TOLERANCIA" else "FUERA DE TOLERANCIA",
                                semaforo = if (dentro) Semaforo.VERDE else Semaforo.AMBAR
                            )
                        } else {
                            Text(
                                text = "Salida programada: ${if (salidaHoy.isBlank()) "Descanso" else Formato.hora12(salidaHoy)}",
                                color = AzulClaro,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                // 3. Detalle según el tipo de marcación
                if (uiState.esIngreso) {
                    DatoCard(
                        icono = Icons.Default.LocationOn,
                        titulo = "Sede asignada",
                        valor = trabajador.sede,
                        detalle = "La marcación queda registrada con la hora oficial del servidor"
                    )
                } else if (asistencia != null) {
                    val efectivo = if (asistencia.tieneSalida) asistencia.minutosTrabajados
                    else (minutosAhora - Formato.aMinutos(asistencia.horaIngreso)).coerceAtLeast(0)
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Borde),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(text = "Resumen de Jornada", fontWeight = FontWeight.Bold, color = AzulMarino, fontSize = 15.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Ingreso: ${Formato.hora12(asistencia.horaIngreso)}",
                                    fontSize = 13.sp,
                                    color = TextoSecundario,
                                    modifier = Modifier.weight(1f)
                                )
                                EstadoBadge(textoAsistencia(asistencia.estado), semaforoAsistencia(asistencia.estado))
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                MiniDato("Efectivo", Formato.duracion(efectivo), Modifier.weight(1f))
                                MiniDato("Sede", trabajador.sede, Modifier.weight(1f))
                            }
                        }
                    }
                } else {
                    DatoCard(
                        icono = Icons.Default.Schedule,
                        titulo = "Aún no registras tu ingreso",
                        valor = "Primero registra tu ingreso",
                        detalle = "La salida se habilita después de marcar la entrada del día"
                    )
                }

                // 4. Resultado (confirmación) o botón para marcar
                if (resultado != null) {
                    ConfirmacionCard(
                        esIngreso = uiState.esIngreso,
                        nombre = Formato.nombreCorto(trabajador.nombres).split(" ").first(),
                        horaSellada = Formato.hora12(if (uiState.esIngreso) resultado.horaIngreso else resultado.horaSalida),
                        estado = resultado.estado,
                        diferencial = Formato.aMinutos(resultado.horaIngreso) - Formato.aMinutos(resultado.horarioIngreso),
                        minutosTrabajados = resultado.minutosTrabajados,
                        codigo = "#${if (uiState.esIngreso) "ING" else "SAL"}-${resultado.fecha.replace("-", "")}-${trabajador.id}"
                    )
                    OutlinedButton(
                        onClick = onVolverInicio,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Icon(Icons.Default.Home, contentDescription = null, tint = AzulMarino)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Volver a Pantalla Principal", color = AzulMarino, fontWeight = FontWeight.Bold)
                    }
                } else {
                    val yaMarcado = if (uiState.esIngreso) asistencia != null else asistencia?.tieneSalida == true
                    val habilitado = !yaMarcado && (uiState.esIngreso || asistencia != null)
                    Button(
                        onClick = { viewModel.showConfirmDialog() },
                        enabled = habilitado,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (uiState.esIngreso) AzulMarino else AzulCorporativo,
                            disabledContainerColor = SuperficieAlta
                        ),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.fillMaxWidth().height(64.dp)
                    ) {
                        Icon(
                            imageVector = if (uiState.esIngreso) Icons.Default.Fingerprint else Icons.AutoMirrored.Filled.Logout,
                            contentDescription = null,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (uiState.esIngreso) "REGISTRAR INGRESO" else "REGISTRAR SALIDA",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            letterSpacing = 1.sp
                        )
                    }
                    if (yaMarcado) {
                        Text(
                            text = if (uiState.esIngreso) "Ya registraste tu ingreso hoy a las ${Formato.hora12(asistencia?.horaIngreso ?: "")}"
                            else "Ya registraste tu salida hoy a las ${Formato.hora12(asistencia?.horaSalida ?: "")}",
                            fontSize = 12.sp,
                            color = TextoTenue,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        LoadingOverlay(isLoading = uiState.isLoading)
    }

    if (uiState.showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissConfirmDialog() },
            title = { Text(if (uiState.esIngreso) "Confirmar Ingreso" else "Confirmar Salida") },
            text = {
                Text(
                    if (uiState.esIngreso) "¿Deseas registrar tu hora de ingreso ahora (${uiState.horaActual})?"
                    else "¿Deseas registrar tu hora de salida y cerrar tu jornada laboral?"
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.confirmarMarcacion() }) {
                    Text("Registrar", color = AzulMarino, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissConfirmDialog() }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun DatoCard(icono: ImageVector, titulo: String, valor: String, detalle: String) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Borde),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(10.dp), color = VerdeFondo, modifier = Modifier.size(40.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icono, contentDescription = null, tint = Verde, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = titulo, fontSize = 11.sp, color = TextoTenue)
                Text(text = valor, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextoPrincipal)
                Text(text = detalle, fontSize = 11.sp, color = TextoSecundario)
            }
        }
    }
}

@Composable
private fun MiniDato(etiqueta: String, valor: String, modifier: Modifier) {
    Surface(shape = RoundedCornerShape(10.dp), color = SuperficieBaja, modifier = modifier) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = etiqueta, fontSize = 10.sp, color = TextoTenue)
            Text(text = valor, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextoPrincipal)
        }
    }
}

@Composable
private fun ConfirmacionCard(
    esIngreso: Boolean,
    nombre: String,
    horaSellada: String,
    estado: String,
    diferencial: Int,
    minutosTrabajados: Int,
    codigo: String
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = VerdeFondo),
        border = BorderStroke(1.dp, Verde.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Verde, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (esIngreso) "¡Ingreso Registrado Correctamente!" else "¡Salida Registrada Correctamente!",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = VerdeTexto,
                textAlign = TextAlign.Center
            )
            if (!esIngreso) {
                Text(
                    text = "¡Excelente trabajo hoy, $nombre! Has completado tu jornada laboral.",
                    fontSize = 12.sp,
                    color = VerdeTexto,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            if (esIngreso) EstadoBadge(textoAsistencia(estado).uppercase(), semaforoAsistencia(estado))
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MiniDato("Hora sellada", horaSellada, Modifier.weight(1f))
                if (esIngreso) {
                    val texto = when {
                        diferencial < 0 -> "${-diferencial} min antes"
                        diferencial == 0 -> "Puntual"
                        else -> "+$diferencial min"
                    }
                    MiniDato("Diferencial", texto, Modifier.weight(1f))
                } else {
                    MiniDato("Trabajado", Formato.duracion(minutosTrabajados), Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Timer, contentDescription = null, tint = TextoTenue, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Código de verificación: $codigo", fontSize = 10.sp, color = TextoTenue)
            }
        }
    }
}
