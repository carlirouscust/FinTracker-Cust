package ucne.edu.fintracker.presentation.ajustes

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SoporteViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(SoporteUiState())
    val uiState: StateFlow<SoporteUiState> = _uiState.asStateFlow()

    fun onAsuntoChange(asunto: String) {
        _uiState.update { it.copy(asunto = asunto) }
    }

    fun onCorreoElectronicoChange(correo: String) {
        _uiState.update { it.copy(correoElectronico = correo) }
    }

    fun onMensajeChange(mensaje: String) {
        _uiState.update { it.copy(mensaje = mensaje) }
    }

    fun enviarMensaje() {
        val state = _uiState.value

        if (state.asunto.isBlank()) {
            _uiState.update {
                it.copy(
                    isError = true,
                    errorMessage = "El asunto es requerido"
                )
            }
            return
        }

        if (state.correoElectronico.isBlank()) {
            _uiState.update {
                it.copy(
                    isError = true,
                    errorMessage = "El correo electrónico es requerido"
                )
            }
            return
        }

        if (!isValidEmail(state.correoElectronico)) {
            _uiState.update {
                it.copy(
                    isError = true,
                    errorMessage = "Ingrese un correo electrónico válido"
                )
            }
            return
        }

        if (state.mensaje.isBlank()) {
            _uiState.update {
                it.copy(
                    isError = true,
                    errorMessage = "El mensaje es requerido"
                )
            }
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update {
                    it.copy(
                        isLoading = true,
                        isError = false,
                        errorMessage = ""
                    )
                }

                kotlinx.coroutines.delay(2000)

                Log.d("Soporte", "Mensaje enviado - Asunto: ${state.asunto}, Correo: ${state.correoElectronico}")

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        mensajeEnviado = true,
                        asunto = "",
                        correoElectronico = "",
                        mensaje = ""
                    )
                }

                kotlinx.coroutines.delay(3000)
                _uiState.update { it.copy(mensajeEnviado = false) }

            } catch (e: Exception) {
                Log.e("Soporte", "Error al enviar mensaje: ${e.message}")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = "Error al enviar el mensaje. Inténtelo de nuevo."
                    )
                }
            }
        }
    }

    fun limpiarError() {
        _uiState.update {
            it.copy(
                isError = false,
                errorMessage = ""
            )
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun cargarDatosUsuario(correoUsuario: String) {
        _uiState.update { it.copy(correoElectronico = correoUsuario) }
    }
}