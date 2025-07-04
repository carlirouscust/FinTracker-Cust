package ucne.edu.fintracker.presentation.remote.dto

import org.threeten.bp.LocalDate

data class PagoRecurrenteDto(
    val pagoRecurrenteId: Int = 0,
    val monto: Double,
    val categoriaId: Int,
    val categoria: CategoriaDto,
    val frecuencia: String, // "Mensual", "Semanal", etc.
    val fechaInicio: LocalDate,
    val fechaFin: LocalDate? = null,
    val activo: Boolean = true
)
