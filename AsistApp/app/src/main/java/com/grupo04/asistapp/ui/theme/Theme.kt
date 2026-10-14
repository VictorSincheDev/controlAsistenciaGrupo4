package com.grupo04.asistapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = AzulMarino,
    onPrimary = Color.White,
    primaryContainer = AzulClaro,
    onPrimaryContainer = AzulNoche,
    secondary = AzulCorporativo,
    onSecondary = Color.White,
    background = Fondo,
    onBackground = TextoPrincipal,
    surface = Color.White,
    onSurface = TextoPrincipal,
    surfaceVariant = SuperficieBaja,
    onSurfaceVariant = TextoSecundario,
    outline = TextoTenue,
    error = Rojo,
    onError = Color.White
)

@Composable
fun AsistAppTheme(
    content: @Composable () -> Unit
) {
    // El prototipo de Stitch usa siempre el tema claro corporativo
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
