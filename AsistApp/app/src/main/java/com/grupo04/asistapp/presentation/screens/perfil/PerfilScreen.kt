package com.grupo04.asistapp.presentation.screens.perfil

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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.grupo04.asistapp.presentation.common.AvatarIniciales
import com.grupo04.asistapp.presentation.common.Formato
import com.grupo04.asistapp.presentation.common.InfoCard
import com.grupo04.asistapp.presentation.common.SeccionTitulo
import com.grupo04.asistapp.ui.theme.AzulClaro
import com.grupo04.asistapp.ui.theme.AzulCorporativo
import com.grupo04.asistapp.ui.theme.AzulMarino
import com.grupo04.asistapp.ui.theme.Borde
import com.grupo04.asistapp.ui.theme.Rojo
import com.grupo04.asistapp.ui.theme.RojoFondo
import com.grupo04.asistapp.ui.theme.SuperficieBaja
import com.grupo04.asistapp.ui.theme.TextoPrincipal
import com.grupo04.asistapp.ui.theme.TextoSecundario
import com.grupo04.asistapp.ui.theme.TextoTenue
import com.grupo04.asistapp.ui.theme.Verde
import com.grupo04.asistapp.ui.theme.VerdeFondo

@Composable
fun PerfilScreen(
    onHorario: () -> Unit,
    viewModel: PerfilViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    uiState.trabajador?.let { trabajador ->
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Cabecera
            Card(shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .background(Brush.verticalGradient(listOf(AzulMarino, Color(0xFF134074))))
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AvatarIniciales(nombre = trabajador.nombres, size = 72.dp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = trabajador.nombres, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(text = trabajador.cargo, color = AzulClaro, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(shape = RoundedCornerShape(20.dp), color = VerdeFondo) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Verde, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (trabajador.esAdmin) "Administrador • Ficha #${trabajador.id}" else "Colaborador Activo • Ficha #${trabajador.id}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Verde
                            )
                        }
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                InfoCard(Icons.Default.Badge, "DNI Colaborador", trabajador.dni, Modifier.weight(1f))
                InfoCard(Icons.Default.VerifiedUser, "Rol", if (trabajador.esAdmin) "Administrador" else "Trabajador", Modifier.weight(1f))
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = RojoFondo,
                modifier = Modifier.fillMaxWidth().clickable { viewModel.showLogoutDialog() }
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = Rojo)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "Cerrar Sesión", color = Rojo, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }

            // 2. Datos laborales
            SeccionTitulo(icono = Icons.Default.Badge, titulo = "Datos Laborales y Contrato")
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Borde),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                    DatoPerfil(Icons.Default.Business, "Área Operativa", trabajador.area)
                    DatoPerfil(Icons.Default.LocationOn, "Sede Asignada", trabajador.sede)
                    DatoPerfil(Icons.Default.Email, "Correo Corporativo", trabajador.correo)
                    DatoPerfil(Icons.Default.EventAvailable, "Fecha de Ingreso", Formato.fechaLarga(trabajador.fechaIngresoEmpresa))
                    val sabado = if (trabajador.trabajaSabado) " / Sáb ${trabajador.horaIngreso} - ${trabajador.horaSalidaSabado}" else ""
                    DatoPerfil(
                        Icons.Default.Alarm,
                        "Horario Asignado",
                        "Lun a Vie ${trabajador.horaIngreso} - ${trabajador.horaSalida}$sabado\nTolerancia de ingreso: ${trabajador.toleranciaMin} minutos"
                    )
                    DatoPerfil(Icons.Default.SupervisorAccount, "Supervisor Directo", trabajador.supervisor, ultimo = true)
                }
            }

            // 3. Opciones
            SeccionTitulo(icono = Icons.Default.ManageAccounts, titulo = "Opciones")
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Borde),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    if (!trabajador.esAdmin) {
                        Opcion(Icons.Default.Schedule, "Mi Horario Laboral", "Horas de ingreso, salida y descansos", onClick = onHorario)
                    }
                    Opcion(Icons.Default.Policy, "Reglamento Interno y Políticas de Asistencia", "Tolerancias, tardanzas y justificaciones", onClick = { viewModel.showReglamento() })
                    Opcion(Icons.Default.SupportAgent, "Soporte Técnico y RRHH", "Atención directa: anexo 104", onClick = {})
                }
            }

            Text(
                text = "AsistApp v1.0 • Intercompany & Señor de Huanca S.A.C.",
                fontSize = 11.sp,
                color = TextoTenue,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    if (uiState.showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissLogoutDialog() },
            title = { Text("Cerrar sesión") },
            text = { Text("¿Está seguro de que desea cerrar su sesión en AsistApp?") },
            confirmButton = {
                TextButton(onClick = { viewModel.cerrarSesion() }) {
                    Text("Cerrar sesión", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissLogoutDialog() }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (uiState.showReglamentoDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissReglamento() },
            title = { Text("Régimen de Asistencias") },
            text = {
                Text(
                    "• El ingreso y la salida se registran diariamente desde AsistApp.\n" +
                            "• Tolerancia de ingreso: 10 minutos. Pasado ese tiempo se registra tardanza.\n" +
                            "• Máximo 2 tardanzas por ciclo para conservar el bono de puntualidad.\n" +
                            "• Un día laborable sin marcación es una ausencia injustificada, salvo permiso aprobado.\n" +
                            "• Las justificaciones se presentan como solicitud de permiso dentro de las 48 horas."
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissReglamento() }) {
                    Text("Entendido")
                }
            }
        )
    }
}

@Composable
private fun DatoPerfil(icono: ImageVector, etiqueta: String, valor: String, ultimo: Boolean = false) {
    Column {
        Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp), verticalAlignment = Alignment.Top) {
            Surface(shape = RoundedCornerShape(8.dp), color = SuperficieBaja, modifier = Modifier.size(32.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icono, contentDescription = null, tint = AzulCorporativo, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = etiqueta, fontSize = 11.sp, color = TextoTenue)
                Text(text = valor.ifBlank { "-" }, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextoPrincipal)
            }
        }
        if (!ultimo) HorizontalDivider(color = Borde, modifier = Modifier.padding(horizontal = 14.dp))
    }
}

@Composable
private fun Opcion(icono: ImageVector, titulo: String, detalle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icono, contentDescription = null, tint = AzulMarino, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = titulo, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextoPrincipal)
            Text(text = detalle, fontSize = 11.sp, color = TextoSecundario)
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = TextoTenue)
    }
}
