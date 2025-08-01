package ucne.edu.fintracker.presentation.asesorIA

sealed interface ChatIaUiState {

    /**
     * Empty state when the screen is first shown
     */
    object Initial : ChatIaUiState

    /**
     * Still loading
     */
    object Loading : ChatIaUiState

    /**
     * Text has been generated
     */
    data class Success(val outputText: String) : ChatIaUiState

    /**
     * There was an error generating text
     */
    data class Error(val errorMessage: String) : ChatIaUiState
}