package ucne.edu.fintracker.presentation.remote.dto

data class CategoriaDto(
    val categoriaId: Int = 0,
    val nombre: String = "",
    val tipo: String = "Gasto", // "Gasto" o "Ingreso"
    val icono: String = "",
    val colorFondo: String = "FFFFFF", // "#FF5733"
    val usuarioId: Int
)
