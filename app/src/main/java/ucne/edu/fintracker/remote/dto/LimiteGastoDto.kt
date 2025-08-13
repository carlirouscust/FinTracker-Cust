package ucne.edu.fintracker.remote.dto

data class LimiteGastoDto(
    val limiteGastoId: Int = 0,
    val categoriaId: Int,
    val montoLimite: Double,
    val periodo: String,
    val gastadoActual: Double? = null,
    val usuarioId: Int
)
