package ucne.edu.fintracker.presentation.metaahorro

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ucne.edu.fintracker.data.local.repository.MetaRepository
import ucne.edu.fintracker.presentation.remote.Resource
import ucne.edu.fintracker.presentation.remote.dto.MetaAhorroDto
import javax.inject.Inject

@HiltViewModel
class MetaViewModel @Inject constructor(
    private val metaRepository: MetaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        MetaUiState(
            metas = emptyList(),
            isLoading = false,
            error = null,
            metaCreada = false
        )
    )
    val uiState: StateFlow<MetaUiState> = _uiState

    fun cargarMetas(usuarioId: Int) {
        viewModelScope.launch {
            metaRepository.getMetas(usuarioId).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                metas = result.data ?: emptyList(),
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

    fun crearMeta(metaDto: MetaAhorroDto) {
        Log.d("MetaVM", "Creando meta: $metaDto")
        viewModelScope.launch {
            metaRepository.createMeta(metaDto).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        result.data?.let { nuevaMeta ->
                            _uiState.update { current ->
                                current.copy(
                                    isLoading = false,
                                    metaCreada = true,
                                    metas = current.metas + nuevaMeta,
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

    fun actualizarMeta(id: Int, metaDto: MetaAhorroDto) {
        viewModelScope.launch {
            metaRepository.updateMeta(id, metaDto).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        val listaActualizada = _uiState.value.metas.map {
                            if (it.metaAhorroId == id) result.data ?: it else it
                        }
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                metas = listaActualizada,
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

    fun eliminarMeta(id: Int) {
        viewModelScope.launch {
            metaRepository.deleteMeta(id).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        val listaFiltrada = _uiState.value.metas.filter { it.metaAhorroId != id }
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                metas = listaFiltrada,
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
