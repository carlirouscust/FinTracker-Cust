package ucne.edu.fintracker.presentation.ajustes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AjustesListScreen(
    onEditarPerfil: () -> Unit = {},
    onCambiarContrasena: () -> Unit = {},
    onCerrarSesion: () -> Unit = {},
    onNotificaciones: () -> Unit = {},
    onApariencia: () -> Unit = {},
    onCentroAyuda: () -> Unit = {},
    onSoporte: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Ajustes",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        AjustesSeccion(titulo = "Cuenta") {
            ItemAjuste("Información del perfil", "Editar información personal", Icons.Default.Person, onEditarPerfil)
            ItemAjuste("Contraseña", "Cambiar contraseña", Icons.Default.Lock, onCambiarContrasena)
            ItemAjuste("Cerrar sesión", "Cerrar sesión de perfil", Icons.Default.ExitToApp, onCerrarSesion, Color.Red)
        }

        Spacer(modifier = Modifier.height(24.dp))

        AjustesSeccion(titulo = "Preferencias de la aplicación") {
            ItemAjuste("Notificaciones", "Gestionar notificaciones", Icons.Default.Notifications, onNotificaciones)
            ItemAjuste("Apariencia", "Cambiar apariencia de la aplicación", Icons.Default.Palette, onApariencia)
        }

        Spacer(modifier = Modifier.height(24.dp))

        AjustesSeccion(titulo = "Ayuda y soporte") {
            ItemAjuste("Centro de ayuda", "Preguntas frecuentes", Icons.Default.Help, onCentroAyuda)
            ItemAjuste("Soporte", "Contactar con soporte", Icons.Default.Support, onSoporte)
        }
    }
}

@Composable
fun AjustesSeccion(titulo: String, contenido: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = titulo,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.Black,
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
    colorIcono: Color = Color(0xFF2F2F2F)
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
                .background(Color(0xFFF1F1F1), shape = CircleShape),
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
            Text(text = subtitulo, color = Color.Gray, fontSize = 13.sp)
        }
    }
}
