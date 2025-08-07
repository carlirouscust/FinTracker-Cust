package ucne.edu.fintracker.presentation.categoria

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriaScreen(
    navController: NavController,
    viewModel: CategoriaViewModel,
    usuarioId: Int,
    tipo: String,
    onGuardar: (nombre: String, tipo: String, icono: String, color: String) -> Unit,
    onCancel: () -> Unit
)
 {
    val state by viewModel.uiState.collectAsState()
     val scrollState = rememberScrollState()
     var mostrarMasIconos by remember { mutableStateOf(false) }
     var mostrarMasColores by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Crear categoría de ${tipo.lowercase().replaceFirstChar { it.uppercase() }}",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
                .verticalScroll(scrollState),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    OutlinedTextField(
                        value = state.nombre,
                        onValueChange = { viewModel.onNombreChange(it) },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Elige un icono",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )

                    val iconosBase = listOf(
                        "🏠","🚗","🍽️","📱","💡","🛍️","💳","🛒",
                        "❤️","🎓","🎮","🎬","🎉","💼"
                    )
                    val iconosExtra = listOf(
                        "📺","🎵","🎧","📷","🧾","🚌","✈️","🛏️",
                        "👕","🐶","🏥","🧼","📚","💻","🍻","🎁",
                        "🏦","📦","🔧","🪙","📍","🧃","🪑","📡","🕹️","🧳"
                    )
                    val iconos = if (mostrarMasIconos) iconosBase + iconosExtra else iconosBase

                    val iconosPorFila = 4

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        iconos.chunked(iconosPorFila).forEach { fila ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                fila.forEach { icon ->
                                    Box(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .border(
                                                width = 1.dp,
                                                color = if (state.icono == icon) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .background(
                                                if (state.icono == icon) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .clickable { viewModel.onIconoChange(icon) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = icon,
                                            fontSize = 24.sp
                                        )
                                    }
                                }
                                repeat(iconosPorFila - fila.size) {
                                    Spacer(modifier = Modifier.size(56.dp))
                                }
                            }
                        }
                        TextButton(
                            onClick = { mostrarMasIconos = !mostrarMasIconos },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(if (mostrarMasIconos) "Mostrar menos iconos" else "➕ Más iconos")
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Text("Cambiar color de fondo", color = MaterialTheme.colorScheme.onBackground)


                    val coloresBase = listOf("#FF3B30","#007AFF","#34C759","#FFCC00","#AF52DE","#5AC8FA")
                    val coloresExtra = listOf("#FF9500","#FF2D55","#8E8E93","#D1D1D6","#FFD60A","#1C1C1E","#00C49A","#A52A2A","#00BFFF","#8B008B","#FFC0CB", "#FF1493")
                    val colores = if (mostrarMasColores) coloresBase + coloresExtra else coloresBase

                    Row( modifier = Modifier
                        .padding(top = 8.dp)
                        .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        colores.forEach { hex ->
                            val color = Color(android.graphics.Color.parseColor(hex))
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = 3.dp,
                                        color = if (state.colorFondo == hex) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { viewModel.onColorChange(hex) }
                            )
                        }
                    }
                    TextButton(onClick = { mostrarMasColores = !mostrarMasColores }) {
                        Text(if (mostrarMasColores) "−" else "➕")
                    }
                }

                Button(
                    onClick = {
                        Log.d("CategoriaScreen", "UsuarioId al guardar: $usuarioId")
                        onGuardar(state.nombre, state.tipo, state.icono, state.colorFondo)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A))
                ) {
                    Text("Guardar")
                }

            }
        }
    }
}
