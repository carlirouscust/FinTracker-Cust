package ucne.edu.fintracker.remote.dto

import org.threeten.bp.OffsetDateTime

data class TransaccionDto(
    val transaccionId: Int = 0,
    val monto: Double,
    val categoriaId: Int,
    val fecha: OffsetDateTime,
    val notas: String? = null,
    val tipo: String,
    val usuarioId: Int
   )
