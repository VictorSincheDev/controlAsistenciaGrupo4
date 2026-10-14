package com.grupo04.asistapp.presentation.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.grupo04.asistapp.domain.model.Permiso
import com.grupo04.asistapp.ui.theme.Borde
import com.grupo04.asistapp.ui.theme.SuperficieBaja
import com.grupo04.asistapp.ui.theme.TextoPrincipal
import com.grupo04.asistapp.ui.theme.TextoSecundario
import com.grupo04.asistapp.ui.theme.TextoTenue

private fun iconoTipo(tipo: String): ImageVector = when {
    tipo.contains("Médico", ignoreCase = true) -> Icons.Default.MedicalServices
    tipo.contains("Comisión", ignoreCase = true) -> Icons.Default.Work
    tipo.contains("Duelo", ignoreCase = true) -> Icons.Default.Gavel
    else -> Icons.Default.Person
}

// Tarjeta de una solicitud de permiso. "acciones" permite al administrador aprobar o rechazar.
@Composable
fun PermisoItem(
    permiso: Permiso,
    mostrarTrabajador: Boolean = false,
    acciones: (@Composable () -> Unit)? = null
) {
    val semaforo = semaforoPermiso(permiso.estado)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Borde)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(10.dp), color = semaforo.fondo, modifier = Modifier.size(40.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = iconoTipo(permiso.tipo),
                            contentDescription = null,
                            tint = semaforo.punto,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = permiso.tipo, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextoPrincipal)
                    if (mostrarTrabajador) {
                        Text(text = "${permiso.trabajadorNombre} • ${permiso.area}", fontSize = 11.sp, color = TextoSecundario)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = TextoTenue,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        val rango = if (permiso.fechaInicio == permiso.fechaFin) Formato.fechaCorta(permiso.fechaInicio)
                        else "${Formato.fechaCorta(permiso.fechaInicio)} al ${Formato.fechaCorta(permiso.fechaFin)}"
                        Text(text = rango, fontSize = 11.sp, color = TextoTenue)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                EstadoBadge(textoPermiso(permiso.estado), semaforo)
                Spacer(modifier = Modifier.weight(1f))
                if (permiso.codigo.isNotBlank()) MetaBadge(text = "#${permiso.codigo}")
            }

            Spacer(modifier = Modifier.height(10.dp))
            Surface(shape = RoundedCornerShape(10.dp), color = SuperficieBaja, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = "Motivo: ${permiso.motivo}", fontSize = 12.sp, color = TextoSecundario)
                    if (permiso.observacion.isNotBlank() || permiso.revisadoPor.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = semaforo.punto,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            val revisor = if (permiso.revisadoPor.isNotBlank()) " (${permiso.revisadoPor})" else ""
                            Text(
                                text = "Observación: ${permiso.observacion.ifBlank { "-" }}$revisor",
                                fontSize = 12.sp,
                                color = semaforo.texto
                            )
                        }
                    } else if (permiso.estado == "PENDIENTE") {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "En revisión por Recursos Humanos", fontSize = 11.sp, color = TextoTenue)
                    }
                }
            }

            if (acciones != null) {
                Spacer(modifier = Modifier.height(10.dp))
                acciones()
            }
        }
    }
}
