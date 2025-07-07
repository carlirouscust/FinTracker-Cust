package ucne.edu.fintracker.presentation.gasto

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ucne.edu.fintracker.presentation.remote.dto.TransaccionDto
import javax.inject.Inject

@HiltViewModel
class GastoViewModel @Inject constructor() : ViewModel() {



    private val _uiState = MutableStateFlow(
        GastoUiState(
            transacciones = emptyList()
        )
    )

    val uiState: StateFlow<GastoUiState> = _uiState

    fun cambiarFiltro(filtro: String) {
        _uiState.value = _uiState.value.copy(filtro = filtro)
    }

    fun cambiarTipo(tipo: String) {
        _uiState.value = _uiState.value.copy(tipoSeleccionado = tipo)
    }

    fun agregarTransaccion(transaccion: TransaccionDto) {
        val listaActual = _uiState.value.transacciones.toMutableList()
        listaActual.add(transaccion)
        _uiState.value = _uiState.value.copy(transacciones = listaActual)
    }
}
