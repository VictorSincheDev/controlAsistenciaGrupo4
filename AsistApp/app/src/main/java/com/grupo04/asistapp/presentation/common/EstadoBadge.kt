package com.grupo04.asistapp.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grupo04.asistapp.ui.theme.Ambar
import com.grupo04.asistapp.ui.theme.AmbarFondo
import com.grupo04.asistapp.ui.theme.AmbarTexto
import com.grupo04.asistapp.ui.theme.AzulClaro
import com.grupo04.asistapp.ui.theme.AzulCorporativo
import com.grupo04.asistapp.ui.theme.Rojo
import com.grupo04.asistapp.ui.theme.RojoFondo
import com.grupo04.asistapp.ui.theme.RojoTexto
import com.grupo04.asistapp.ui.theme.Verde
import com.grupo04.asistapp.ui.theme.VerdeFondo
import com.grupo04.asistapp.ui.theme.VerdeTexto

// Verde = A tiempo / Aprobado, Ámbar = Tardanza / En trámite, Rojo = Ausencia / Rechazado
enum class Semaforo(val fondo: Color, val texto: Color, val punto: Color) {
    VERDE(VerdeFondo, VerdeTexto, Verde),
    AMBAR(AmbarFondo, AmbarTexto, Ambar),
    ROJO(RojoFondo, RojoTexto, Rojo),
    AZUL(AzulClaro, AzulCorporativo, AzulCorporativo)
}

fun semaforoAsistencia(estado: String): Semaforo = if (estado == "TARDANZA") Semaforo.AMBAR else Semaforo.VERDE

fun textoAsistencia(estado: String): String = if (estado == "TARDANZA") "Tardanza" else "A tiempo"

fun semaforoPermiso(estado: String): Semaforo = when (estado) {
    "APROBADO" -> Semaforo.VERDE
    "RECHAZADO" -> Semaforo.ROJO
    else -> Semaforo.AMBAR
}

fun textoPermiso(estado: String): String = when (estado) {
    "APROBADO" -> "Aprobado"
    "RECHAZADO" -> "Rechazado"
    else -> "En trámite"
}

@Composable
fun EstadoBadge(texto: String, semaforo: Semaforo) {
    Surface(shape = CircleShape, color = semaforo.fondo) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Box(modifier = Modifier.size(6.dp).background(semaforo.punto, CircleShape))
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = texto,
                color = semaforo.texto,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
