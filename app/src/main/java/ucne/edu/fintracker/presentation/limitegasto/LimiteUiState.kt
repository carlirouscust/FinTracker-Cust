package ucne.edu.fintracker.presentation.limitegasto

import ucne.edu.fintracker.remote.dto.LimiteGastoDto

data class LimiteUiState(
    val limites: List<LimiteGastoDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val limiteCreado: Boolean = false
)

