package ucne.edu.fintracker.presentation.ajustes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assistant
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun NavegacionInferior(navController: NavController, usuarioId: Int) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        listOf(
            Triple("gastos", Icons.Default.Home, "Home"),
            Triple("chatIA/$usuarioId", Icons.Default.Assistant, "IA Asesor"),
            Triple("metaahorros/$usuarioId", Icons.Default.Star, "Metas")
        ).forEach { (route, icon, label) ->
            NavigationBarItem(
                selected = currentRoute == route,
                onClick = {
                    navController.navigate(route) {
                        if (route.startsWith("metaahorros")) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                        }
                    }
                },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) }
            )
        }
    }
}

@Composable
fun NotificacionesScreen(navController: NavController, usuarioId: Int) {
    Scaffold(
        topBar = { MetaTopBar("Notificaciones") { navController.popBackStack() } },
        bottomBar = { NavegacionInferior(navController, usuarioId) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            NotificacionesSeccion("General", listOf(
                "Alertas de Transacciones" to "Recibe alertas sobre transacciones, incluyendo compras, depósitos y transferencias.",
                "Recordatorios de Límites de Gastos" to "Recibe recordatorios para revisar cuando te acercas a tus límites de gastos.",
                "Actualizaciones de Metas de Ahorro" to "Recibe actualizaciones sobre el progreso de tus metas de ahorro."
            ))

            Spacer(modifier = Modifier.height(24.dp))

            NotificacionesSeccion("Consejos y Sugerencias", listOf(
                "Consejos Financieros" to "Recibe consejos personalizados para mejorar tus hábitos financieros.",
                "Sugerencias de Ahorro" to "Recibe sugerencias para optimizar tus gastos y encontrar oportunidades de ahorro."
            ))
        }
    }
}

@Composable
fun NotificacionesSeccion(titulo: String, items: List<Pair<String, String>>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = titulo,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        items.forEach { (titulo, descripcion) ->
            ItemNotificacion(titulo, descripcion)
        }
    }
}

@Composable
fun ItemNotificacion(titulo: String, descripcion: String) {
    var isEnabled by remember { mutableStateOf(false) }
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
                onCheckedChange = { isEnabled = it },
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