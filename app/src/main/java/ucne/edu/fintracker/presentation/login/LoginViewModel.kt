package ucne.edu.fintracker.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ucne.edu.fintracker.presentation.remote.FinTrackerApi
import ucne.edu.fintracker.presentation.remote.dto.UsuarioDto
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val api: FinTrackerApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun changeTab(index: Int) {
        _uiState.update { it.copy(usuarioId = index) }
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

    fun onRegisterEmailChange(value: String) {
        _uiState.update { it.copy(registerEmail = value) }
    }

    fun onRegisterPasswordChange(value: String) {
        _uiState.update { it.copy(registerPassword = value) }
    }

    fun registerUser(
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val state = uiState.value
                val newUser = UsuarioDto(
                    usuarioId= 0,
                    nombre = state.registerNombre,
                    apellido = "ApellidoEjemplo",
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
}


