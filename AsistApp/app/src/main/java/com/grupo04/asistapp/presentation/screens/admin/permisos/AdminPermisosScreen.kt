package com.grupo04.asistapp.presentation.screens.admin.permisos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.grupo04.asistapp.presentation.common.EmptyState
import com.grupo04.asistapp.presentation.common.LoadingOverlay
import com.grupo04.asistapp.presentation.common.PermisoItem
import com.grupo04.asistapp.ui.theme.AzulClaro
import com.grupo04.asistapp.ui.theme.Rojo
import com.grupo04.asistapp.ui.theme.Verde

@Composable
fun AdminPermisosScreen(viewModel: AdminPermisosViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarPermisos()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "PENDIENTE" to "Pendientes (${uiState.permisos.count { it.estado == "PENDIENTE" }})",
                        "APROBADO" to "Aprobados", "RECHAZADO" to "Rechazados", "TODOS" to "Todos"
                    ).forEach { (valor, texto) ->
                        FilterChip(
                            selected = uiState.filtro == valor,
                            onClick = { viewModel.onFiltroChange(valor) },
                            label = { Text(texto) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = AzulClaro)
                        )
                    }
                }
            }
            if (uiState.permisosFiltrados.isEmpty() && !uiState.isLoading) {
                item { EmptyState(Icons.AutoMirrored.Filled.Assignment, "No hay solicitudes en esta categoría") }
            }
            items(uiState.permisosFiltrados, key = { it.id }) { permiso ->
                // Solo las solicitudes pendientes muestran los botones Aprobar / Rechazar
                val acciones: (@Composable () -> Unit)? = if (permiso.estado == "PENDIENTE") {
                    @Composable {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedButton(
                                onClick = { viewModel.seleccionar(permiso, "RECHAZADO") },
                                border = BorderStroke(1.dp, Rojo),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null, tint = Rojo, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Rechazar", color = Rojo, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { viewModel.seleccionar(permiso, "APROBADO") },
                                colors = ButtonDefaults.buttonColors(containerColor = Verde),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Aprobar", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else null

                PermisoItem(permiso = permiso, mostrarTrabajador = true, acciones = acciones)
            }
        }

        LoadingOverlay(isLoading = uiState.isLoading)
    }

    uiState.permisoSeleccionado?.let { permiso ->
        val aprobar = uiState.accion == "APROBADO"
        AlertDialog(
            onDismissRequest = { viewModel.dismissDialog() },
            title = { Text(if (aprobar) "Aprobar permiso" else "Rechazar permiso") },
            text = {
                Column {
                    Text("${permiso.tipo} de ${permiso.trabajadorNombre}")
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = uiState.observacion,
                        onValueChange = viewModel::onObservacionChange,
                        label = { Text(if (aprobar) "Observación (opcional)" else "Motivo del rechazo") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.confirmar() }) {
                    Text(if (aprobar) "Aprobar" else "Rechazar", color = if (aprobar) Verde else Rojo, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissDialog() }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
