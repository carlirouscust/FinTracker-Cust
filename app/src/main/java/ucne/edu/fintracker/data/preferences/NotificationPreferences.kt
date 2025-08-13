package ucne.edu.fintracker.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.notificationDataStore: DataStore<Preferences> by preferencesDataStore(name = "notification_preferences")

class NotificationPreferences(private val context: Context) {

    companion object {
        private val TRANSACTION_ALERTS = booleanPreferencesKey("transaction_alerts")
        private val SPENDING_LIMIT_REMINDERS = booleanPreferencesKey("spending_limit_reminders")
        private val SAVINGS_GOAL_UPDATES = booleanPreferencesKey("savings_goal_updates")
        private val FINANCIAL_TIPS = booleanPreferencesKey("financial_tips")
        private val SAVINGS_SUGGESTIONS = booleanPreferencesKey("savings_suggestions")
    }

    // Update methods - refactored to eliminate duplication
    suspend fun updateTransactionAlerts(enabled: Boolean) =
        updatePreference(TRANSACTION_ALERTS, enabled)

    suspend fun updateSpendingLimitReminders(enabled: Boolean) =
        updatePreference(SPENDING_LIMIT_REMINDERS, enabled)

    suspend fun updateSavingsGoalUpdates(enabled: Boolean) =
        updatePreference(SAVINGS_GOAL_UPDATES, enabled)

    suspend fun updateFinancialTips(enabled: Boolean) =
        updatePreference(FINANCIAL_TIPS, enabled)

    suspend fun updateSavingsSuggestions(enabled: Boolean) =
        updatePreference(SAVINGS_SUGGESTIONS, enabled)

    // Flow properties - refactored to eliminate duplication
    val transactionAlertsFlow: Flow<Boolean> = getPreferenceFlow(TRANSACTION_ALERTS)
    val spendingLimitRemindersFlow: Flow<Boolean> = getPreferenceFlow(SPENDING_LIMIT_REMINDERS)
    val savingsGoalUpdatesFlow: Flow<Boolean> = getPreferenceFlow(SAVINGS_GOAL_UPDATES)
    val financialTipsFlow: Flow<Boolean> = getPreferenceFlow(FINANCIAL_TIPS)
    val savingsSuggestionsFlow: Flow<Boolean> = getPreferenceFlow(SAVINGS_SUGGESTIONS)

    // Private helper methods to eliminate code duplication
    private suspend fun updatePreference(key: Preferences.Key<Boolean>, value: Boolean) {
        context.notificationDataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    private fun getPreferenceFlow(key: Preferences.Key<Boolean>): Flow<Boolean> =
        context.notificationDataStore.data.map { preferences ->
            preferences[key] ?: false
        }
}