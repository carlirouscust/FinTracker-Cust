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
import ucne.edu.fintracker.remote.dto.CategoriaDto
import ucne.edu.fintracker.remote.dto.PagoRecurrenteDto

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
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { PagoTopBar(title = "Pagos Recurrente", onBackClick = onBackClick) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAgregarPagoClick,
                containerColor = Color(0xFF8BC34A)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Pago", tint = Color.White)
            }
        }
    ) { padding ->
        when {
            uiState.isLoading -> ScreenState(message = "Cargando...", isError = false, padding = padding)
            uiState.error != null -> ScreenState(message = uiState.error!!, isError = true, padding = padding)
            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = padding
            ) {
                items(uiState.pagos) { pago ->
                    val categoria = categorias.find { it.categoriaId == pago.categoriaId }
                    PagoItem(pago = pago, categoria = categoria, onPagoClick = onPagoClick) { checked ->
                        viewModel.actualizarPagoRecurrente(pago.pagoRecurrenteId, pago.copy(activo = checked))
                    }
                    Divider()
                }
            }
        }
    }
}

@Composable
fun PagoTopBar(title: String, onBackClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = MaterialTheme.colorScheme.onSurface)
        }
        Spacer(Modifier.weight(1f))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 24.sp),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.weight(1f))
    }
}

@Composable
fun ScreenState(message: String, isError: Boolean, padding: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(padding),
        contentAlignment = Alignment.Center
    ) {
        if (isError) {
            Text(message, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
        } else {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun PagoItem(
    pago: PagoRecurrenteDto,
    categoria: CategoriaDto?,
    onPagoClick: (Int) -> Unit,
    onActivoChange: (Boolean) -> Unit
) {
    val colorFondo = try {
        Color(android.graphics.Color.parseColor(categoria?.colorFondo ?: "#EFEFEF"))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.surfaceVariant
    }

    var isActivo by remember { mutableStateOf(pago.activo) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable { onPagoClick(pago.pagoRecurrenteId) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(colorFondo, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(categoria?.icono?.takeIf { it.isNotBlank() } ?: "💵", fontSize = 20.sp)
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(categoria?.nombre ?: "Categoría desconocida", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("RD$ ${pago.monto} • ${pago.frecuencia}", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Switch(
            checked = isActivo,
            onCheckedChange = { checked ->
                isActivo = checked
                onActivoChange(checked)
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = Color(0xFF8BC34A),
                uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}