package ucne.edu.fintracker.presentation.ajustes

data class SoporteUiState(
    val asunto: String = "",
    val correoElectronico: String = "",
    val mensaje: String = "",
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = "",
    val mensajeEnviado: Boolean = false
)