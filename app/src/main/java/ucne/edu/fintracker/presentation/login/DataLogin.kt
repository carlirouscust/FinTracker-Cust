package ucne.edu.fintracker.presentation.login

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

object DataLogin {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "usuario_prefs")

    private val USUARIO_ID = intPreferencesKey("usuario_id")

    suspend fun guardarUsuarioId(context: Context, id: Int) {
        context.dataStore.edit { prefs ->
            prefs[USUARIO_ID] = id
        }
    }

    suspend fun obtenerUsuarioId(context: Context): Int? {
        val prefs = context.dataStore.data.first()
        return prefs[USUARIO_ID]
    }

    suspend fun limpiarSesion(context: Context) {
        context.dataStore.edit { it.clear() }
    }
}