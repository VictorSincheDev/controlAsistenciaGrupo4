package com.grupo04.asistapp.presentation.screens.ausencias

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
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.grupo04.asistapp.domain.model.Ausencia
import com.grupo04.asistapp.presentation.common.EmptyState
import com.grupo04.asistapp.presentation.common.EstadoBadge
import com.grupo04.asistapp.presentation.common.Formato
import com.grupo04.asistapp.presentation.common.LoadingOverlay
import com.grupo04.asistapp.presentation.common.SeccionTitulo
import com.grupo04.asistapp.presentation.common.Semaforo
import com.grupo04.asistapp.ui.theme.AzulClaro
import com.grupo04.asistapp.ui.theme.AzulCorporativo
import com.grupo04.asistapp.ui.theme.AzulMarino
import com.grupo04.asistapp.ui.theme.Borde
import com.grupo04.asistapp.ui.theme.Rojo
import com.grupo04.asistapp.ui.theme.RojoFondo
import com.grupo04.asistapp.ui.theme.RojoTexto
import com.grupo04.asistapp.ui.theme.TextoPrincipal
import com.grupo04.asistapp.ui.theme.TextoSecundario
import com.grupo04.asistapp.ui.theme.Verde
import com.grupo04.asistapp.ui.theme.VerdeFondo
import com.grupo04.asistapp.ui.theme.VerdeTexto

@Composable
fun AusenciasScreen(
    onJustificar: (fecha: String) -> Unit,
    viewModel: AusenciasViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarAusencias()
    }

    val injustificadas = uiState.ausencias.count { !it.esJustificada }
    val justificadas = uiState.ausencias.size - injustificadas

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                SeccionTitulo(icono = Icons.Default.EventBusy, titulo = "Resumen de Incidencias", detalle = "Últimos 60 días")
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(shape = RoundedCornerShape(14.dp), color = RojoFondo, modifier = Modifier.weight(1f)) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Rojo)
                            Text(text = "$injustificadas", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Rojo)
                            Text(text = "Injustificadas", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = RojoTexto)
                            Text(text = "Requieren sustento", fontSize = 10.sp, color = RojoTexto)
                        }
                    }
                    Surface(shape = RoundedCornerShape(14.dp), color = VerdeFondo, modifier = Modifier.weight(1f)) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = Verde)
                            Text(text = "$justificadas", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Verde)
                            Text(text = "Justificadas", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = VerdeTexto)
                            Text(text = "Con permiso aprobado", fontSize = 10.sp, color = VerdeTexto)
                        }
                    }
                }
            }
            item {
                Surface(shape = RoundedCornerShape(14.dp), color = AzulClaro.copy(alpha = 0.5f), modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.Gavel, contentDescription = null, tint = AzulCorporativo, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Aviso: las ausencias injustificadas impactan en el cálculo de planilla y dominical según la Ley Laboral Peruana (D.L. 728).",
                            fontSize = 12.sp,
                            color = AzulMarino
                        )
                    }
                }
            }
            item {
                SeccionTitulo(icono = Icons.Default.EventBusy, titulo = "Registro de Ausencias", detalle = "${uiState.ausencias.size} eventos")
            }
            if (uiState.ausencias.isEmpty() && !uiState.isLoading) {
                item { EmptyState(Icons.Default.ThumbUp, "No registras ausencias. ¡Sigue así!") }
            }
            items(uiState.ausencias, key = { it.fecha }) { ausencia ->
                AusenciaItem(ausencia = ausencia, onJustificar = { onJustificar(ausencia.fecha) })
            }
        }

        LoadingOverlay(isLoading = uiState.isLoading)
    }
}

@Composable
private fun AusenciaItem(ausencia: Ausencia, onJustificar: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Borde),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (ausencia.esJustificada) Icons.Default.EventAvailable else Icons.Default.EventBusy,
                    contentDescription = null,
                    tint = if (ausencia.esJustificada) Verde else Rojo,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${Formato.fechaCorta(ausencia.fecha)} • ${ausencia.dia}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextoPrincipal,
                    modifier = Modifier.weight(1f)
                )
                if (ausencia.esJustificada) EstadoBadge("Justificada", Semaforo.VERDE)
                else EstadoBadge("Injustificada", Semaforo.ROJO)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Motivo: ${ausencia.motivo}", fontSize = 12.sp, color = TextoSecundario)
            if (!ausencia.esJustificada) {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onJustificar,
                    colors = ButtonDefaults.buttonColors(containerColor = AzulMarino),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Justificar con una solicitud de permiso", fontSize = 13.sp)
                }
            }
        }
    }
}
