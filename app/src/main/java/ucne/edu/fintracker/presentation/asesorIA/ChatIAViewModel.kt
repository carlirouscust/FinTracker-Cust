package ucne.edu.fintracker.presentation.asesorIA

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ucne.edu.fintracker.BuildConfig

class ChatIAViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<ChatIaUiState> =
        MutableStateFlow(ChatIaUiState.Initial)
    val uiState: StateFlow<ChatIaUiState> =
        _uiState.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.apiKey
    )

    fun sendPrompt(
        prompt: String,
        bitmap: Bitmap? = null // ✅ Imagen opcional
    ) {
        _uiState.value = ChatIaUiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(
                    content {
                        text(prompt)
                        if (bitmap != null) {
                            image(bitmap)
                        }
                    }
                )
                response.text?.let { outputContent ->
                    _uiState.value = ChatIaUiState.Success(outputContent)
                } ?: run {
                    _uiState.value = ChatIaUiState.Error("Respuesta vacía")
                }
            } catch (e: Exception) {
                _uiState.value = ChatIaUiState.Error(e.localizedMessage ?: "Error desconocido")
            }
        }
    }

}