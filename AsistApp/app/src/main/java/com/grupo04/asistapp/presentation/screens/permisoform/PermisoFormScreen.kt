package com.grupo04.asistapp.presentation.screens.permisoform

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.grupo04.asistapp.presentation.common.AvatarIniciales
import com.grupo04.asistapp.presentation.common.LoadingOverlay
import com.grupo04.asistapp.presentation.common.SeccionTitulo
import com.grupo04.asistapp.ui.theme.AzulMarino
import com.grupo04.asistapp.ui.theme.Borde
import com.grupo04.asistapp.ui.theme.TextoPrincipal
import com.grupo04.asistapp.ui.theme.TextoTenue
import com.grupo04.asistapp.ui.theme.Verde

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermisoFormScreen(onSuccess: () -> Unit, viewModel: PermisoFormViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Colaborador
            uiState.trabajador?.let { trabajador ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Borde),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        AvatarIniciales(nombre = trabajador.nombres, size = 40.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "Colaborador: ${trabajador.nombres}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextoPrincipal)
                            Text(text = "Área: ${trabajador.area} • ${trabajador.turno}", fontSize = 11.sp, color = TextoTenue)
                        }
                    }
                }
            }

            SeccionTitulo(icono = Icons.Default.Category, titulo = "Tipo de Permiso Laboral")
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = uiState.tipo,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Selecciona el tipo de permiso") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    uiState.tipos.forEach { tipo ->
                        DropdownMenuItem(
                            text = { Text(tipo.nombre) },
                            onClick = {
                                viewModel.onTipoChange(tipo.nombre)
                                expanded = false
                            }
                        )
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Verde, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Sujeto a validación de Recursos Humanos", fontSize = 11.sp, color = TextoTenue)
            }

            SeccionTitulo(icono = Icons.Default.DateRange, titulo = "Periodo Solicitado")
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = uiState.fechaInicio,
                    onValueChange = viewModel::onFechaInicioChange,
                    label = { Text("Fecha de inicio") },
                    placeholder = { Text("AAAA-MM-DD") },
                    leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = uiState.fechaFin,
                    onValueChange = viewModel::onFechaFinChange,
                    label = { Text("Fecha de fin") },
                    placeholder = { Text("AAAA-MM-DD") },
                    leadingIcon = { Icon(Icons.Default.EventAvailable, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            SeccionTitulo(icono = Icons.Default.EditNote, titulo = "Motivo Principal")
            OutlinedTextField(
                value = uiState.motivo,
                onValueChange = viewModel::onMotivoChange,
                label = { Text("Observaciones adicionales / detalles") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4
            )

            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = { viewModel.enviarSolicitud(onSuccess) },
                colors = ButtonDefaults.buttonColors(containerColor = AzulMarino),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("ENVIAR SOLICITUD A RRHH", fontWeight = FontWeight.Bold)
            }
            OutlinedButton(
                onClick = { viewModel.cancelar(onSuccess) },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar", color = AzulMarino)
            }
        }

        LoadingOverlay(isLoading = uiState.isLoading)
    }
}
