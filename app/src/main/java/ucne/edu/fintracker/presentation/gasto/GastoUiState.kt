package ucne.edu.fintracker.presentation.gasto

import ucne.edu.fintracker.remote.dto.TransaccionDto

data class GastoUiState(
    val transacciones: List<TransaccionDto> = emptyList(),
    val filtro: String = "Semana",
    val tipoSeleccionado: String = "Gasto",
    val isLoading: Boolean = false,
    val error: String? = null
)
