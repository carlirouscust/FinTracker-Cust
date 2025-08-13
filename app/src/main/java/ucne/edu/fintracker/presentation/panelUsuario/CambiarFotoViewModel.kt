package ucne.edu.fintracker.presentation.panelUsuario

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import ucne.edu.fintracker.repository.UsuarioRepository
import ucne.edu.fintracker.remote.DataSource
import ucne.edu.fintracker.remote.FinTrackerApi
import ucne.edu.fintracker.remote.Resource
import javax.inject.Inject

@HiltViewModel
class CambiarFotoViewModel @Inject constructor(
    private val api: FinTrackerApi,
    private val usuarioRepository: UsuarioRepository,
    private val dataSource: DataSource
) : ViewModel() {

    private val _uiState = MutableStateFlow(CambiarFotoUiState())
    val uiState: StateFlow<CambiarFotoUiState> = _uiState.asStateFlow()

    fun cargarDatosUsuario(usuarioId: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    isError = false,
                    errorMessage = ""
                )
            }

            try {
                val usuario = api.getUsuario(usuarioId)
                Log.d("CambiarFoto", "Usuario cargado: $usuario")

                _uiState.update {
                    it.copy(
                        usuarioId = usuarioId,
                        nombreUsuario = usuario.nombre,
                        apellidoUsuario = usuario.apellido,
                        fotoPerfilUrl = usuario.fotoPerfil,
                        isLoading = false,
                        isError = false,
                        errorMessage = ""
                    )
                }

            } catch (e: HttpException) {
                Log.e("CambiarFoto", "HttpException: ${e.code()} - ${e.message()}")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = when (e.code()) {
                            404 -> "Usuario no encontrado"
                            401 -> "No autorizado"
                            500 -> "Error del servidor"
                            else -> "Error al cargar los datos del usuario"
                        }
                    )
                }
            } catch (e: Exception) {
                Log.e("CambiarFoto", "Error al cargar usuario: ${e.message}")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = "Error de conexión: ${e.message}"
                    )
                }
            }
        }
    }

    fun seleccionarFotoDesdeGaleria(path: String?) {
        Log.d("CambiarFoto", "Foto seleccionada desde galería: $path")
        _uiState.update {
            it.copy(
                fotoPerfilPath = path,
                mostrarOpcionesFoto = false,
                fotoGuardadaExitosamente = false
            )
        }
    }

    fun seleccionarFotoDesdeCamara(path: String?) {
        Log.d("CambiarFoto", "Foto tomada desde cámara: $path")
        _uiState.update {
            it.copy(
                fotoPerfilPath = path,
                mostrarOpcionesFoto = false,
                fotoGuardadaExitosamente = false
            )
        }
    }

    fun mostrarOpcionesFoto() {
        _uiState.update {
            it.copy(mostrarOpcionesFoto = true)
        }
    }

    fun ocultarOpcionesFoto() {
        _uiState.update {
            it.copy(mostrarOpcionesFoto = false)
        }
    }

    fun guardarFotoPerfil() {
        val usuarioId = _uiState.value.usuarioId
        val fotoPath = _uiState.value.fotoPerfilPath

        if (usuarioId == null) {
            _uiState.update {
                it.copy(
                    isError = true,
                    errorMessage = "Error: ID de usuario no válido"
                )
            }
            return
        }

        if (fotoPath == null) {
            _uiState.update {
                it.copy(
                    isError = true,
                    errorMessage = "Por favor selecciona una foto antes de guardar"
                )
            }
            return
        }

        Log.d("CambiarFoto", "Iniciando actualización de foto para usuario ID=$usuarioId con path: $fotoPath")

        viewModelScope.launch {
            usuarioRepository.actualizarFotoPerfil(usuarioId, fotoPath).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        Log.d("CambiarFoto", "Guardando foto... [LOADING]")
                        _uiState.update {
                            it.copy(
                                isUploadingFoto = true,
                                isError = false,
                                errorMessage = ""
                            )
                        }
                    }
                    is Resource.Success -> {
                        Log.d("CambiarFoto", "Foto guardada exitosamente: ${result.data}")
                        _uiState.update {
                            it.copy(
                                isUploadingFoto = false,
                                fotoGuardadaExitosamente = true,
                                fotoPerfilUrl = result.data?.fotoPerfil ?: fotoPath,
                                isError = false,
                                errorMessage = ""
                            )
                        }
                    }
                    is Resource.Error -> {
                        Log.e("CambiarFoto", "Error al guardar foto: ${result.message}")
                        _uiState.update {
                            it.copy(
                                isUploadingFoto = false,
                                isError = true,
                                errorMessage = result.message ?: "Error al guardar la foto"
                            )
                        }
                    }
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

    fun limpiarEstadoExito() {
        _uiState.update {
            it.copy(fotoGuardadaExitosamente = false)
        }
    }
}