package ucne.edu.fintracker.presentation.metaahorro

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.threeten.bp.OffsetDateTime
import ucne.edu.fintracker.repository.MetaRepository
import ucne.edu.fintracker.remote.DataSource
import ucne.edu.fintracker.remote.Resource
import ucne.edu.fintracker.remote.dto.AhorroRegistro
import ucne.edu.fintracker.remote.dto.MetaAhorroDto
import javax.inject.Inject

@HiltViewModel
class MetaViewModel @Inject constructor(
    private val metaRepository: MetaRepository,
    private val dataSource: DataSource
) : ViewModel() {

    private var usuarioId: Int = 0

    companion object {
        private const val ERROR_DESCONOCIDO = "Error desconocido"
    }

    fun setUsuarioId(id: Int) {
        Log.d("MetaVM", "UsuarioId seteado a $id")
        usuarioId = id
    }

    private val _uiState = MutableStateFlow(
        MetaUiState(
            metas = emptyList(),
            isLoading = false,
            error = null,
            metaCreada = false,
            metaSeleccionada = null
        )
    )
    val uiState: StateFlow<MetaUiState> = _uiState

    fun cargarMetas(usuarioId: Int, metaId: Int? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val metas = dataSource.getMetaAhorrosPorUsuario(usuarioId)
                val metaSeleccionada = metaId?.let { id -> metas.find { it.metaAhorroId == id } }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        metas = metas,
                        metaSeleccionada = metaSeleccionada
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error: ${e.message}"
                    )
                }
            }
        }
    }

    fun obtenerMetas(metaId: Int): MetaAhorroDto? {
        return _uiState.value.metas.find { it.metaAhorroId == metaId }
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
                                error = result.message ?: ERROR_DESCONOCIDO
                            )
                        }
                    }
                }
            }
        }
    }

    fun actualizarMeta(id: Int, metaDto: MetaAhorroDto) {
        Log.d("MetaVM", "Iniciando actualización meta ID=$id con datos: $metaDto")
        viewModelScope.launch {
            metaRepository.updateMeta(id, metaDto.copy(usuarioId = usuarioId)).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        Log.d("MetaVM", "Actualizando meta... [LOADING]")
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        val currentMetaId = _uiState.value.metaSeleccionada?.metaAhorroId
                        cargarMetas(usuarioId, currentMetaId)

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        Log.e("MetaVM", "Error al actualizar meta: ${result.message}")
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

    fun actualizarMontoAhorrado(metaId: Int, montoNuevo: Double, fechaMonto: OffsetDateTime) {
        Log.d("MetaVM", "Actualizando monto ahorrado: metaId=$metaId, monto=$montoNuevo")

        val metaActual = obtenerMetas(metaId) ?: return
        val montoTotal = (metaActual.montoAhorrado ?: 0.0) + montoNuevo

        val nuevoAhorro = AhorroRegistro(
            monto = montoNuevo,
            fecha = fechaMonto
        )

        val nuevosAhorros = metaActual.ahorros + nuevoAhorro

        val metaActualizada = metaActual.copy(
            montoAhorrado = montoTotal,
            fechaMontoAhorrado = fechaMonto,
            ahorros = nuevosAhorros
        )

        Log.d("MetaVM", "Meta actualizada con ${nuevosAhorros.size} ahorros")

        _uiState.update { currentState ->
            val metasActualizadas = currentState.metas.map { meta ->
                if (meta.metaAhorroId == metaId) metaActualizada else meta
            }
            currentState.copy(
                metas = metasActualizadas,
                metaSeleccionada = if (currentState.metaSeleccionada?.metaAhorroId == metaId) {
                    metaActualizada
                } else currentState.metaSeleccionada
            )
        }

        actualizarMeta(metaId, metaActualizada)
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
                                error = result.message ?: ERROR_DESCONOCIDO
                            )
                        }
                    }
                }
            }
        }
    }
}