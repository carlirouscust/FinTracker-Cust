package ucne.edu.fintracker.presentation.login

data class ResetPasswordUiState(
    val step: ResetPasswordStep = ResetPasswordStep.EMAIL_INPUT,
    val email: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val emailError: Boolean = false,
    val passwordError: Boolean = false,
    val confirmPasswordError: Boolean = false,
    val isPasswordUpdated: Boolean = false,
    val foundUserId: Int? = null
)

enum class ResetPasswordStep {
    EMAIL_INPUT,
    PASSWORD_RESET,
    SUCCESS
}