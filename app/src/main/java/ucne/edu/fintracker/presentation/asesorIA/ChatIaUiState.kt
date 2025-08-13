package ucne.edu.fintracker.presentation.asesorIA

sealed interface ChatIaUiState {


    object Initial : ChatIaUiState
    object Loading : ChatIaUiState
    data class Success(val outputText: String) : ChatIaUiState
    data class Error(val errorMessage: String) : ChatIaUiState
}