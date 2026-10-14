package com.grupo04.asistapp.presentation.screens.permisos

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import com.grupo04.asistapp.presentation.common.EmptyState
import com.grupo04.asistapp.presentation.common.LoadingOverlay
import com.grupo04.asistapp.presentation.common.PermisoItem
import com.grupo04.asistapp.ui.theme.Ambar
import com.grupo04.asistapp.ui.theme.AmbarFondo
import com.grupo04.asistapp.ui.theme.AzulClaro
import com.grupo04.asistapp.ui.theme.AzulMarino
import com.grupo04.asistapp.ui.theme.SuperficieBaja
import com.grupo04.asistapp.ui.theme.TextoSecundario
import com.grupo04.asistapp.ui.theme.Verde
import com.grupo04.asistapp.ui.theme.VerdeFondo

@Composable
fun PermisosScreen(
    onNuevoPermiso: () -> Unit,
    viewModel: PermisosViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarPermisos()
    }

    val aprobados = uiState.permisos.count { it.estado == "APROBADO" }
    val enTramite = uiState.permisos.count { it.estado == "PENDIENTE" }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Mis solicitudes",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AzulMarino,
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = onNuevoPermiso,
                        colors = ButtonDefaults.buttonColors(containerColor = AzulMarino),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Nuevo Permiso")
                    }
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Contador("${uiState.permisos.size}", "Registrados", AzulMarino, SuperficieBaja, Modifier.weight(1f))
                    Contador("$aprobados", "Aprobados", Verde, VerdeFondo, Modifier.weight(1f))
                    Contador("$enTramite", "En trámite", Ambar, AmbarFondo, Modifier.weight(1f))
                }
            }
            item {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "TODOS" to "Todos", "PENDIENTE" to "Pendientes",
                        "APROBADO" to "Aprobados", "RECHAZADO" to "Rechazados"
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
                item { EmptyState(Icons.AutoMirrored.Filled.Assignment, "No tienes solicitudes en esta categoría") }
            }
            items(uiState.permisosFiltrados, key = { it.id }) { permiso ->
                PermisoItem(permiso = permiso)
            }
            item {
                Button(
                    onClick = onNuevoPermiso,
                    colors = ButtonDefaults.buttonColors(containerColor = AzulMarino),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Icon(Icons.Default.PostAdd, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Solicitar Nuevo Permiso", fontWeight = FontWeight.Bold)
                }
            }
        }

        LoadingOverlay(isLoading = uiState.isLoading)
    }

}

@Composable
private fun Contador(valor: String, etiqueta: String, color: Color, fondo: Color, modifier: Modifier = Modifier) {
    Surface(color = fondo, shape = RoundedCornerShape(14.dp), modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            Text(valor, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = color)
            Text(etiqueta, fontSize = 12.sp, color = TextoSecundario)
        }
    }
}
