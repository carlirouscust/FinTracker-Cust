package ucne.edu.fintracker.presentation.login

data class LoginUiState(
    val loginEmail: String = "",
    val loginPassword: String = "",
    val registerNombre: String = "",
    val registerEmail: String = "",
    val registerPassword: String = "",
    val usuarioId: Int = 0
)