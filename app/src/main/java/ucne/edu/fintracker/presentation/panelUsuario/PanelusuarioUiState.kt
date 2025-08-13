package ucne.edu.fintracker.presentation.panelUsuario

import ucne.edu.fintracker.remote.dto.UsuarioDto

data class PanelUsuarioUiState(
    val isLoading: Boolean = true,
    val isUpdatingSaldo: Boolean = false,
    val usuario: UsuarioDto? = null,
    val isError: Boolean = false,
    val errorMessage: String = ""
)