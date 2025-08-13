package ucne.edu.fintracker.presentation.asesorIA

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.Chat
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import ucne.edu.fintracker.BuildConfig1
import ucne.edu.fintracker.remote.FinTrackerApi
import ucne.edu.fintracker.remote.LocalDateTimeAdapter
import ucne.edu.fintracker.remote.dto.TransaccionDto
import ucne.edu.fintracker.remote.dto.MetaAhorroDto
import java.text.NumberFormat
import java.util.*
import java.util.concurrent.TimeUnit
import org.threeten.bp.format.DateTimeFormatter
import ucne.edu.fintracker.BuildConfig
import ucne.edu.fintracker.remote.dto.UsuarioDto

class ChatIAViewModel : ViewModel() {

    private val _uiState: MutableStateFlow<ChatIaUiState> =
        MutableStateFlow(ChatIaUiState.Initial)
    val uiState: StateFlow<ChatIaUiState> =
        _uiState.asStateFlow()

    private val api: FinTrackerApi by lazy {
        val moshi = Moshi.Builder()
            .add(LocalDateTimeAdapter())
            .add(KotlinJsonAdapterFactory())
            .build()

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl("https://fintrackerapp.azurewebsites.net/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(FinTrackerApi::class.java)
    }

    private val generativeModel = GenerativeModel(
        modelName = BuildConfig1.GEMINI_MODEL,
        apiKey = BuildConfig.apiKey,
        systemInstruction = content {
            text("""
                Eres un asesor financiero experto para una app móvil llamada FinTracker. 
                IMPORTANTE: Mantén todas las respuestas cortas y concisas (máximo 200 palabras).
                - Usa frases directas y puntos clave
                - Evita explicaciones largas o repetitivas
                - Proporciona consejos prácticos e inmediatos basados en los datos reales del usuario
                - Si necesitas dar más información, sugiere hacer preguntas específicas
                - Usa emojis ocasionalmente para hacer las respuestas más amigables
                - Dirígete al usuario de manera personal y amigable
                - NUNCA menciones IDs técnicos del usuario
                - Siempre usa el nombre del usuario para dirigirte a él
                - Analiza los patrones de gasto y ahorro del usuario para dar consejos personalizados
            """.trimIndent())
        }
    )

    private var chat: Chat = generativeModel.startChat()
    private var usuarioInicializado = false

    fun inicializarConUsuario(usuarioId: Int) {
        if (!usuarioInicializado) {
            _uiState.value = ChatIaUiState.Loading

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val usuario = api.getUsuario(usuarioId)
                    val transacciones = api.getTransaccionesPorUsuario(usuarioId)
                    val metas = api.getMetaAhorrosPorUsuario(usuarioId)

                    val promptInicial = construirPromptCompleto(usuario, transacciones, metas)

                    val response = chat.sendMessage(content { text(promptInicial) })

                    if (response.text != null) {
                        _uiState.value = ChatIaUiState.Success(response.text!!)
                        usuarioInicializado = true
                    } else {
                        _uiState.value = ChatIaUiState.Error("Error en inicialización")
                    }

                } catch (e: Exception) {
                    _uiState.value = ChatIaUiState.Error("Error al cargar datos del usuario: ${e.localizedMessage}")
                }
            }
        }
    }

    private fun construirPromptCompleto(
        usuario: UsuarioDto,
        transacciones: List<TransaccionDto>,
        metas: List<MetaAhorroDto>
    ): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("es", "DO"))
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val fechaActual = org.threeten.bp.LocalDate.now().format(dateFormatter)

        val nombreCompleto = "${usuario.nombre} ${usuario.apellido}".trim()
        val nombreSimple = usuario.nombre

        val gastos = transacciones.filter { it.tipo.equals("Gasto", ignoreCase = true) }
        val ingresos = transacciones.filter { it.tipo.equals("Ingreso", ignoreCase = true) }

        val totalGastos = gastos.sumOf { it.monto }
        val totalIngresos = ingresos.sumOf { it.monto }
        val balanceNeto = totalIngresos - totalGastos
        val transaccionesRecientes = transacciones
            .sortedByDescending { it.fecha }
            .take(5)

        val metasActivas = metas.filter {
            val montoAhorrado = it.montoAhorrado ?: 0.0
            val montoObjetivo = it.montoObjetivo
            montoAhorrado < montoObjetivo
        }

        val metasCompletadas = metas.filter {
            val montoAhorrado = it.montoAhorrado ?: 0.0
            val montoObjetivo = it.montoObjetivo
            montoAhorrado >= montoObjetivo
        }

        return """
            PERFIL FINANCIERO COMPLETO DE $nombreCompleto
            Fecha del análisis: $fechaActual
            
            === INFORMACIÓN PERSONAL ===
            - Nombre: $nombreCompleto
            - Email: ${usuario.email}
            - Saldo Total Actual: ${formatter.format(usuario.saldoTotal)}
            - Divisa: ${if (usuario.divisa.isEmpty()) "RD$" else usuario.divisa}
            
            === ANÁLISIS DE TRANSACCIONES ===
            Total de transacciones registradas: ${transacciones.size}
            
            RESUMEN FINANCIERO:
            • Total de gastos: ${formatter.format(totalGastos)} (${gastos.size} transacciones)
            • Total de ingresos: ${formatter.format(totalIngresos)} (${ingresos.size} transacciones)
            • Balance neto: ${formatter.format(balanceNeto)}
            
            TRANSACCIONES RECIENTES:
            ${transaccionesRecientes.take(5).joinToString("\n") { t ->
            "• ${t.fecha.format(dateFormatter)}: ${if (t.tipo == "Gasto") "-" else "+"}${formatter.format(t.monto)} (${t.tipo})"
        }}
            
            === METAS DE AHORRO ===
            Total de metas: ${metas.size}
            • Metas activas: ${metasActivas.size}
            • Metas completadas: ${metasCompletadas.size}
            
            METAS ACTIVAS EN PROGRESO:
            ${metasActivas.take(4).joinToString("\n") { meta ->
            val montoAhorrado = meta.montoAhorrado ?: 0.0
            val montoObjetivo = meta.montoObjetivo
            val progreso = if (montoObjetivo > 0) (montoAhorrado / montoObjetivo * 100).toInt() else 0
            val fechaLimite = meta.fechaFinalizacion.format(dateFormatter)
            "• ${meta.nombreMeta}: ${formatter.format(montoAhorrado)} / ${formatter.format(montoObjetivo)} (${progreso}%) - Fecha límite: $fechaLimite"
        }}
            
           METAS COMPLETADAS:
            ${metasCompletadas.take(3).joinToString("\n") { meta ->
            "• ${meta.nombreMeta}: ✅ ${formatter.format(meta.montoObjetivo)} completada"
            
        }}
            
            === PATRONES FINANCIEROS DETECTADOS ===
            ${if (gastos.isNotEmpty()) {
            val gastoPromedio = totalGastos / gastos.size
            "• Gasto promedio por transacción: ${formatter.format(gastoPromedio)}"
        } else "• No hay gastos registrados"}
            
            ${if (ingresos.isNotEmpty()) {
            val ingresoPromedio = totalIngresos / ingresos.size
            "• Ingreso promedio por transacción: ${formatter.format(ingresoPromedio)}"
        } else "• No hay ingresos registrados"}
            
            • Ratio ahorro vs gasto: ${if (totalGastos > 0) String.format("%.1f", (balanceNeto / totalGastos) * 100) else "N/A"}%
            
            === INSTRUCCIONES PARA EL ASESOR ===
            1. SIEMPRE dirígete al usuario como "$nombreSimple"
            2. Usa esta información real para dar consejos específicos y personalizados
            3. Analiza sus patrones de gasto y progreso en metas
            4. Sugiere mejoras basadas en su comportamiento financiero actual
            5. Sé empático si tiene dificultades financieras o celebra sus logros
            6. Nunca menciones IDs técnicos o información sensible
            
            TAREA INICIAL:
            Saluda a $nombreSimple con un análisis breve de su situación financiera actual.
            Destaca algo positivo de sus finanzas y ofrece un consejo personalizado basado en sus datos reales.
            Pregúntale en qué área específica le gustaría que le ayudes hoy.
        """.trimIndent()
    }

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

                if (response.text != null) {
                    _uiState.value = ChatIaUiState.Success(response.text!!)
                } else {
                    _uiState.value = ChatIaUiState.Error("Respuesta vacía")
                }
            } catch (e: Exception) {
                _uiState.value = ChatIaUiState.Error(e.localizedMessage ?: "Error desconocido")
            }
        }
    }

    fun clearChat() {
        chat = generativeModel.startChat()
        usuarioInicializado = false
    }

    fun getChatHistory() = chat.history
}
