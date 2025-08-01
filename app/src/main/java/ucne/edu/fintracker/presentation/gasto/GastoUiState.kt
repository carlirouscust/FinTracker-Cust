package ucne.edu.fintracker.presentation.gasto

import ucne.edu.fintracker.presentation.remote.dto.TransaccionDto

data class GastoUiState(
    val transacciones: List<TransaccionDto> = emptyList(),
    val filtro: String = "Dia", // "Día", "Semana", etc.
    val tipoSeleccionado: String = "Gasto", // "Gasto" o "Ingreso"
    val isLoading: Boolean = false,
    val error: String? = null
)
