package ucne.edu.fintracker.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import ucne.edu.fintracker.presentation.remote.FinTrackerApi
import ucne.edu.fintracker.presentation.remote.dto.ResetPasswordRequest
import ucne.edu.fintracker.presentation.remote.dto.UsuarioDto
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val api: FinTrackerApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun login() {
        viewModelScope.launch {
            try {
                val usuarios = api.getUsuario()
                val usuario = usuarios.find {
                    it.email == _uiState.value.loginEmail &&
                            it.contraseña == _uiState.value.loginPassword
                }

                if (usuario != null) {
                    _uiState.update {
                        it.copy(usuarioId = usuario.usuarioId ?: 0, loginError = false)
                    }
                } else {
                    _uiState.update {
                        it.copy(loginError = true)
                    }
                }
            } catch (e: Exception) {
                println("Error al intentar login: ${e.message}")
                _uiState.update {
                    it.copy(loginError = true)
                }
            }
        }
    }


    fun registerUser(
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val state = uiState.value
                val newUser = UsuarioDto(
                    usuarioId = 0,
                    nombre = state.registerNombre,
                    apellido = state.registerApellido,
                    email = state.registerEmail,
                    contraseña = state.registerPassword,
                    divisa = "DOP"
                )
                api.createUsuario(newUser)
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    fun onLoginEmailChange(value: String) {
        _uiState.update { it.copy(loginEmail = value) }
    }

    fun onLoginPasswordChange(value: String) {
        _uiState.update { it.copy(loginPassword = value) }
    }

    fun onRegisterNombreChange(value: String) {
        _uiState.update { it.copy(registerNombre = value) }
    }


    fun onRegisterApellidoChange(value: String) {
        _uiState.update { it.copy(registerApellido = value) }
    }


    fun onRegisterEmailChange(value: String) {
        _uiState.update { it.copy(registerEmail = value) }
    }

    fun onRegisterPasswordChange(value: String) {
        _uiState.update { it.copy(registerPassword = value) }
    }


    fun onResetEmailChange(value: String) {
        _uiState.update { it.copy(resetEmail = value, resetError = null, resetSuccess = null) }
    }

    fun resetPassword() {
        viewModelScope.launch {
            try {
                val email = uiState.value.resetEmail
                val response = api.enviarLinkResetPassword(ResetPasswordRequest(email))
                if (response.isSuccessful) {
                    println("Enlace enviado")
                } else {
                    println("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                println("Excepción: ${e.message}")
            }
        }
    }

    fun changeTab(index: Int) {
        _uiState.update { it.copy(tabIndex = index) }
    }
}

