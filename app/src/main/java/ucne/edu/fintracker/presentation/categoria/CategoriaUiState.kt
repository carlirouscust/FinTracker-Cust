package ucne.edu.fintracker.presentation.categoria

import ucne.edu.fintracker.presentation.remote.dto.CategoriaDto

data class CategoriaUiState(
    val nombre: String = "",
    val tipo: String = "Gasto", // o "Ingreso"
    val icono: String = "",
    val colorFondo: String = "FF0000",
    val isLoading: Boolean = false,
    val selectedTabIndex: Int = 0,
    val categorias: List<CategoriaDto> = emptyList(),
    val error: String? = null
)
