package ucne.edu.fintracker.presentation.panelUsuario

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun CambiarContrasenaScreen(
    usuarioId: Int,
    onBack: () -> Unit,
    viewModel: CambiarContrasenaViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Log.d("UsuarioId", "request: $usuarioId")


    var contrasenaActual by remember { mutableStateOf("") }
    var nuevaContrasena by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }

    var showPasswordActual by remember { mutableStateOf(false) }
    var showNuevaPassword by remember { mutableStateOf(false) }
    var showConfirmarPassword by remember { mutableStateOf(false) }

    // Validación simple para activar botón
    val isFormValid = nuevaContrasena.length >= 8 &&
            nuevaContrasena == confirmarContrasena &&
            contrasenaActual.isNotBlank()

    // Si cambio fue exitoso, regresa atrás automáticamente
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onBack()
            viewModel.limpiarMensajes()
        }
    }

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

        // Mostrar mensajes de error o éxito
        if (uiState.isError) {
            Text(
                text = uiState.errorMessage,
                color = Color.Red,
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (uiState.isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        Button(
            onClick = {
                viewModel.cambiarContrasena(usuarioId, contrasenaActual, nuevaContrasena)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isFormValid) Color(0xFF85D844) else Color.Gray,
                contentColor = Color.White
            ),
            enabled = isFormValid && !uiState.isLoading
        ) {
            Text("Guardar cambios", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}
