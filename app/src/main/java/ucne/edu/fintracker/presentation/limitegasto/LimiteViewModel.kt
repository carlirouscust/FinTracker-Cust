package ucne.edu.fintracker.presentation.limitegasto

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ucne.edu.fintracker.data.local.repository.LimiteRepository
import ucne.edu.fintracker.data.local.repository.CategoriaRepository
import ucne.edu.fintracker.presentation.remote.Resource
import ucne.edu.fintracker.presentation.remote.dto.CategoriaDto
import ucne.edu.fintracker.presentation.remote.dto.LimiteGastoDto
import javax.inject.Inject

@HiltViewModel
class LimiteViewModel @Inject constructor(
    private val limiteRepository: LimiteRepository,
    private val categoriaRepository: CategoriaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        LimiteUiState(
            limites = emptyList(),
            isLoading = false,
            error = null
        )
    )
    val uiState: StateFlow<LimiteUiState> = _uiState



    private val _categorias = MutableStateFlow<List<CategoriaDto>>(emptyList())
    val categorias: StateFlow<List<CategoriaDto>> = _categorias

    init {
        cargarCategorias()
        cargarLimites()
    }

    fun cargarCategorias() {
        viewModelScope.launch {
            categoriaRepository.getCategorias().collect { result ->
                when (result) {
                    is Resource.Loading -> {  }
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

    fun cargarLimites() {
        viewModelScope.launch {
            limiteRepository.getLimites().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                limites = result.data ?: emptyList(),
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

    fun crearLimite(limiteDto: LimiteGastoDto) {
        Log.d("LimiteVM", "Creando límite: $limiteDto")
        viewModelScope.launch {
            limiteRepository.createLimite(limiteDto).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        result.data?.let { nuevoLimite ->
                            _uiState.update { current ->
                                current.copy(
                                    isLoading = false,
                                    limiteCreado = true,
                                    limites = current.limites + nuevoLimite,
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
                                error = result.message ?: "Error desconocido"
                            )
                        }
                    }
                }
            }
        }
    }

    fun actualizarLimite(id: Int, limiteDto: LimiteGastoDto) {
        viewModelScope.launch {
            limiteRepository.updateLimite(id, limiteDto).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        val listaActualizada = _uiState.value.limites.map {
                            if (it.limiteGastoId == id) result.data ?: it else it
                        }
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                limites = listaActualizada,
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

    fun eliminarLimite(id: Int) {
        viewModelScope.launch {
            limiteRepository.deleteLimite(id).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        val listaFiltrada = _uiState.value.limites.filter { it.limiteGastoId != id }
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                limites = listaFiltrada,
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


