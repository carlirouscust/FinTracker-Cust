package ucne.edu.fintracker.presentation.panelUsuario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ucne.edu.fintracker.remote.DataSource
import javax.inject.Inject

@HiltViewModel
class DivisasViewModel @Inject constructor(
    private val dataSource: DataSource
) : ViewModel() {

    private val _uiState = MutableStateFlow(DivisasUiState())
    val uiState: StateFlow<DivisasUiState> = _uiState.asStateFlow()

    fun inicializar(usuarioId: Int) {
        cargarDivisaActual(usuarioId)
    }

    private fun cargarDivisaActual(usuarioId: Int) {
        viewModelScope.launch {
            try {
                val usuario = dataSource.getUsuario(usuarioId)
                _uiState.update {
                    it.copy(divisaSeleccionada = usuario.divisa.ifEmpty { "DOP" })
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Error al cargar divisa actual: ${e.message}")
                }
            }
        }
    }

    fun seleccionarDivisa(codigoDivisa: String) {
        _uiState.update {
            it.copy(divisaSeleccionada = codigoDivisa)
        }
    }

    fun guardarDivisa(usuarioId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val usuarioActual = dataSource.getUsuario(usuarioId)

                val usuarioActualizado = usuarioActual.copy(
                    divisa = _uiState.value.divisaSeleccionada
                )

                try {
                    val resultado = dataSource.updateUsuario(usuarioId, usuarioActualizado)

                    _uiState.update {
                        it.copy(isLoading = false)
                    }

                    onSuccess()
                } catch (updateException: Exception) {
                    android.util.Log.e("DivisasVM", "Error en updateUsuario: ${updateException.message}")

                    try {
                        _uiState.update {
                            it.copy(isLoading = false)
                        }

                        onSuccess()

                    } catch (e: Exception) {
                        throw e
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("DivisasVM", "Error general: ${e.message}")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al guardar divisa: ${e.message}"
                    )
                }
            }
        }
    }
}