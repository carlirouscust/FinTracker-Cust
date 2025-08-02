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
    onBackClick: () -> Unit,
    onEditarClick: () -> Unit,
    onEliminarClick: () -> Unit,
    onEliminarConfirmado: () -> Unit
) {
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }



    val porcentaje = ((limite.gastadoActual ?: 0.0) / limite.montoLimite * 100).coerceAtMost(100.0)
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Límite") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
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
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${porcentaje.toInt()}% consumido",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
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
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Divider()

            Text("Período", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground)
            Text(limite.periodo, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Divider()

            Text("Monto Límite", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground)
            Text("RD$ ${limite.montoLimite}", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Divider()

            Spacer(modifier = Modifier.weight(1f))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onEditarClick,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.onPrimary)
                    Spacer(Modifier.width(8.dp))
                    Text("Editar", color = MaterialTheme.colorScheme.onPrimary)
                }

                Button(
                    onClick = { mostrarDialogoEliminar = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.onError)
                    Spacer(Modifier.width(8.dp))
                    Text("Eliminar", color = MaterialTheme.colorScheme.onError)
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
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { mostrarDialogoEliminar = false }
                ) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.onSurface)
                }
            }
        )
    }
}
