package ucne.edu.fintracker.presentation.categoria

import ucne.edu.fintracker.presentation.remote.dto.CategoriaDto

data class CategoriaUiState(
    val selectedTabIndex: Int = 0,
    val categorias: List<CategoriaDto> = emptyList()
)
