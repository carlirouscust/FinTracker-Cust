package ucne.edu.fintracker.presentation.panelUsuario

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun CambiarContrasenaScreen(
    usuarioId: Int,
    onBack: () -> Unit) {
    var contrasenaActual by remember { mutableStateOf("") }
    var nuevaContrasena by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }

    var showPasswordActual by remember { mutableStateOf(false) }
    var showNuevaPassword by remember { mutableStateOf(false) }
    var showConfirmarPassword by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(
            text = "Cambiar contraseña",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "La contraseña debe tener al menos 8 caracteres, incluyendo una mayúscula, una minúscula y un número.",
            color = Color.Gray,
            fontSize = 12.sp,
            lineHeight = 16.sp
        )

        // Contraseña actual
        TextField(
            value = contrasenaActual,
            onValueChange = { contrasenaActual = it },
            placeholder = { Text("Introduce tu contraseña actual", color = Color(0xFFBFC4C1)) },
            visualTransformation = if (showPasswordActual) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showPasswordActual = !showPasswordActual }) {
                    Icon(
                        imageVector = if (showPasswordActual) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF4F8F4),
                focusedContainerColor = Color(0xFFF4F8F4),
                disabledContainerColor = Color(0xFFF4F8F4),
                cursorColor = Color.Black,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                disabledTextColor = Color.Black
            )
        )

        // Nueva contraseña
        TextField(
            value = nuevaContrasena,
            onValueChange = { nuevaContrasena = it },
            placeholder = { Text("Introduce tu nueva contraseña", color = Color(0xFFBFC4C1)) },
            visualTransformation = if (showNuevaPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showNuevaPassword = !showNuevaPassword }) {
                    Icon(
                        imageVector = if (showNuevaPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF4F8F4),
                focusedContainerColor = Color(0xFFF4F8F4),
                disabledContainerColor = Color(0xFFF4F8F4),
                cursorColor = Color.Black,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                disabledTextColor = Color.Black
            )
        )

        // Confirmar contraseña
        TextField(
            value = confirmarContrasena,
            onValueChange = { confirmarContrasena = it },
            placeholder = { Text("Confirma tu nueva contraseña", color = Color(0xFFBFC4C1)) },
            visualTransformation = if (showConfirmarPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showConfirmarPassword = !showConfirmarPassword }) {
                    Icon(
                        imageVector = if (showConfirmarPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF4F8F4),
                focusedContainerColor = Color(0xFFF4F8F4),
                disabledContainerColor = Color(0xFFF4F8F4),
                cursorColor = Color.Black,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                disabledTextColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                // Navegar a la pantalla de cambiar contraseña con el usuarioId
                onBack()
              },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF85D844),
                contentColor = Color.White
            )
        ) {
            Text("Guardar cambios", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }

    }
}
