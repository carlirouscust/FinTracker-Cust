package ucne.edu.fintracker.presentation.ajustes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assistant
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Support
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

data class AjusteItem(
    val titulo: String,
    val subtitulo: String,
    val icono: ImageVector,
    val onClick: () -> Unit,
    val colorIcono: Color? = null
)

data class AjustesNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String,
    val onClick: () -> Unit = {}
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AjustesListScreen(
    navController: NavController,
    usuarioId: Int,
    onEditarPerfil: () -> Unit = {},
    onCambiarContrasena: () -> Unit = {},
    onCerrarSesion: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    onApariencia: () -> Unit = {},
    onCentroAyuda: () -> Unit = {},
    onSoporte: () -> Unit = {}
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { AjustesTopBar(onBackClick = { navController.popBackStack() }) },
        bottomBar = { AjustesBottomBar(navController, usuarioId) }
    ) { paddingValues ->
        AjustesContent(
            paddingValues = paddingValues,
            ajustesSections = createAjustesSections(
                onEditarPerfil = onEditarPerfil,
                onCambiarContrasena = onCambiarContrasena,
                onCerrarSesion = { showLogoutDialog = true },
                onNotificaciones = onNotificaciones,
                onApariencia = onApariencia,
                onCentroAyuda = onCentroAyuda,
                onSoporte = onSoporte
            )
        )
    }

    if (showLogoutDialog) {
        LogoutConfirmationDialog(
            onConfirm = {
                showLogoutDialog = false
                onCerrarSesion()
            },
            onDismiss = { showLogoutDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AjustesTopBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "Ajustes",
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
private fun AjustesBottomBar(navController: NavController, usuarioId: Int) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        val navItems = listOf(
            AjustesNavItem("gastos", Icons.Default.Home, "Home") {
                navController.navigate("gastos")
            },
            AjustesNavItem("chatIA", Icons.Default.Assistant, "IA Asesor") {
                navController.navigate("chatIA/$usuarioId")
            },
            AjustesNavItem("metaahorros/$usuarioId", Icons.Default.Star, "Metas") {
                navController.navigate("metaahorros/$usuarioId") {
                    launchSingleTop = true
                    restoreState = true
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                }
            }
        )

        navItems.forEach { navItem ->
            NavigationBarItem(
                selected = currentRoute == navItem.route,
                onClick = navItem.onClick,
                icon = { Icon(navItem.icon, contentDescription = navItem.label) },
                label = { Text(navItem.label) }
            )
        }
    }
}

@Composable
private fun AjustesContent(
    paddingValues: androidx.compose.foundation.layout.PaddingValues,
    ajustesSections: List<Pair<String, List<AjusteItem>>>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        ajustesSections.forEachIndexed { index, (titulo, items) ->
            if (index > 0) {
                Spacer(modifier = Modifier.height(24.dp))
            }

            AjustesSeccion(titulo = titulo) {
                items.forEach { item ->
                    ItemAjuste(
                        titulo = item.titulo,
                        subtitulo = item.subtitulo,
                        icono = item.icono,
                        onClick = item.onClick,
                        colorIcono = item.colorIcono ?: MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun LogoutConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Cerrar sesión",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Text(
                text = "¿Está seguro que quiere cerrar la sesión?",
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Sí, cerrar sesión", color = MaterialTheme.colorScheme.onError)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = MaterialTheme.colorScheme.primary)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp)
    )
}

private fun createAjustesSections(
    onEditarPerfil: () -> Unit,
    onCambiarContrasena: () -> Unit,
    onCerrarSesion: () -> Unit,
    onNotificaciones: () -> Unit,
    onApariencia: () -> Unit,
    onCentroAyuda: () -> Unit,
    onSoporte: () -> Unit
): List<Pair<String, List<AjusteItem>>> {
    return listOf(
        "Cuenta" to listOf(
            AjusteItem("Información del perfil", "Editar información personal", Icons.Default.Person, onEditarPerfil),
            AjusteItem("Contraseña", "Cambiar contraseña", Icons.Default.Lock, onCambiarContrasena),
            AjusteItem("Cerrar sesión", "Cerrar sesión de perfil", Icons.Default.ExitToApp, onCerrarSesion, Color.Red)
        ),
        "Preferencias de la aplicación" to listOf(
            AjusteItem("Notificaciones", "Gestionar notificaciones", Icons.Default.Notifications, onNotificaciones),
            AjusteItem("Apariencia", "Cambiar apariencia de la aplicación", Icons.Default.Palette, onApariencia)
        ),
        "Ayuda y soporte" to listOf(
            AjusteItem("Centro de ayuda", "Preguntas frecuentes", Icons.Default.Help, onCentroAyuda),
            AjusteItem("Soporte", "Contactar con soporte", Icons.Default.Support, onSoporte)
        )
    )
}

@Composable
fun AjustesSeccion(titulo: String, contenido: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = titulo,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        contenido()
    }
}

@Composable
fun ItemAjuste(
    titulo: String,
    subtitulo: String,
    icono: ImageVector,
    onClick: () -> Unit,
    colorIcono: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = colorIcono
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = titulo, color = colorIcono, fontWeight = FontWeight.SemiBold)
            Text(text = subtitulo, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        }
    }
}