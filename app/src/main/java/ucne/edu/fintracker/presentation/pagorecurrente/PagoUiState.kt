package ucne.edu.fintracker.presentation.pagorecurrente

import ucne.edu.fintracker.remote.dto.CategoriaDto
import ucne.edu.fintracker.remote.dto.PagoRecurrenteDto

data class PagoUiState(
    val pagos: List<PagoRecurrenteDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val pagoCreado: Boolean = false,
    val pagoSeleccionado: PagoRecurrenteDto? = null,
    val categorias: List<CategoriaDto> = emptyList(),
    val mensajeExito: String? = null
)


