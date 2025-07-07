package ucne.edu.fintracker.presentation.login

data class LoginUiState(
    val loginEmail: String = "",
    val loginPassword: String = "",
    val registerNombre: String = "",
    val registerEmail: String = "",
    val registerPassword: String = "",
    val usuarioId: Int = 0,
    val tabIndex: Int = 0,
    val loginError: Boolean = false,

    val resetEmail: String = "",
    val resetError: String? = null,
    val resetSuccess: String? = null
)