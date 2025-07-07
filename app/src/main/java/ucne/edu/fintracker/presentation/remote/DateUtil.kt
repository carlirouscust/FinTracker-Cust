package ucne.edu.fintracker.presentation.remote

import org.threeten.bp.LocalDate

class DateUtil {
    companion object {
        private var idCounter = 1000
        fun generateUniqueId(): Int {
            idCounter++
            return idCounter
        }

        fun parseFecha(fechaTexto: String): LocalDate {
            return when (fechaTexto) {
                "Hoy" -> LocalDate.now()
                "Ayer" -> LocalDate.now().minusDays(1)
                else -> {
                    val partes = fechaTexto.split("/")
                    if (partes.size == 3) {
                        val dia = partes[0].toIntOrNull() ?: 1
                        val mes = partes[1].toIntOrNull() ?: 1
                        val anio = partes[2].toIntOrNull() ?: 2025
                        LocalDate.of(anio, mes, dia)
                    } else {
                        LocalDate.now()
                    }
                }
            }
        }
    }
}

