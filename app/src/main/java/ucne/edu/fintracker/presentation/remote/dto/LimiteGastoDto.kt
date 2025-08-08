package ucne.edu.fintracker.presentation.remote.dto

data class LimiteGastoDto(
    val limiteGastoId: Int = 0,
    val categoriaId: Int,
    val montoLimite: Double,
    val periodo: String, // "Diario", "Mensual", etc.
    val gastadoActual: Double? = null,
    val usuarioId: Int
)
