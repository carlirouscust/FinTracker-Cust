package ucne.edu.fintracker.presentation.remote.dto

import org.threeten.bp.OffsetDateTime

data class TransaccionDto(
    val transaccionId: Int = 0,
    val monto: Double,
    val categoriaId: Int,
    val categoria: CategoriaDto? = null,
    val fecha: OffsetDateTime,
    val notas: String? = null,
    val tipo: String // "Gasto" o "Ingreso"
   )
