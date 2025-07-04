package ucne.edu.fintracker.presentation.remote.dto

import org.threeten.bp.LocalDate

data class TransaccionDto(
    val transaccionId: Int = 0,
    val monto: Double,
    val categoriaId: Int,
    val categoria: CategoriaDto,
    val fecha: LocalDate,
    val notas: String? = null,
    val tipo: String // "Gasto" o "Ingreso"
   )
