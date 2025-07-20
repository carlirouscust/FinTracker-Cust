package ucne.edu.fintracker.presentation.pagorecurrente

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ucne.edu.fintracker.data.local.repository.CategoriaRepository
import ucne.edu.fintracker.data.local.repository.PagoRepository
import ucne.edu.fintracker.presentation.remote.Resource
import ucne.edu.fintracker.presentation.remote.dto.CategoriaDto
import ucne.edu.fintracker.presentation.remote.dto.PagoRecurrenteDto
import javax.inject.Inject


@HiltViewModel
class PagoViewModel @Inject constructor(
    private val pagoRecurrenteRepository: PagoRepository,
    private val categoriaRepository: CategoriaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        PagoUiState(
            pagos = emptyList(),
            isLoading = false,
            error = null
        )
    )
    val uiState: StateFlow<PagoUiState> = _uiState

    private val _categorias = MutableStateFlow<List<CategoriaDto>>(emptyList())
    val categorias: StateFlow<List<CategoriaDto>> = _categorias

    fun cargarCategorias() {
        viewModelScope.launch {
            categoriaRepository.getCategorias().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        _categorias.value = result.data ?: emptyList()
                    }
                    is Resource.Error -> {
                        _categorias.value = emptyList()
                    }
                }
            }
        }
    }

    init {
        cargarCategorias()
        cargarPagosRecurrentes()
    }

    fun cargarPagosRecurrentes() {
        viewModelScope.launch {
            pagoRecurrenteRepository.getPagosRecurrentes().collect { result ->
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
                                error = result.message ?: "Error desconocido"
                            )
                        }
                    }
                }
            }
        }
    }

    fun crearPagoRecurrente(pagoRecurrenteDto: PagoRecurrenteDto) {
        Log.d("PagoRecurrenteVM", "Intentando crear pago recurrente: $pagoRecurrenteDto")
        viewModelScope.launch {
            pagoRecurrenteRepository.createPagoRecurrente(pagoRecurrenteDto).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                pagoCreado = true
                            )
                        }
                        cargarPagosRecurrentes()
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = result.message ?: "Error desconocido"
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
                                error = result.message ?: "Error desconocido"
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
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = result.message ?: "Error desconocido"
                            )
                        }
                    }
                }
            }
        }
    }
}
