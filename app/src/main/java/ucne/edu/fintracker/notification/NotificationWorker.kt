package ucne.edu.fintracker.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.first
import ucne.edu.fintracker.data.preferences.NotificationPreferences

class NotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        private val FINANCIAL_TIPS = listOf(
            "💡 Consejo: Revisa tus gastos semanalmente para mantener el control",
            "💰 Tip: Ahorra al menos el 20% de tus ingresos cada mes",
            "📊 Consejo: Categoriza tus gastos para identificar áreas de mejora",
            "🎯 Tip: Establece metas de ahorro realistas y alcanzables",
            "💳 Consejo: Evita usar tarjetas de crédito para gastos innecesarios"
        )

        private val SAVINGS_SUGGESTIONS = listOf(
            "💡 Sugerencia: Prepara comida en casa para ahorrar en restaurantes",
            "🚗 Tip: Considera usar transporte público para reducir gastos",
            "🛍️ Consejo: Compara precios antes de hacer compras grandes",
            "📱 Sugerencia: Revisa tus suscripciones y cancela las que no uses",
            "💡 Tip: Aprovecha ofertas y descuentos para productos esenciales"
        )

        private const val FINANCIAL_TIP_TITLE = "Consejo Financiero"
        private const val SAVINGS_SUGGESTION_TITLE = "Sugerencia de Ahorro"
    }

    override suspend fun doWork(): Result =
        try {
            processNotifications()
            Result.success()
        } catch (exception: Exception) {
            Result.failure()
        }

    private suspend fun processNotifications() {
        val preferences = NotificationPreferences(applicationContext)
        val notificationManager = NotificationManagerImpl(applicationContext)

        val (financialTipsEnabled, savingsSuggestionsEnabled) = getEnabledPreferences(preferences)

        if (financialTipsEnabled) {
            sendRandomNotification(notificationManager::showFinancialTip, FINANCIAL_TIP_TITLE, FINANCIAL_TIPS)
        }

        if (savingsSuggestionsEnabled) {
            sendRandomNotification(notificationManager::showFinancialTip, SAVINGS_SUGGESTION_TITLE, SAVINGS_SUGGESTIONS)
        }
    }

    private suspend fun getEnabledPreferences(preferences: NotificationPreferences): Pair<Boolean, Boolean> =
        Pair(
            preferences.financialTipsFlow.first(),
            preferences.savingsSuggestionsFlow.first()
        )

    private fun sendRandomNotification(
        notificationFunction: (String, String) -> Unit,
        title: String,
        messages: List<String>
    ) {
        val randomMessage = messages.random()
        notificationFunction(title, randomMessage)
    }
}