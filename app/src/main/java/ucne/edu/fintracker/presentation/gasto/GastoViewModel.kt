package ucne.edu.fintracker.presentation.gasto

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ucne.edu.fintracker.data.local.repository.CategoriaRepository
import ucne.edu.fintracker.data.local.repository.TransaccionRepository
import ucne.edu.fintracker.presentation.login.LoginViewModel
import ucne.edu.fintracker.presentation.remote.Resource
import ucne.edu.fintracker.presentation.remote.dto.CategoriaDto
import ucne.edu.fintracker.presentation.remote.dto.TransaccionDto
import javax.inject.Inject

@HiltViewModel
class GastoViewModel @Inject constructor(
    private val transaccionRepository: TransaccionRepository,
    private val categoriaRepository: CategoriaRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        GastoUiState(
            transacciones = emptyList(),
            isLoading = false,
            error = null
        )
    )
    val uiState: StateFlow<GastoUiState> = _uiState

    private val _categorias = MutableStateFlow<List<CategoriaDto>>(emptyList())
    val categorias: StateFlow<List<CategoriaDto>> = _categorias

    private var usuarioIdActual: Int? = null
    fun inicializar(usuarioId: Int) {
        if (usuarioId <= 0) {
            Log.e("GastoViewModel", "UsuarioId inválido en inicializar: $usuarioId")
            return
        }

        if (usuarioIdActual != usuarioId) {
            usuarioIdActual = usuarioId
            Log.d("GastoViewModel", "Inicializando para usuarioId: $usuarioId")
            fetchCategorias(usuarioId)
            cargarTransacciones(usuarioId)
        } else {
            Log.d("GastoViewModel", "Ya inicializado para usuarioId: $usuarioIdActual")
        }
    }



    fun fetchCategorias(usuarioId: Int? = usuarioIdActual) {
        usuarioId?.let {
            viewModelScope.launch {
                Log.d("GastoViewModel", "Cargando categorías para usuario $it")
                categoriaRepository.getCategorias(it).collect { result ->
                    when (result) {
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            Log.d("GastoViewModel", "Categorías cargadas: ${result.data?.size}")
                            _categorias.value = result.data ?: emptyList()
                        }
                        is Resource.Error -> {
                            Log.e("GastoViewModel", "Error cargando categorías: ${result.message}")
                            _categorias.value = emptyList()
                        }
                    }
                }
            }
        } ?: Log.e("GastoViewModel", "usuarioId es null en fetchCategorias")
    }

    fun cargarTransacciones(usuarioId: Int? = usuarioIdActual) {
        usuarioId?.let {
            viewModelScope.launch {
                transaccionRepository.getTransacciones(it).collect { result ->
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
                                it.copy(
                                    isLoading = false,
                                    error = result.message ?: "Error desconocido"
                                )
                            }
                        }
                    }
                }
            }
        } ?: Log.e("GastoViewModel", "usuarioId es null en cargarTransacciones")
    }

    fun cambiarFiltro(filtro: String) {
        _uiState.update { it.copy(filtro = filtro) }
    }

    fun cambiarTipo(tipo: String) {
        _uiState.update { it.copy(tipoSeleccionado = tipo) }
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
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                transacciones = listaActual
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

    fun agregarTransaccionLocal(transaccion: TransaccionDto) {
        val listaActual = _uiState.value.transacciones.toMutableList()
        listaActual.add(transaccion)
        _uiState.update { it.copy(transacciones = listaActual) }
    }
}
