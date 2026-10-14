package com.grupo04.asistapp.presentation.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grupo04.asistapp.domain.model.Asistencia
import com.grupo04.asistapp.ui.theme.Ambar
import com.grupo04.asistapp.ui.theme.AzulClaro
import com.grupo04.asistapp.ui.theme.AzulCorporativo
import com.grupo04.asistapp.ui.theme.AzulMarino
import com.grupo04.asistapp.ui.theme.Borde
import com.grupo04.asistapp.ui.theme.SuperficieBaja
import com.grupo04.asistapp.ui.theme.TextoPrincipal
import com.grupo04.asistapp.ui.theme.TextoSecundario
import com.grupo04.asistapp.ui.theme.TextoTenue

// Tarjeta de una marcación (historial del trabajador y registros del administrador)
@Composable
fun AsistenciaItem(asistencia: Asistencia, mostrarTrabajador: Boolean = false) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Borde)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Bloque de fecha: 19 / SEP
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AzulClaro.copy(alpha = 0.6f),
                    modifier = Modifier.size(50.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = Formato.numeroDia(asistencia.fecha),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = AzulMarino
                        )
                        Text(
                            text = Formato.mesCorto(asistencia.fecha),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AzulCorporativo
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (mostrarTrabajador) asistencia.trabajadorNombre else Formato.fechaLarga(asistencia.fecha),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoPrincipal
                    )
                    Text(
                        text = if (mostrarTrabajador) "${asistencia.area} • ${Formato.fechaCorta(asistencia.fecha)}"
                        else "Turno programado: ${asistencia.horarioIngreso} - ${asistencia.horarioSalida}",
                        fontSize = 11.sp,
                        color = TextoTenue
                    )
                }
                EstadoBadge(textoAsistencia(asistencia.estado), semaforoAsistencia(asistencia.estado))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                HoraMarcada(
                    icono = Icons.AutoMirrored.Filled.Login,
                    etiqueta = "Entrada",
                    hora = Formato.hora12(asistencia.horaIngreso),
                    modifier = Modifier.weight(1f)
                )
                HoraMarcada(
                    icono = Icons.AutoMirrored.Filled.Logout,
                    etiqueta = "Salida",
                    hora = if (asistencia.tieneSalida) Formato.hora12(asistencia.horaSalida) else "Pendiente",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Borde, thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (asistencia.esTardanza) Icons.Default.Warning else Icons.Default.Schedule,
                    contentDescription = null,
                    tint = if (asistencia.esTardanza) Ambar else TextoSecundario,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                val laboradas = if (asistencia.tieneSalida) "${Formato.duracion(asistencia.minutosTrabajados)} laboradas" else "Jornada en curso"
                val extra = if (asistencia.esTardanza) " • +${asistencia.minutosTardanza} min de tardanza" else ""
                Text(text = laboradas + extra, fontSize = 11.sp, color = TextoSecundario)
            }
        }
    }
}

@Composable
private fun HoraMarcada(icono: ImageVector, etiqueta: String, hora: String, modifier: Modifier = Modifier) {
    Surface(shape = RoundedCornerShape(10.dp), color = SuperficieBaja, modifier = modifier) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icono, contentDescription = null, tint = AzulCorporativo, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(text = etiqueta, fontSize = 10.sp, color = TextoTenue)
                Text(text = hora, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextoPrincipal)
            }
        }
    }
}
