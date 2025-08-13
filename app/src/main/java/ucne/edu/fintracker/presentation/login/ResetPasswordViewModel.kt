package ucne.edu.fintracker.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ucne.edu.fintracker.presentation.remote.DataSource
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val dataSource: DataSource
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

    fun resetPassword() {
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
                val usuarios = dataSource.getUsuarios()
                val usuarioExiste = usuarios.any { it.email.equals(currentState.email, ignoreCase = true) }

                if (!usuarioExiste) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "No se encontró ninguna cuenta con ese email"
                        )
                    }
                    return@launch
                }

                kotlinx.coroutines.delay(2000)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isEmailSent = true,
                        error = null
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al enviar el email: ${e.message}"
                    )
                }
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
        return email.isNotBlank() && emailRegex.matches(email)
    }

    fun clearState() {
        _uiState.value = ResetPasswordUiState()
    }
}