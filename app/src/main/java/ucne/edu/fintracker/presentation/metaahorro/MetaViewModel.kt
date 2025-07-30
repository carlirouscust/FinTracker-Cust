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

    private var usuarioId: Int = 0

    private val _uiState = MutableStateFlow(
        MetaUiState(
            metas = emptyList(),
            isLoading = false,
            error = null,
            metaCreada = false
        )
    )
    val uiState: StateFlow<MetaUiState> = _uiState

    fun cargarMetas(idUsuario: Int) {
        usuarioId = idUsuario
        Log.d("MetaVM", "usuarioId en ViewModel antes de crear: $usuarioId")
        viewModelScope.launch {
            metaRepository.getMetas(usuarioId).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        Log.d("MetaVM", "Cargando metas... [LOADING]")
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        Log.d("MetaVM", "Metas cargadas correctamente: ${result.data}")
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                metas = result.data ?: emptyList(),
                                error = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        Log.e("MetaVM", "Error al cargar metas: ${result.message}")
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

    fun crearMeta(metaDto: MetaAhorroDto, usuarioId: Int) {
        Log.d("MetaVM", "Intentando crear meta: $metaDto con usuarioId: $usuarioId")
        viewModelScope.launch {
            val metaParaEnviar = metaDto.copy(usuarioId = usuarioId)
            Log.d("MetaVM", "Meta a enviar al repositorio: $metaParaEnviar")

            metaRepository.createMeta(metaParaEnviar).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        Log.d("MetaVM", "Creando meta... [LOADING]")
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        result.data?.let { nuevaMeta ->
                            Log.d("MetaVM", "Meta creada exitosamente: $nuevaMeta")
                            _uiState.update { current ->
                                current.copy(
                                    isLoading = false,
                                    metaCreada = true,
                                    metas = current.metas + nuevaMeta,
                                    error = null
                                )
                            }
                        } ?: run {
                            Log.w("MetaVM", "La meta fue creada pero vino nula.")
                            _uiState.update { it.copy(isLoading = false) }
                        }
                    }
                    is Resource.Error -> {
                        Log.e("MetaVM", "Error al crear meta: ${result.message}")
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
        Log.d("MetaVM", "Actualizando meta con ID $id para usuarioId: $usuarioId")
        viewModelScope.launch {
            metaRepository.updateMeta(id, metaDto.copy(usuarioId = usuarioId)).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        Log.d("MetaVM", "Actualizando meta... [LOADING]")
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        Log.d("MetaVM", "Meta actualizada correctamente: ${result.data}")
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
                        Log.e("MetaVM", "Error al actualizar meta: ${result.message}")
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
