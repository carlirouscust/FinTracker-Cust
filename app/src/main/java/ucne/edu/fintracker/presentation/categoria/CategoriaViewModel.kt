package ucne.edu.fintracker.presentation.categoria

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ucne.edu.fintracker.data.local.repository.CategoriaRepository
import ucne.edu.fintracker.presentation.remote.dto.CategoriaDto
import ucne.edu.fintracker.presentation.remote.Resource
import javax.inject.Inject

@HiltViewModel
class CategoriaViewModel @Inject constructor(
    private val repository: CategoriaRepository
) : ViewModel() {

    private val _usuarioId = MutableStateFlow<Int?>(null)

    private val _uiState = MutableStateFlow(CategoriaUiState())
    val uiState = _uiState.asStateFlow()

    val usuarioId: StateFlow<Int?> = _usuarioId

    // Control de usuarios inicializados - MEJORADO
    private val usuariosInicializados = mutableSetOf<Int>()
    private val inicializacionEnProceso = mutableSetOf<Int>()

    init {
        viewModelScope.launch {
            _usuarioId
                .filter { it != null && it != 0 }
                .collectLatest { id ->
                    // Solo inicializar si no se ha hecho antes y no está en proceso
                    if (!usuariosInicializados.contains(id!!) && !inicializacionEnProceso.contains(id)) {
                        inicializacionEnProceso.add(id)
                        try {
                            inicializarCategoriasPorDefecto(id)
                            usuariosInicializados.add(id)
                        } finally {
                            inicializacionEnProceso.remove(id)
                        }
                    }
                    fetchCategorias(id)
                }
        }
    }

    fun setUsuarioId(id: Int) {
        Log.d("CategoriaViewModel", "setUsuarioId: $id")
        if (id != 0) {
            _usuarioId.value = id
        }
    }

    // FUNCIÓN CORREGIDA - Evita duplicados
    private suspend fun inicializarCategoriasPorDefecto(usuarioId: Int) = coroutineScope {
        Log.d("CategoriaViewModel", "Inicializando categorías para usuario: $usuarioId")

        // Verificar doble que no se haya inicializado
        if (usuariosInicializados.contains(usuarioId)) {
            Log.d("CategoriaViewModel", "Usuario $usuarioId ya inicializado, saltando...")
            return@coroutineScope
        }

        try {
            // Obtener categorías existentes
            repository.getCategorias(usuarioId)
                .filter { it is Resource.Success }
                .firstOrNull()
                ?.let { result ->
                    val categoriasExistentes = (result as Resource.Success).data ?: emptyList()
                    Log.d("CategoriaViewModel", "Categorías existentes: ${categoriasExistentes.size}")

                    // Solo crear si no hay categorías para este usuario
                    if (categoriasExistentes.isEmpty()) {
                        val categoriasDefault = listOf(
                            CategoriaDto(nombre = "Gasolina", tipo = "Gasto", icono = "⛽", colorFondo = "FF9800", usuarioId = usuarioId),
                            CategoriaDto(nombre = "Casa", tipo = "Gasto", icono = "🏠", colorFondo = "4CAF50", usuarioId = usuarioId),
                            CategoriaDto(nombre = "Comida", tipo = "Gasto", icono = "🍽", colorFondo = "F44336", usuarioId = usuarioId),
                            CategoriaDto(nombre = "Viajes", tipo = "Gasto", icono = "✈", colorFondo = "2196F3", usuarioId = usuarioId),
                            CategoriaDto(nombre = "Celular", tipo = "Gasto", icono = "📱", colorFondo = "9C27B0", usuarioId = usuarioId),
                            CategoriaDto(nombre = "Salario", tipo = "Ingreso", icono = "💰", colorFondo = "4CAF50", usuarioId = usuarioId),
                            CategoriaDto(nombre = "Freelance", tipo = "Ingreso", icono = "💻", colorFondo = "2196F3", usuarioId = usuarioId)
                        )

                        Log.d("CategoriaViewModel", "Creando ${categoriasDefault.size} categorías por defecto")

                        // Crear categorías de forma secuencial para evitar conflictos
                        categoriasDefault.forEach { categoria ->
                            try {
                                repository.createCategoria(categoria).collect { createResult ->
                                    when (createResult) {
                                        is Resource.Success -> {
                                            Log.d("CategoriaViewModel", "Categoría creada: ${categoria.nombre}")
                                        }
                                        is Resource.Error -> {
                                            Log.e("CategoriaViewModel", "Error creando categoría ${categoria.nombre}: ${createResult.message}")
                                        }
                                        is Resource.Loading -> {
                                            // No hacer nada durante loading
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("CategoriaViewModel", "Excepción creando categoría ${categoria.nombre}: ${e.message}")
                            }
                        }
                    } else {
                        Log.d("CategoriaViewModel", "Usuario $usuarioId ya tiene categorías, no se crean por defecto")
                    }
                }
        } catch (e: Exception) {
            Log.e("CategoriaViewModel", "Error en inicializarCategoriasPorDefecto: ${e.message}")
        }
    }

    fun getCategoriasFiltradas(): List<CategoriaDto> {
        val state = _uiState.value
        return if (state.filtroTipo.isBlank()) {
            state.categorias
        } else {
            state.categorias.filter { it.tipo == state.filtroTipo }
        }
    }

    fun fetchCategorias(usuarioId: Int) {
        if (usuarioId == 0) {
            _uiState.update { it.copy(error = "Usuario inválido", isLoading = false) }
            return
        }

        viewModelScope.launch {
            repository.getCategorias(usuarioId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let { categorias ->
                            // Filtrar duplicados por nombre y tipo para el mismo usuario
                            val categoriasSinDuplicados = categorias
                                .distinctBy { "${it.nombre}-${it.tipo}-${it.usuarioId}" }

                            _uiState.update {
                                it.copy(
                                    categorias = categoriasSinDuplicados,
                                    isLoading = false,
                                    error = null
                                )
                            }
                            Log.d("CategoriaViewModel", "Categorías cargadas: ${categoriasSinDuplicados.size}")
                        } ?: run {
                            _uiState.update { it.copy(isLoading = false) }
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(error = result.message, isLoading = false) }
                        Log.e("CategoriaViewModel", "Error cargando categorías: ${result.message}")
                    }
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    fun saveCategoria(usuarioId: Int, onSuccess: () -> Unit) {
        if (usuarioId == 0) {
            _uiState.update { it.copy(error = "Usuario inválido, no se puede guardar") }
            return
        }

        val current = _uiState.value

        // Verificar que no exista una categoría con el mismo nombre y tipo
        val yaExiste = current.categorias.any {
            it.nombre.equals(current.nombre, ignoreCase = true) &&
                    it.tipo == current.tipo &&
                    it.usuarioId == usuarioId
        }

        if (yaExiste) {
            _uiState.update { it.copy(error = "Ya existe una categoría con ese nombre y tipo") }
            return
        }

        val nuevaCategoria = CategoriaDto(
            nombre = current.nombre,
            tipo = current.tipo,
            icono = current.icono,
            colorFondo = current.colorFondo,
            usuarioId = usuarioId
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.createCategoria(nuevaCategoria).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let { categoriaCreada ->
                            _uiState.update { state ->
                                state.copy(
                                    isLoading = false,
                                    categorias = state.categorias + categoriaCreada,
                                    nombre = "",
                                    tipo = "Gasto",
                                    icono = "",
                                    colorFondo = "FFFFFF",
                                    error = null
                                )
                            }
                        }
                        onSuccess()
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(error = result.message, isLoading = false) }
                    }
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    // Resto de funciones sin cambios...
    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTabIndex = index) }
    }

    fun categoriasFiltradas(): List<CategoriaDto> {
        val state = _uiState.value
        return state.categorias.filter { cat ->
            if (state.selectedTabIndex == 0) cat.tipo == "Gasto" else cat.tipo == "Ingreso"
        }
    }

    fun onNombreChange(value: String) {
        _uiState.update { it.copy(nombre = value) }
    }

    fun onTipoChange(value: String) {
        _uiState.update { it.copy(tipo = value) }
    }

    fun onFiltroTipoChange(filtro: String) {
        _uiState.update { it.copy(filtroTipo = filtro) }
    }

    fun inicializarSinFiltro() {
        _uiState.update { it.copy(filtroTipo = "") }
    }

    fun limpiarFiltro() {
        _uiState.update { it.copy(filtroTipo = "") }
    }

    fun onIconoChange(value: String) {
        _uiState.update { it.copy(icono = value) }
    }

    fun onColorChange(value: String) {
        _uiState.update { it.copy(colorFondo = value) }
    }
}