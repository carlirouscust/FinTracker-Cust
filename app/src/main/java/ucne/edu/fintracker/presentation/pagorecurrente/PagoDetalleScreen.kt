package ucne.edu.fintracker.presentation.pagorecurrente

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.threeten.bp.format.DateTimeFormatter
import ucne.edu.fintracker.presentation.remote.dto.PagoRecurrenteDto
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PagoDetalleScreen(
    pagoId: Int,
    pago: PagoRecurrenteDto,
    categoriaIcono: String,
    categoriaNombre: String,
    onBackClick: () -> Unit,
    onEditarClick: () -> Unit,
    onEliminarClick: () -> Unit,
    onEliminarConfirmado: () -> Unit,
    navHostController: NavHostController,
    pagoViewModel: PagoViewModel
) {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        pagoViewModel.eventoEliminacion.collect {
            navHostController.navigate("pagos") {
                popUpTo("pagos") { inclusive = true }
            }
        }
    }


    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Pago") },
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
            Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icono y Nombre con Monto abajo
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = categoriaIcono.takeIf { it.isNotBlank() } ?: "💵",
                    fontSize = 32.sp,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Column {
                    Text(
                        text = categoriaNombre,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = Color.Black
                    )
                    Text(
                        text = "RD$ ${pago.monto}",
                        fontSize = 20.sp,
                        color = Color.Gray
                    )
                }
            }

            Divider()

            // Categoría
            Text("Categoría", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
            Text(categoriaNombre, color = Color.Black)

            Divider()

            // Frecuencia
            Text("Frecuencia", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
            Text(pago.frecuencia, color = Color.Black)

            Divider()

            // Fecha Inicio
            Text("Fecha de Inicio", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
            Text(pago.fechaInicio.format(formatter), color = Color.Black)

            Divider()

            // Fecha Fin (opcional)
            Text("Fecha de Finalización", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
            Text(pago.fechaFin?.format(formatter) ?: "-", color = Color.Black)

            Spacer(modifier = Modifier.weight(1f))

            // Botones Editar y Eliminar
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
            text = { Text("¿Seguro que deseas eliminar el pago recurrente?") },
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


