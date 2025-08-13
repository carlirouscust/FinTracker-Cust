package ucne.edu.fintracker.remote.dto

import org.threeten.bp.OffsetDateTime

data class AhorroRegistro(
    val monto: Double,
    val fecha: OffsetDateTime
)
data class MetaAhorroDto(
    val metaAhorroId: Int = 0,
    val nombreMeta: String,
    val montoObjetivo: Double,
    val fechaFinalizacion: OffsetDateTime,
    val contribucionRecurrente: Double? = null,
    val imagen: String? = null,
    val montoActual: Double? = null,
    val montoAhorrado: Double? = null,
    val fechaMontoAhorrado: OffsetDateTime? = null,
    val usuarioId: Int,
    val ahorros: List<AhorroRegistro> = emptyList()
)
