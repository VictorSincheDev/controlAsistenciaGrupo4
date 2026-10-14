package com.grupo04.asistapp.presentation.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grupo04.asistapp.ui.theme.AzulClaro
import com.grupo04.asistapp.ui.theme.AzulMarino

@Composable
fun AvatarIniciales(
    nombre: String,
    size: Dp = 48.dp,
    fondo: Color = AzulClaro,
    colorTexto: Color = AzulMarino
) {
    Surface(shape = CircleShape, color = fondo, modifier = Modifier.size(size)) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = Formato.iniciales(nombre),
                color = colorTexto,
                fontWeight = FontWeight.Bold,
                fontSize = (size.value * 0.36f).sp
            )
        }
    }
}
