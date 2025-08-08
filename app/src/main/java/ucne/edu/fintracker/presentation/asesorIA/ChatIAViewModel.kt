package ucne.edu.fintracker.presentation.asesorIA

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.Chat
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
        modelName = BuildConfig.GEMINI_MODEL, // ✅ Versión más reciente
        apiKey = BuildConfig.apiKey,
        systemInstruction = content {
            text("""
                Eres un asesor financiero experto para una app móvil. 
                IMPORTANTE: Mantén todas las respuestas cortas y concisas (máximo 150 palabras).
                - Usa frases directas y puntos clave
                - Evita explicaciones largas o repetitivas
                - Proporciona consejos prácticos e inmediatos
                - Si necesitas dar más información, sugiere hacer preguntas específicas
            """.trimIndent())
        }
    )

    // ✅ Chat persistente que mantiene el contexto
    private var chat: Chat = generativeModel.startChat()

    fun sendPrompt(
        prompt: String,
        bitmap: Bitmap? = null
    ) {
        _uiState.value = ChatIaUiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = chat.sendMessage(
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

    // ✅ Función para reiniciar la conversación si es necesario
    fun clearChat() {
        chat = generativeModel.startChat()
    }

    // ✅ Función para obtener el historial si lo necesitas
    fun getChatHistory() = chat.history
}