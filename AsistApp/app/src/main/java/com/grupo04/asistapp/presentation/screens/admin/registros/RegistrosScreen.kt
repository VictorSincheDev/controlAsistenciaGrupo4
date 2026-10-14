package com.grupo04.asistapp.presentation.screens.admin.registros

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.grupo04.asistapp.presentation.common.AsistenciaItem
import com.grupo04.asistapp.presentation.common.CuadroInfo
import com.grupo04.asistapp.presentation.common.EmptyState
import com.grupo04.asistapp.presentation.common.Formato
import com.grupo04.asistapp.presentation.common.LoadingOverlay
import com.grupo04.asistapp.ui.theme.AzulClaro
import com.grupo04.asistapp.ui.theme.AzulMarino

@Composable
fun RegistrosScreen(viewModel: RegistrosViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarRegistros()
    }

    val lista = uiState.filteredAsistencias

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            OutlinedTextField(
                value = uiState.query,
                onValueChange = viewModel::onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar por trabajador o área...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = uiState.fecha,
                    onValueChange = viewModel::onFechaChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Fecha AAAA-MM-DD (opcional)") },
                    leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                TextButton(onClick = { viewModel.onFechaChange(if (uiState.fecha.isBlank()) Formato.hoy() else "") }) {
                    Text(if (uiState.fecha.isBlank()) "Hoy" else "Limpiar", color = AzulMarino)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                listOf("TODOS" to "Todos", "A_TIEMPO" to "A tiempo", "TARDANZA" to "Tardanzas").forEach { (valor, texto) ->
                    FilterChip(
                        selected = uiState.filtro == valor,
                        onClick = { viewModel.onFiltroChange(valor) },
                        label = { Text(texto) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = AzulClaro)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.weight(1f))
                CuadroInfo(Icons.Default.HowToReg, "Total: ${lista.size} registros")
            }
            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (lista.isEmpty() && !uiState.isLoading) {
                    item { EmptyState(Icons.Default.Search, "No se encontraron registros con esos filtros") }
                }
                items(lista, key = { it.id }) { asistencia ->
                    AsistenciaItem(asistencia = asistencia, mostrarTrabajador = true)
                }
            }
        }

        LoadingOverlay(isLoading = uiState.isLoading)
    }
}
