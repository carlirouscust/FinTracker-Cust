package ucne.edu.fintracker.presentation.panelUsuario

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import ucne.edu.fintracker.presentation.remote.FinTrackerApi
import ucne.edu.fintracker.presentation.remote.dto.UsuarioDto
import javax.inject.Inject

@HiltViewModel
class PanelUsuarioViewModel @Inject constructor(
    private val api: FinTrackerApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(PanelUsuarioUiState())
    val uiState: StateFlow<PanelUsuarioUiState> = _uiState.asStateFlow()

    fun cargarUsuario(usuarioId: Int) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    isError = false,
                    errorMessage = ""
                )

                val usuario = api.getUsuario(usuarioId)

                Log.d("PanelUsuario", "Usuario cargado: $usuario")

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    usuario = usuario,
                    isError = false,
                    errorMessage = ""
                )

            } catch (e: HttpException) {
                Log.e("PanelUsuario", "HttpException: ${e.code()} - ${e.message()}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isError = true,
                    errorMessage = when (e.code()) {
                        404 -> "Usuario no encontrado"
                        401 -> "No autorizado"
                        500 -> "Error del servidor"
                        else -> "Error al cargar los datos del usuario"
                    }
                )
            } catch (e: Exception) {
                Log.e("PanelUsuario", "Error al cargar usuario: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isError = true,
                    errorMessage = "Error de conexión: ${e.message}"
                )
            }
        }
    }

    fun actualizarUsuario(usuarioId: Int, usuario: UsuarioDto) {
        viewModelScope.launch {
            try {
                val usuarioActualizado = api.updateUsuario(usuarioId, usuario)

                Log.d("PanelUsuario", "Usuario actualizado: $usuarioActualizado")

                _uiState.value = _uiState.value.copy(
                    usuario = usuarioActualizado,
                    isError = false,
                    errorMessage = ""
                )

            } catch (e: HttpException) {
                Log.e("PanelUsuario", "HttpException al actualizar: ${e.code()} - ${e.message()}")
                _uiState.value = _uiState.value.copy(
                    isError = true,
                    errorMessage = when (e.code()) {
                        404 -> "Usuario no encontrado"
                        400 -> "Datos inválidos"
                        401 -> "No autorizado"
                        500 -> "Error del servidor"
                        else -> "Error al actualizar usuario"
                    }
                )
            } catch (e: Exception) {
                Log.e("PanelUsuario", "Error al actualizar usuario: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isError = true,
                    errorMessage = "Error de conexión: ${e.message}"
                )
            }
        }
    }

    fun limpiarError() {
        _uiState.value = _uiState.value.copy(
            isError = false,
            errorMessage = ""
        )
    }
}