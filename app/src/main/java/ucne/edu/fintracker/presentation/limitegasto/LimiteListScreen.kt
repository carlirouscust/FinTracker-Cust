package ucne.edu.fintracker.presentation.limitegasto

import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun LimiteListScreen(
    viewModel: LimiteViewModel,
    onBackClick: () -> Unit,
    onAgregarLimiteClick: () -> Unit,
    onLimiteClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val categorias by viewModel.categorias.collectAsState()


    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Límites de Gasto",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAgregarLimiteClick,
                containerColor = Color(0xFF8BC34A)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar Límite",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                uiState.error != null -> {
                    Text(
                        text = uiState.error ?: "Error desconocido",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    if (uiState.limites.isEmpty()) {
                        Text(
                            text = "No hay límites de gasto.",
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(uiState.limites) { limite ->
                                val categoria = categorias.find { it.categoriaId == limite.categoriaId }
                                val nombreCategoria = categoria?.nombre ?: "Sin categoría"
                                val icono = categoria?.icono ?: "❓"
                                val colorFondo = try {
                                    Color(android.graphics.Color.parseColor(categoria?.colorFondo ?: "#8BC34A"))
                                } catch (e: Exception) {
                                    Color(0xFF8BC34A)
                                }

                                val gastado = limite.gastadoActual ?: 0.0
                                val porcentaje = ((gastado / limite.montoLimite) * 100)
                                    .coerceAtMost(100.0)

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onLimiteClick(limite.limiteGastoId) }
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .background(colorFondo, shape = MaterialTheme.shapes.small),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = icono,
                                                fontSize = 24.sp
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Column {
                                            Text(
                                                text = nombreCategoria,
                                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "Límite: RD$${limite.montoLimite}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Column(
                                        horizontalAlignment = Alignment.End,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        LinearProgressIndicator(
                                            progress = (porcentaje / 100f).toFloat(),
                                            modifier = Modifier
                                                .width(120.dp)
                                                .height(8.dp),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "${porcentaje.toInt()}%",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
