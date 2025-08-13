@file:OptIn(ExperimentalPermissionsApi::class)

package ucne.edu.fintracker.presentation.ajustes

import android.Manifest
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assistant
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import ucne.edu.fintracker.data.preferences.NotificationPreferences
import ucne.edu.fintracker.notification.NotificationManagerImpl
import ucne.edu.fintracker.notification.NotificationScheduler
import java.util.concurrent.TimeUnit

@Composable
fun NavegacionInferior(navController: NavController, usuarioId: Int) {
    val navigationItems = createNavigationItems(usuarioId)
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        navigationItems.forEach { (route, icon, label) ->
            NavigationBarItem(
                selected = currentRoute == route,
                onClick = { navigateToRoute(navController, route) },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificacionesTopBar(title: String, onBackClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Atrás",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun NotificacionesScreen(navController: NavController, usuarioId: Int) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dependencies = rememberNotificationDependencies(context)

    val notificationStates = rememberNotificationStates()

    val notificationPermissionState = rememberNotificationPermissionState()

    LoadNotificationPreferences(dependencies.preferences, notificationStates)

    HandleNotificationScheduling(
        dependencies.scheduler,
        notificationStates.financialTips.value,
        notificationStates.savingsSuggestions.value
    )

    Scaffold(
        topBar = { NotificacionesTopBar("Notificaciones") { navController.popBackStack() } },
        bottomBar = { NavegacionInferior(navController, usuarioId) }
    ) { paddingValues ->
        NotificationsContent(
            paddingValues = paddingValues,
            notificationPermissionState = notificationPermissionState,
            notificationStates = notificationStates,
            onPreferenceChange = { updateType, enabled ->
                updateNotificationPreference(updateType, enabled, dependencies.preferences, notificationStates, scope)
            },
            onTestNotification = { testType ->
                handleTestNotification(testType, dependencies.notificationManager, context)
            }
        )
    }
}

@Composable
private fun NotificationsContent(
    paddingValues: PaddingValues,
    notificationPermissionState: Any?,
    notificationStates: NotificationStates,
    onPreferenceChange: (PreferenceType, Boolean) -> Unit,
    onTestNotification: (TestNotificationType) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        PermissionAlert(notificationPermissionState)
        NotificationSections(notificationStates, onPreferenceChange)
        TestingSection(notificationStates, onTestNotification)
    }
}

@Composable
private fun PermissionAlert(notificationPermissionState: Any?) {
    val permissionState = notificationPermissionState as? com.google.accompanist.permissions.PermissionState

    if (permissionState != null && permissionState.status.isGranted == false) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Permisos de notificación requeridos",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Text(
                    text = "Para recibir notificaciones, necesitas otorgar permisos.",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Button(
                    onClick = { permissionState.launchPermissionRequest() },
                    modifier = Modifier.padding(top = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Otorgar permisos")
                }
            }
        }
    }
}

@Composable
private fun NotificationSections(
    states: NotificationStates,
    onPreferenceChange: (PreferenceType, Boolean) -> Unit
) {
    val generalItems = createGeneralNotificationItems(states, onPreferenceChange)
    val tipsItems = createTipsNotificationItems(states, onPreferenceChange)

    NotificacionesSeccion("General", generalItems)
    Spacer(modifier = Modifier.height(24.dp))
    NotificacionesSeccion("Consejos y Sugerencias", tipsItems)
}

@Composable
private fun TestingSection(
    notificationStates: NotificationStates,
    onTestNotification: (TestNotificationType) -> Unit
) {
    Spacer(modifier = Modifier.height(24.dp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            TestingSectionHeader()
            TestingButtons(notificationStates, onTestNotification)
        }
    }
}

@Composable
private fun TestingSectionHeader() {
    Text(
        text = "🧪 Zona de Pruebas",
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onPrimaryContainer
    )
    Text(
        text = "Solo puedes probar notificaciones que estén activadas arriba",
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
    )
}

@Composable
private fun TestingButtons(
    notificationStates: NotificationStates,
    onTestNotification: (TestNotificationType) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TestButton(
                text = "Consejo",
                modifier = Modifier.weight(1f),
                enabled = notificationStates.financialTips.value,
                onClick = { onTestNotification(TestNotificationType.TIP) }
            )
            TestButton(
                text = "Transacción",
                modifier = Modifier.weight(1f),
                enabled = notificationStates.transactionAlerts.value,
                onClick = { onTestNotification(TestNotificationType.TRANSACTION) }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TestButton(
                text = "Meta",
                modifier = Modifier.weight(1f),
                enabled = notificationStates.savingsGoalUpdates.value,
                onClick = { onTestNotification(TestNotificationType.GOAL) }
            )
            TestButton(
                text = "10s Work",
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                fontSize = 12.sp,
                enabled = notificationStates.financialTips.value || notificationStates.savingsSuggestions.value,
                onClick = { onTestNotification(TestNotificationType.SCHEDULED) }
            )
        }
    }
}

@Composable
private fun TestButton(
    text: String,
    modifier: Modifier,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    fontSize: androidx.compose.ui.unit.TextUnit = 14.sp,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = if (enabled) colors else ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        enabled = enabled
    ) {
        Text(
            text = if (enabled) text else "$text (Desactivado)",
            fontSize = if (enabled) fontSize else (fontSize.value - 1).sp
        )
    }
}

data class NotificationItem(
    val title: String,
    val description: String,
    val isEnabled: Boolean,
    val onToggle: (Boolean) -> Unit
)

data class NotificationStates(
    val transactionAlerts: MutableState<Boolean>,
    val spendingLimitReminders: MutableState<Boolean>,
    val savingsGoalUpdates: MutableState<Boolean>,
    val financialTips: MutableState<Boolean>,
    val savingsSuggestions: MutableState<Boolean>
)

data class NotificationDependencies(
    val preferences: NotificationPreferences,
    val scheduler: NotificationScheduler,
    val notificationManager: NotificationManagerImpl
)

enum class PreferenceType {
    TRANSACTION_ALERTS, SPENDING_LIMIT_REMINDERS, SAVINGS_GOAL_UPDATES, FINANCIAL_TIPS, SAVINGS_SUGGESTIONS
}

enum class TestNotificationType {
    TIP, TRANSACTION, GOAL, SCHEDULED
}

@Composable
private fun rememberNotificationDependencies(context: android.content.Context) = remember {
    NotificationDependencies(
        preferences = NotificationPreferences(context),
        scheduler = NotificationScheduler(context),
        notificationManager = NotificationManagerImpl(context)
    )
}

@Composable
private fun rememberNotificationStates() = remember {
    NotificationStates(
        transactionAlerts = mutableStateOf(false),
        spendingLimitReminders = mutableStateOf(false),
        savingsGoalUpdates = mutableStateOf(false),
        financialTips = mutableStateOf(false),
        savingsSuggestions = mutableStateOf(false)
    )
}

@Composable
private fun rememberNotificationPermissionState() =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    } else null

@Composable
private fun LoadNotificationPreferences(
    preferences: NotificationPreferences,
    states: NotificationStates
) {
    LaunchedEffect(Unit) {
        launch { preferences.transactionAlertsFlow.collect { states.transactionAlerts.value = it } }
        launch { preferences.spendingLimitRemindersFlow.collect { states.spendingLimitReminders.value = it } }
        launch { preferences.savingsGoalUpdatesFlow.collect { states.savingsGoalUpdates.value = it } }
        launch { preferences.financialTipsFlow.collect { states.financialTips.value = it } }
        launch { preferences.savingsSuggestionsFlow.collect { states.savingsSuggestions.value = it } }
    }
}

@Composable
private fun HandleNotificationScheduling(
    scheduler: NotificationScheduler,
    financialTips: Boolean,
    savingsSuggestions: Boolean
) {
    LaunchedEffect(financialTips, savingsSuggestions) {
        if (financialTips || savingsSuggestions) {
            scheduler.schedulePeriodicNotifications()
        } else {
            scheduler.cancelPeriodicNotifications()
        }
    }
}

private fun createNavigationItems(usuarioId: Int) = listOf(
    Triple("gastos", Icons.Default.Home, "Home"),
    Triple("chatIA/$usuarioId", Icons.Default.Assistant, "IA Asesor"),
    Triple("metaahorros/$usuarioId", Icons.Default.Star, "Metas")
)

private fun navigateToRoute(navController: NavController, route: String) {
    navController.navigate(route) {
        if (route.startsWith("metaahorros")) {
            launchSingleTop = true
            restoreState = true
            popUpTo(navController.graph.startDestinationId) { saveState = true }
        }
    }
}

private fun createGeneralNotificationItems(
    states: NotificationStates,
    onPreferenceChange: (PreferenceType, Boolean) -> Unit
) = listOf(
    NotificationItem(
        "Alertas de Transacciones",
        "Recibe alertas sobre transacciones, incluyendo compras, depósitos y transferencias.",
        states.transactionAlerts.value
    ) { onPreferenceChange(PreferenceType.TRANSACTION_ALERTS, it) },
    NotificationItem(
        "Recordatorios de Límites de Gastos",
        "Recibe recordatorios para revisar cuando te acercas a tus límites de gastos.",
        states.spendingLimitReminders.value
    ) { onPreferenceChange(PreferenceType.SPENDING_LIMIT_REMINDERS, it) },
    NotificationItem(
        "Actualizaciones de Metas de Ahorro",
        "Recibe actualizaciones sobre el progreso de tus metas de ahorro.",
        states.savingsGoalUpdates.value
    ) { onPreferenceChange(PreferenceType.SAVINGS_GOAL_UPDATES, it) }
)

private fun createTipsNotificationItems(
    states: NotificationStates,
    onPreferenceChange: (PreferenceType, Boolean) -> Unit
) = listOf(
    NotificationItem(
        "Consejos Financieros",
        "Recibe consejos personalizados para mejorar tus hábitos financieros.",
        states.financialTips.value
    ) { onPreferenceChange(PreferenceType.FINANCIAL_TIPS, it) },
    NotificationItem(
        "Sugerencias de Ahorro",
        "Recibe sugerencias para optimizar tus gastos y encontrar oportunidades de ahorro.",
        states.savingsSuggestions.value
    ) { onPreferenceChange(PreferenceType.SAVINGS_SUGGESTIONS, it) }
)

private fun updateNotificationPreference(
    preferenceType: PreferenceType,
    enabled: Boolean,
    preferences: NotificationPreferences,
    states: NotificationStates,
    scope: CoroutineScope
) {
    when (preferenceType) {
        PreferenceType.TRANSACTION_ALERTS -> {
            states.transactionAlerts.value = enabled
            scope.launch { preferences.updateTransactionAlerts(enabled) }
        }
        PreferenceType.SPENDING_LIMIT_REMINDERS -> {
            states.spendingLimitReminders.value = enabled
            scope.launch { preferences.updateSpendingLimitReminders(enabled) }
        }
        PreferenceType.SAVINGS_GOAL_UPDATES -> {
            states.savingsGoalUpdates.value = enabled
            scope.launch { preferences.updateSavingsGoalUpdates(enabled) }
        }
        PreferenceType.FINANCIAL_TIPS -> {
            states.financialTips.value = enabled
            scope.launch { preferences.updateFinancialTips(enabled) }
        }
        PreferenceType.SAVINGS_SUGGESTIONS -> {
            states.savingsSuggestions.value = enabled
            scope.launch { preferences.updateSavingsSuggestions(enabled) }
        }
    }
}

private fun handleTestNotification(
    testType: TestNotificationType,
    notificationManager: NotificationManagerImpl,
    context: android.content.Context,
) {
    when (testType) {
        TestNotificationType.TIP -> notificationManager.showFinancialTip(
            "💡 Consejo de Prueba",
            "¡Esta es una notificación de prueba! El sistema funciona correctamente."
        )
        TestNotificationType.TRANSACTION -> notificationManager.showTransactionAlert(
            "💰 Transacción de Prueba",
            "Nueva transacción registrada: $100.00"
        )
        TestNotificationType.GOAL -> notificationManager.showGoalUpdate(
            "🎯 Meta de Prueba",
            "¡Has alcanzado el 75% de tu meta de ahorro!"
        )
        TestNotificationType.SCHEDULED -> {
            androidx.work.WorkManager.getInstance(context).enqueue(
                androidx.work.OneTimeWorkRequestBuilder<ucne.edu.fintracker.notification.NotificationWorker>()
                    .setInitialDelay(10, TimeUnit.SECONDS)
                    .build()
            )
        }
    }
}

@Composable
fun NotificacionesSeccion(titulo: String, items: List<NotificationItem>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = titulo,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        items.forEach { item ->
            ItemNotificacion(
                titulo = item.title,
                descripcion = item.description,
                isEnabled = item.isEnabled,
                onToggle = item.onToggle
            )
        }
    }
}

@Composable
fun ItemNotificacion(
    titulo: String,
    descripcion: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = titulo,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = isEnabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                    checkedTrackColor = Color(0xFF8BC34A),
                    uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = descripcion,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
            lineHeight = 16.sp
        )
    }
}