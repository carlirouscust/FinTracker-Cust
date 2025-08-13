package ucne.edu.fintracker.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ucne.edu.fintracker.remote.FinTrackerApi
import javax.inject.Inject



@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val api: FinTrackerApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResetPasswordUiState())
    val uiState: StateFlow<ResetPasswordUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update {
            it.copy(
                email = email,
                emailError = false,
                error = null
            )
        }
    }

    fun onNewPasswordChange(password: String) {
        _uiState.update {
            it.copy(
                newPassword = password,
                passwordError = false,
                error = null
            )
        }
    }

    fun onConfirmPasswordChange(password: String) {
        _uiState.update {
            it.copy(
                confirmPassword = password,
                confirmPasswordError = false,
                error = null
            )
        }
    }

    fun verifyEmail() {
        val currentState = _uiState.value

        if (!isValidEmail(currentState.email)) {
            _uiState.update {
                it.copy(emailError = true, error = "Por favor ingresa un email válido")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val usuarios = api.getUsuarios()
                val usuario = usuarios.find { it.email.equals(currentState.email, ignoreCase = true) }

                if (usuario == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "No se encontró ninguna cuenta con ese email"
                        )
                    }
                    return@launch
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        step = ResetPasswordStep.PASSWORD_RESET,
                        foundUserId = usuario.usuarioId,
                        error = null
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al verificar el email: ${e.message}"
                    )
                }
            }
        }
    }

    fun updatePassword() {
        val currentState = _uiState.value

        var hasErrors = false

        if (!isValidPassword(currentState.newPassword)) {
            _uiState.update {
                it.copy(
                    passwordError = true,
                    error = "La contraseña debe tener al menos 8 caracteres, incluir letras, números y símbolos"
                )
            }
            hasErrors = true
        }

        if (currentState.newPassword != currentState.confirmPassword) {
            _uiState.update {
                it.copy(
                    confirmPasswordError = true,
                    error = "Las contraseñas no coinciden"
                )
            }
            hasErrors = true
        }

        if (hasErrors) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val userId = currentState.foundUserId ?: throw Exception("ID de usuario no encontrado")

                val usuarioActual = api.getUsuario(userId)

                val usuarioActualizado = usuarioActual.copy(
                    contraseña = currentState.newPassword
                )

                try {
                    val resultado = api.updateUsuario(userId, usuarioActualizado)

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            step = ResetPasswordStep.SUCCESS,
                            isPasswordUpdated = true,
                            error = null
                        )
                    }

                } catch (updateException: Exception) { // Si hay error de respuesta nula pero posiblemente se guardó
                    if (updateException.message?.contains("null") == true ||
                        updateException.message?.contains("non-null") == true) {

                        try {
                            val usuarioVerificacion = api.getUsuario(userId)
                            if (usuarioVerificacion.contraseña == currentState.newPassword) {
                                // Se guardó correctamente
                                _uiState.update {
                                    it.copy(
                                        isLoading = false,
                                        step = ResetPasswordStep.SUCCESS,
                                        isPasswordUpdated = true,
                                        error = null
                                    )
                                }
                            } else {
                                throw Exception("La contraseña no se actualizó correctamente")
                            }
                        } catch (verificationException: Exception) {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    step = ResetPasswordStep.SUCCESS,
                                    isPasswordUpdated = true,
                                    error = null
                                )
                            }
                        }
                    } else {
                        throw updateException
                    }
                }

            } catch (e: Exception) {
                android.util.Log.e("ResetPasswordVM", "Error al actualizar contraseña: ${e.message}")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al actualizar la contraseña: ${e.message}"
                    )
                }
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
        return email.isNotBlank() && emailRegex.matches(email)
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 8 &&
                password.any { it.isLetter() } &&
                password.any { it.isDigit() } &&
                password.any { it in "!@#$%^&*()_+-=[]{}|;:,.<>?" }
    }

    fun clearState() {
        _uiState.value = ResetPasswordUiState()
    }

    fun goBackToEmailInput() {
        _uiState.update {
            it.copy(
                step = ResetPasswordStep.EMAIL_INPUT,
                newPassword = "",
                confirmPassword = "",
                passwordError = false,
                confirmPasswordError = false,
                error = null
            )
        }
    }
}