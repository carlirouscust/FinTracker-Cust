package ucne.edu.fintracker.presentation.remote.dto

import org.threeten.bp.OffsetDateTime

data class TransaccionDto(
    val transaccionId: Int = 0,
    val monto: Double,
    val categoriaId: Int,
//    val categoriaId: Int,
    val fecha: OffsetDateTime,
    val notas: String? = null,
    val tipo: String, // "Gasto" o "Ingreso"
    val usuarioId: Int
   )
