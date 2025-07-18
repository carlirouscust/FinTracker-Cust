package ucne.edu.fintracker.presentation.gasto

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ucne.edu.fintracker.data.local.repository.TransaccionRepository
import ucne.edu.fintracker.presentation.remote.Resource
import ucne.edu.fintracker.presentation.remote.dto.TransaccionDto
import javax.inject.Inject

@HiltViewModel
class GastoViewModel @Inject constructor(
    private val transaccionRepository: TransaccionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        GastoUiState(
            transacciones = emptyList(),
            isLoading = false,
            error = null
        )
    )
    val uiState: StateFlow<GastoUiState> = _uiState

    fun cambiarFiltro(filtro: String) {
        _uiState.update { it.copy(filtro = filtro) }
    }

    fun cambiarTipo(tipo: String) {
        _uiState.update { it.copy(tipoSeleccionado = tipo) }
    }

    fun cargarTransacciones() {
        viewModelScope.launch {
            transaccionRepository.getTransacciones().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                transacciones = result.data ?: emptyList(),
                                error = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(isLoading = false, error = result.message ?: "Error desconocido")
                        }
                    }
                }
            }
        }
    }

    fun crearTransaccion(transaccionDto: TransaccionDto) {
        Log.d("GastoViewModel", "Intentando crear transacción: $transaccionDto")
        viewModelScope.launch {
            transaccionRepository.createTransaccion(transaccionDto).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        val listaActual = _uiState.value.transacciones.toMutableList()
                        result.data?.let { listaActual.add(it) }
                        _uiState.update { it.copy(isLoading = false, transacciones = listaActual) }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(isLoading = false, error = result.message ?: "Error desconocido")
                        }
                    }
                }
            }
        }
    }

    fun agregarTransaccionLocal(transaccion: TransaccionDto) {
        val listaActual = _uiState.value.transacciones.toMutableList()
        listaActual.add(transaccion)
        _uiState.update { it.copy(transacciones = listaActual) }
    }
}
