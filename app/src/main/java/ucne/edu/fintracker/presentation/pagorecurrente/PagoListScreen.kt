package ucne.edu.fintracker.presentation.pagorecurrente

import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import ucne.edu.fintracker.presentation.remote.dto.CategoriaDto

@Composable
fun PagoListScreen(
    viewModel: PagoViewModel,
    onAgregarPagoClick: () -> Unit,
    onBackClick: () -> Unit,
    categorias: List<CategoriaDto>,
    onPagoClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp),

                verticalAlignment = Alignment.CenterVertically
            ){
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.Black
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Pagos Recurrente",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAgregarPagoClick,
                containerColor = Color(0xFF8BC34A)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar Pago",
                    tint = Color.White
                )
            }
        }

    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.error ?: "Error desconocido",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
                    contentPadding = padding
                ) {
                    items(uiState.pagos) { pago ->

                        val categoria = categorias.find { it.categoriaId == pago.categoriaId }

                        val colorFondo = try {
                            Color(android.graphics.Color.parseColor(categoria?.colorFondo ?: "#EFEFEF"))
                        } catch (e: Exception) {
                            Color(0xFFEFEFEF)
                        }

                        var isActivo by remember { mutableStateOf(pago.activo) }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                            .clickable {
                            onPagoClick(pago.pagoRecurrenteId)
                        },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(colorFondo, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = categoria?.icono?.takeIf { it.isNotBlank() } ?: "💵",
                                    fontSize = 20.sp
                                )
                            }

                            Spacer(Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = categoria?.nombre ?: "Categoría desconocida",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "RD$ ${pago.monto} • ${pago.frecuencia}",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }

                            Switch(
                                checked = isActivo,
                                onCheckedChange = { checked ->
                                    isActivo = checked
                                    viewModel.actualizarPagoRecurrente(
                                        pago.pagoRecurrenteId,
                                        pago.copy(activo = checked)
                                    )
                                }
                            )
                        }

                        Divider()
                    }
                }
            }
        }
    }
}
