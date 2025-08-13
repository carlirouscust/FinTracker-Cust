package ucne.edu.fintracker.presentation.pagorecurrente

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ucne.edu.fintracker.repository.CategoriaRepository
import ucne.edu.fintracker.repository.PagoRepository
import ucne.edu.fintracker.remote.Resource
import ucne.edu.fintracker.remote.dto.CategoriaDto
import ucne.edu.fintracker.remote.dto.PagoRecurrenteDto
import javax.inject.Inject

@HiltViewModel
class PagoViewModel @Inject constructor(
    private val pagoRecurrenteRepository: PagoRepository,
    private val categoriaRepository: CategoriaRepository
) : ViewModel() {

    companion object {
        private const val ERROR_DESCONOCIDO = "Error desconocido"
    }

    private val _uiState = MutableStateFlow(
        PagoUiState(
            pagos = emptyList(),
            isLoading = false,
            error = null
        )
    )
    val uiState: StateFlow<PagoUiState> = _uiState
    private var usuarioIdActual: Int? = null

    private val _eventoEliminacion = MutableSharedFlow<Unit>()
    val eventoEliminacion = _eventoEliminacion.asSharedFlow()

    private val _categorias = MutableStateFlow<List<CategoriaDto>>(emptyList())
    val categorias: StateFlow<List<CategoriaDto>> = _categorias

    fun inicializar(usuarioId: Int) {
        if (usuarioId <= 0) return
        if (usuarioIdActual != usuarioId) {
            usuarioIdActual = usuarioId

            fetchCategorias(usuarioId)

            cargarPagosRecurrentes(usuarioId)

            viewModelScope.launch {
                pagoRecurrenteRepository.syncPagosRecurrentes(usuarioId)
                cargarPagosRecurrentes(usuarioId)
            }
        }
    }


    fun fetchCategorias(usuarioId: Int) {
        viewModelScope.launch {
            Log.d("LimiteViewModel", "Cargando categorías para usuario $usuarioId")
            categoriaRepository.getCategorias(usuarioId).collect { result ->
                when (result) {
                    is Resource.Loading -> {

                    }
                    is Resource.Success -> {
                        Log.d("LimiteViewModel", "Categorías cargadas: ${result.data?.size}")
                        _categorias.value = result.data ?: emptyList()
                    }
                    is Resource.Error -> {
                        Log.e("LimiteViewModel", "Error cargando categorías: ${result.message}")
                        _categorias.value = emptyList()
                    }
                }
            }
        }
    }

    fun cargarPagosRecurrentes(usuarioId: Int) {
        viewModelScope.launch {
            pagoRecurrenteRepository.getPagosRecurrentes(usuarioId).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                pagos = result.data ?: emptyList(),
                                error = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = result.message ?: ERROR_DESCONOCIDO
                            )
                        }
                    }
                }
            }
        }
    }

    fun crearPagoRecurrente(pagoRecurrenteDto: PagoRecurrenteDto) {
        Log.d("PagoRecurrenteVM", "Creando pago recurrente: $pagoRecurrenteDto")
        viewModelScope.launch {
            pagoRecurrenteRepository.createPagoRecurrente(pagoRecurrenteDto).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        result.data?.let { nuevoPago ->
                            _uiState.update { current ->
                                current.copy(
                                    isLoading = false,
                                    pagoCreado = true,
                                    pagos = current.pagos + nuevoPago,
                                    error = null
                                )
                            }
                        } ?: run {
                            _uiState.update { it.copy(isLoading = false) }
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = result.message ?: ERROR_DESCONOCIDO
                            )
                        }
                    }
                }
            }
        }
    }

    fun actualizarPagoRecurrente(id: Int, pagoRecurrenteDto: PagoRecurrenteDto) {
        viewModelScope.launch {
            pagoRecurrenteRepository.updatePagoRecurrente(id, pagoRecurrenteDto).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        val listaActualizada = _uiState.value.pagos.map {
                            if (it.pagoRecurrenteId == id) result.data ?: it else it
                        }
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                pagos = listaActualizada,
                                error = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = result.message ?: ERROR_DESCONOCIDO
                            )
                        }
                    }
                }
            }
        }
    }

    fun eliminarPagoRecurrente(id: Int) {
        viewModelScope.launch {
            pagoRecurrenteRepository.deletePagoRecurrente(id).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        val listaFiltrada = _uiState.value.pagos.filter { it.pagoRecurrenteId != id }
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                pagos = listaFiltrada,
                                error = null
                            )
                        }
                        _eventoEliminacion.emit(Unit)
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = result.message ?: ERROR_DESCONOCIDO
                            )
                        }
                    }
                }
            }
        }
    }

}
