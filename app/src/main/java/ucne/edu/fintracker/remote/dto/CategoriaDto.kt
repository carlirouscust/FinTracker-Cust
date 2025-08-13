package ucne.edu.fintracker.remote.dto

data class CategoriaDto(
    val categoriaId: Int = 0,
    val nombre: String = "",
    val tipo: String = "Gasto",
    val icono: String = "",
    val colorFondo: String = "FFFFFF",
    val usuarioId: Int
)
