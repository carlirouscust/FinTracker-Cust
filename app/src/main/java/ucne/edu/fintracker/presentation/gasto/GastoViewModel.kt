package ucne.edu.fintracker.presentation.gasto

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.threeten.bp.DayOfWeek
import org.threeten.bp.OffsetDateTime
import ucne.edu.fintracker.repository.CategoriaRepository
import ucne.edu.fintracker.repository.TransaccionRepository
import androidx.compose.runtime.State
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import ucne.edu.fintracker.remote.Resource
import ucne.edu.fintracker.remote.dto.CategoriaDto
import ucne.edu.fintracker.remote.dto.TotalAnual
import ucne.edu.fintracker.remote.dto.TotalMes
import ucne.edu.fintracker.remote.dto.TransaccionDto
import ucne.edu.fintracker.presentation.utils.SaldoCalculatorUtil
import javax.inject.Inject

@HiltViewModel
class GastoViewModel @Inject constructor(
    private val transaccionRepository: TransaccionRepository,
    private val categoriaRepository: CategoriaRepository,
    private val saldoCalculator: SaldoCalculatorUtil
) : ViewModel() {
    companion object {
        private const val ERROR_DESCONOCIDO = "Error desconocido"
    }
    private val _filtroState = MutableStateFlow("Día")
    val filtroState: StateFlow<String> = _filtroState.asStateFlow()
    private val _fechaSeleccionadaState = MutableStateFlow(OffsetDateTime.now())
    private val fechaSeleccionadaState: StateFlow<OffsetDateTime> = _fechaSeleccionadaState.asStateFlow()

    private val _totalesMensuales = mutableStateOf<List<TotalMes>>(emptyList())
    val totalesMensuales: State<List<TotalMes>> = _totalesMensuales

    private val _totalesAnuales = mutableStateOf<List<TotalAnual>>(emptyList())
    val totalesAnuales: State<List<TotalAnual>> = _totalesAnuales
    private val _transacciones = MutableStateFlow<List<TransaccionDto>>(emptyList())
    val transacciones: StateFlow<List<TransaccionDto>> = _transacciones

    private val _uiState = MutableStateFlow(GastoUiState())
    val uiState: StateFlow<GastoUiState> = _uiState

    val transaccionesFiltradas: StateFlow<List<TransaccionDto>> = combine(
        _uiState,
        filtroState,
        fechaSeleccionadaState
    ) { uiState, filtro, fechaSeleccionada ->
        val transacciones = uiState.transacciones
        val (fechaInicio, fechaFin) = calcularRangoFechas(filtro, fechaSeleccionada)

        transacciones.filter { transaccion ->
            transaccion.fecha.isAfter(fechaInicio.minusNanos(1)) &&
                    transaccion.fecha.isBefore(fechaFin.plusNanos(1))
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private var usuarioIdActual: Int? = null

    private val _eventoEliminacion = MutableSharedFlow<Unit>()
    val eventoEliminacion = _eventoEliminacion.asSharedFlow()

    private val _categorias = MutableStateFlow<List<CategoriaDto>>(emptyList())
    val categorias: StateFlow<List<CategoriaDto>> = _categorias

    fun cambiarFiltro(filtro: String) {
        _uiState.update { it.copy(filtro = filtro) }
    }
    fun cambiarFecha(nuevaFecha: OffsetDateTime) {
        _fechaSeleccionadaState.value = nuevaFecha
    }

    fun cambiarTipo(tipo: String) {
        Log.d("GastoViewModel", "Tipo seleccionado cambiado a: $tipo")
        _uiState.update { it.copy(tipoSeleccionado = tipo) }
        usuarioIdActual?.let { cargarDatos(it) }
    }

    fun inicializar(usuarioId: Int) {
        if (usuarioId <= 0) return

        if (usuarioIdActual != usuarioId) {
            usuarioIdActual = usuarioId
            cargarTransacciones(usuarioId)
            viewModelScope.launch {
                transaccionRepository.syncTransacciones(usuarioId)
                cargarTransacciones(usuarioId)
            }
            cargarTodosDatos(usuarioId)
        }
    }
    private fun cargarTodosDatos(usuarioId: Int) {
        viewModelScope.launch {
            try {
                Log.d("GastoViewModel", "Cargando todos los datos...")

                val categoriasJob = launch { fetchCategorias(usuarioId) }
                val transaccionesJob = launch { cargarTransacciones(usuarioId) }
                val totalesJob = launch { cargarDatos(usuarioId) }

                categoriasJob.join()
                transaccionesJob.join()
                totalesJob.join()

                Log.d("GastoViewModel", "Todos los datos cargados")
            } catch (e: Exception) {
                Log.e("GastoViewModel", "Error cargando datos: ${e.message}", e)
            }
        }
    }

    fun cargarDatos(usuarioId: Int) {
            viewModelScope.launch {
                try {
                    val totalesMes = transaccionRepository.obtenerTotalesPorMes(usuarioId)
                    val totalesAno = transaccionRepository.obtenerTotalesPorAno(usuarioId)

                    _totalesMensuales.value = totalesMes
                    _totalesAnuales.value = totalesAno
                } catch (e: Exception) {
                    Log.e("GastoViewModel", "Error al cargar datos: ${e.message}", e)
                }
            }
    }

    fun fetchCategorias(usuarioId: Int? = usuarioIdActual) {
        usuarioId?.let {
            viewModelScope.launch {
                Log.d("GastoViewModel", "Cargando categorías para usuario $it")
                categoriaRepository.getCategorias(it).collect { result ->
                    when (result) {
                        is Resource.Loading -> {
                            Log.d("Repository", "Cargando recurso...")
                        }
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
                transaccionRepository.getTransacciones(usuarioId).collect { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _uiState.update { it.copy(isLoading = true, error = null) }
                        }

                        is Resource.Success -> {
                            val transacciones = result.data ?: emptyList()
                            Log.d("GastoViewModel", "Transacciones cargadas: ${transacciones.size}")
                            transacciones.forEach { transaccion ->
                                Log.d(
                                    "GastoViewModel",
                                    "ID=${transaccion.transaccionId} Tipo=${transaccion.tipo} Monto=${transaccion.monto}"
                                )
                            }

                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    transacciones = transacciones,
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
        } ?: Log.e("GastoViewModel", "usuarioId es null en cargarTransacciones")
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
                        result.data?.let { nuevaTransaccion ->
                            val listaActual = _uiState.value.transacciones.toMutableList()
                            listaActual.add(nuevaTransaccion)
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    transacciones = listaActual
                                )
                            }
                            usuarioIdActual?.let { userId -> cargarDatos(userId) }
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

    fun filtrarTransaccionesPorFecha(
        transacciones: List<TransaccionDto>, filtro: String
    ): List<TransaccionDto> {
        val hoy = OffsetDateTime.now()

        return when (filtro) {
            "Día" -> {
                val hoyLocalDate = hoy.toLocalDate()
                transacciones.filter {
                    it.fecha.toLocalDate().isEqual(hoyLocalDate)
                }
            }

            "Semana" -> {
                val lunes = hoy.toLocalDate().with(DayOfWeek.MONDAY)
                val domingo = hoy.toLocalDate().with(DayOfWeek.SUNDAY)
                transacciones.filter {
                    val fecha = it.fecha.toLocalDate()
                    !fecha.isBefore(lunes) && !fecha.isAfter(domingo)
                }
            }

            "Mes" -> {
                transacciones.filter {
                    val fecha = it.fecha.toLocalDate()
                    fecha.month == hoy.toLocalDate().month && fecha.year == hoy.toLocalDate().year
                }
            }

            "Año" -> {
                transacciones.filter {
                    it.fecha.toLocalDate().year == hoy.toLocalDate().year
                }
            }

            else -> transacciones
        }
    }

    fun obtenerTransaccionPorId(id: Int): TransaccionDto? {
        return _uiState.value.transacciones.find { it.transaccionId == id }
    }


    fun actualizarTransaccion(transaccionDto: TransaccionDto) {
        viewModelScope.launch {
            transaccionRepository.updateTransaccion(transaccionDto.transaccionId, transaccionDto)
                .collect { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _uiState.update { it.copy(isLoading = true, error = null) }
                        }

                        is Resource.Success -> {
                            val listaActual = _uiState.value.transacciones.toMutableList()
                            val index =
                                listaActual.indexOfFirst { it.transaccionId == transaccionDto.transaccionId }
                            if (index != -1) {
                                listaActual[index] = transaccionDto
                                _uiState.update {
                                    it.copy(
                                        isLoading = false,
                                        transacciones = listaActual,
                                        error = null
                                    )
                                }
                                usuarioIdActual?.let { userId -> cargarDatos(userId) }
                            }
                        }

                        is Resource.Error -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = result.message ?: "Error actualizando transacción"
                                )
                            }
                        }
                    }
                }
        }
    }


    fun eliminarTransaccion(id: Int) {
        viewModelScope.launch {
            val listaOriginal = _uiState.value.transacciones
            val listaFiltrada = listaOriginal.filter { it.transaccionId != id }

            _uiState.update {
                it.copy(
                    isLoading = true,
                    transacciones = listaFiltrada
                )
            }
            transaccionRepository.deleteTransaccion(id).collect { result ->
                when (result) {
                    is Resource.Loading -> {  Log.d("Repository", "Cargando recurso...") }

                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                transacciones = listaFiltrada,
                                error = null
                            )
                        }

                        _eventoEliminacion.emit(Unit)
                    }

                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                transacciones = listaOriginal,
                                error = result.message ?: ERROR_DESCONOCIDO
                            )
                        }
                    }
                }
            }
        }
    }
}