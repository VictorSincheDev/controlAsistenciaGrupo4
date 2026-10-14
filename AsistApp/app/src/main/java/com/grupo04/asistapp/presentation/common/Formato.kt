package com.grupo04.asistapp.presentation.common

import java.time.LocalDate
import java.time.LocalTime

// Funciones de ayuda para mostrar fechas y horas en español
object Formato {
    private val DIAS = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
    private val MESES = listOf(
        "enero", "febrero", "marzo", "abril", "mayo", "junio",
        "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"
    )

    private fun parse(fecha: String): LocalDate? = runCatching { LocalDate.parse(fecha) }.getOrNull()

    fun hoy(): String = LocalDate.now().toString()

    fun inicioDeMes(): String = LocalDate.now().withDayOfMonth(1).toString()

    fun mesActual(): String = hoy().take(7) // "2026-10"

    // "Lunes, 12 de octubre de 2026"
    fun fechaLarga(fecha: String): String {
        val d = parse(fecha) ?: return fecha
        return "${DIAS[d.dayOfWeek.value - 1]}, ${d.dayOfMonth} de ${MESES[d.monthValue - 1]} de ${d.year}"
    }

    // "12/10/2026"
    fun fechaCorta(fecha: String): String {
        val d = parse(fecha) ?: return fecha
        return "%02d/%02d/%d".format(d.dayOfMonth, d.monthValue, d.year)
    }

    fun diaSemana(fecha: String): String {
        val d = parse(fecha) ?: return ""
        return DIAS[d.dayOfWeek.value - 1]
    }

    fun numeroDia(fecha: String): String = parse(fecha)?.dayOfMonth?.toString() ?: "--"

    fun mesCorto(fecha: String): String {
        val d = parse(fecha) ?: return ""
        return MESES[d.monthValue - 1].take(3).uppercase()
    }

    // "Octubre 2026"
    fun nombreMesActual(): String {
        val d = LocalDate.now()
        return "${MESES[d.monthValue - 1].replaceFirstChar { it.uppercase() }} ${d.year}"
    }

    // "17:05" -> "05:05 p.m."
    fun hora12(hora: String): String {
        if (hora.isBlank()) return "--:--"
        val partes = hora.split(":")
        val h = partes.getOrNull(0)?.toIntOrNull() ?: return hora
        val m = partes.getOrNull(1) ?: "00"
        val sufijo = if (h < 12) "a.m." else "p.m."
        val h12 = if (h % 12 == 0) 12 else h % 12
        return "%02d:%s %s".format(h12, m, sufijo)
    }

    // 522 -> "8h 42m"
    fun duracion(minutos: Int): String = "${minutos / 60}h %02dm".format(minutos % 60)

    fun aMinutos(hora: String): Int {
        val partes = hora.split(":")
        return (partes.getOrNull(0)?.toIntOrNull() ?: 0) * 60 + (partes.getOrNull(1)?.toIntOrNull() ?: 0)
    }

    fun minutosAHora(minutos: Int): String = "%02d:%02d".format(minutos / 60, minutos % 60)

    fun horaActual(): String {
        val t = LocalTime.now()
        return "%02d:%02d:%02d".format(t.hour, t.minute, t.second)
    }

    fun saludo(): String {
        val h = LocalTime.now().hour
        return when {
            h < 12 -> "¡Buen día"
            h < 19 -> "¡Buenas tardes"
            else -> "¡Buenas noches"
        }
    }

    fun iniciales(nombre: String): String =
        nombre.split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() }

    // "Juan Carlos Pérez Mendoza" -> "Juan Pérez" (primer nombre + primer apellido, si hay 4 palabras)
    fun nombreCorto(nombre: String): String {
        val p = nombre.split(" ").filter { it.isNotBlank() }
        return when {
            p.size >= 4 -> "${p[0]} ${p[2]}"
            p.size >= 2 -> "${p[0]} ${p[1]}"
            else -> nombre
        }
    }

    fun esFechaValida(fecha: String): Boolean =
        Regex("""\d{4}-\d{2}-\d{2}""").matches(fecha) && parse(fecha) != null

    // Fechas (AAAA-MM-DD) de lunes a domingo de la semana actual
    fun semanaActual(): List<String> {
        val hoy = LocalDate.now()
        val lunes = hoy.minusDays((hoy.dayOfWeek.value - 1).toLong())
        return (0..6).map { lunes.plusDays(it.toLong()).toString() }
    }
}
