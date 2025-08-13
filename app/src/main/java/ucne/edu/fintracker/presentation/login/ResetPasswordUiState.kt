package ucne.edu.fintracker.presentation.login

data class ResetPasswordUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val isEmailSent: Boolean = false,
    val error: String? = null,
    val emailError: Boolean = false
)