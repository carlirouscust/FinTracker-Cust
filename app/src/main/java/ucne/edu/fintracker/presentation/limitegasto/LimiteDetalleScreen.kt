package ucne.edu.fintracker.presentation.limitegasto

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ucne.edu.fintracker.presentation.remote.dto.LimiteGastoDto
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import ucne.edu.fintracker.presentation.gasto.GastoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LimiteDetalleScreen(
    limite: LimiteGastoDto,
    categoriaIcono: String,
    categoriaNombre: String,
    limiteViewModel: LimiteViewModel,
    onBackClick: () -> Unit,
    onEditarClick: () -> Unit,
    onEliminarClick: () -> Unit,
    onEliminarConfirmado: () -> Unit
) {
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }

    val transacciones by limiteViewModel.transacciones.collectAsState()

    val gastadoActual = remember(transacciones) {
        transacciones
            .filter { it.categoriaId == limite.categoriaId && it.tipo == "Gasto" }
            .sumOf { it.monto }
    }

    val porcentaje = ((gastadoActual / limite.montoLimite) * 100).coerceAtMost(100.0)
    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Límite") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LinearProgressIndicator(
                    progress = (porcentaje / 100f).toFloat(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp),
                    color = Color(0xFF8BC34A),
                    trackColor = Color(0xFFE0E0E0)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${porcentaje.toInt()}% consumido",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Divider()

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = categoriaIcono.takeIf { it.isNotBlank() } ?: "💵",
                    fontSize = 36.sp,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text(
                    text = categoriaNombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    color = Color.Black
                )
            }

            Divider()

            Text("Período", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
            Text(limite.periodo, fontSize = 16.sp, color = Color.Gray)

            Divider()

            Text("Monto Límite", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
            Text("RD$ ${limite.montoLimite}", fontSize = 16.sp, color = Color.Gray)

            Divider()

            Spacer(modifier = Modifier.weight(1f))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onEditarClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Editar", color = Color.White)
                }

                Button(
                    onClick = { mostrarDialogoEliminar = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Eliminar", color = Color.White)
                }
            }
        }
    }

    if (mostrarDialogoEliminar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminar = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Seguro que deseas eliminar este límite de gasto?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoEliminar = false
                        onEliminarConfirmado()
                    }
                ) {
                    Text("Eliminar", color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { mostrarDialogoEliminar = false }
                ) {
                    Text("Cancelar", color = Color.Black)
                }
            }
        )
    }
}
