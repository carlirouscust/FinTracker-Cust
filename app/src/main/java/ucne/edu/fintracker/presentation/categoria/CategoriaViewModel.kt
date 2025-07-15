package ucne.edu.fintracker.presentation.categoria

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ucne.edu.fintracker.presentation.remote.dto.CategoriaDto

@HiltViewModel
class CategoriaViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(CategoriaUiState())
    val uiState = _uiState.asStateFlow()

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTabIndex = index) }
    }

    fun agregarCategoria(categoria: CategoriaDto) {
        _uiState.update { state ->
            state.copy(categorias = state.categorias + categoria)
        }
    }

    fun categoriasFiltradas(): List<CategoriaDto> {
        val state = _uiState.value
        return state.categorias.filter { cat ->
            if (state.selectedTabIndex == 0) cat.tipo == "Gasto" else cat.tipo == "Ingreso"
        }
    }
}