package ucne.edu.fintracker.presentation.remote.dto

import org.threeten.bp.LocalDate

data class MetaAhorroDto(
    val metaAhorroId: Int = 0,
    val nombreMeta: String,
    val montoObjetivo: Double,
    val fechaFinalizacion: LocalDate,
    val contribucionRecurrente: Double? = null,
    val imagen: String? = null,
    val montoActual: Double? = null
)
