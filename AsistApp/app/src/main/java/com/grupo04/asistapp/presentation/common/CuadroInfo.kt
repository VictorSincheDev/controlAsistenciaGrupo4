package com.grupo04.asistapp.presentation.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grupo04.asistapp.ui.theme.AzulClaro
import com.grupo04.asistapp.ui.theme.AzulCorporativo
import com.grupo04.asistapp.ui.theme.AzulSuave

@Composable
fun CuadroInfo(icono: ImageVector, texto: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = AzulClaro.copy(alpha = 0.6f),
        border = BorderStroke(1.dp, AzulSuave)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = AzulCorporativo,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = texto,
                color = AzulCorporativo,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
