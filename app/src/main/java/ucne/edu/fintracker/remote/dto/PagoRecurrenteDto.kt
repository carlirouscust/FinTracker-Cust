package ucne.edu.fintracker.remote.dto

import org.threeten.bp.OffsetDateTime

data class PagoRecurrenteDto(
    val pagoRecurrenteId: Int = 0,
    val monto: Double,
    val categoriaId: Int,
    val frecuencia: String,
    val fechaInicio: OffsetDateTime,
    val fechaFin: OffsetDateTime? = null,
    val activo: Boolean = true,
    val usuarioId: Int
)
