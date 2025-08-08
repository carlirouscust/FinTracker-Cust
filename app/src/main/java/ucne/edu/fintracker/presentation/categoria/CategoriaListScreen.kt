package ucne.edu.fintracker.presentation.categoria

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.ui.unit.sp
import ucne.edu.fintracker.presentation.remote.dto.CategoriaDto
import androidx.compose.material3.TextButton

@Composable
fun CategoriaListScreen(
    viewModel: CategoriaViewModel,
    usuarioId: Int,
    tipoFiltro: String = "",
    onBackClick: () -> Unit = {},
    onAgregarCategoriaClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(usuarioId) {
        viewModel.fetchCategorias(usuarioId)
        if (tipoFiltro.isBlank()) {
            viewModel.inicializarSinFiltro()
        }
    }
    LaunchedEffect(tipoFiltro) {
        if (tipoFiltro.isNotBlank()) {
            viewModel.onFiltroTipoChange(tipoFiltro)
        }
    }
    val categorias = viewModel.getCategoriasFiltradas()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        CategoriaTopBar(onBackClick)

        CategoriaFiltro(
            filtroActual = uiState.filtroTipo,
            onFiltroChange = { viewModel.onFiltroTipoChange(it) },
            onLimpiarFiltro = { viewModel.limpiarFiltro() }
        )

        CategoriaListaEmpty(
            categorias = categorias,
            filtroTipo = uiState.filtroTipo,
            categoriasTotales = uiState.categorias
        )

        CategoriaFab(
            filtroTipo = uiState.filtroTipo,
            onAgregarCategoriaClick = onAgregarCategoriaClick
        )
    }
}

@Composable
private fun CategoriaTopBar(onBackClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Categorías",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun CategoriaFiltro(
    filtroActual: String,
    onFiltroChange: (String) -> Unit,
    onLimpiarFiltro: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        FiltroButton("Gasto", filtroActual == "Gasto") { onFiltroChange("Gasto") }
        Spacer(modifier = Modifier.width(8.dp))
        FiltroButton("Ingreso", filtroActual == "Ingreso") { onFiltroChange("Ingreso") }
    }
    if (filtroActual.isNotBlank()) {
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onLimpiarFiltro, modifier = Modifier.fillMaxWidth()) {
            Text("Ver todas las categorías", color = Color(0xFF8BC34A))
        }
    }
    Spacer(modifier = Modifier.height(24.dp))
}

@Composable
private fun RowScope.FiltroButton(
    text: String,
    seleccionado: Boolean,
    onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = if (seleccionado)
            ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A))
        else
            ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.weight(1f)
    ) {
        Text(
            text,
            color = if (seleccionado)
                MaterialTheme.colorScheme.onPrimary
            else
                MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ColumnScope.CategoriaListaEmpty(
    categorias: List<CategoriaDto>,
    filtroTipo: String,
    categoriasTotales: List<CategoriaDto>
) {
    if (categorias.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = when {
                    categoriasTotales.isEmpty() -> "Sin categorías aún"
                    filtroTipo == "Gasto" -> "Sin categorías de gastos"
                    filtroTipo == "Ingreso" -> "Sin categorías de ingresos"
                    else -> "Sin categorías aún"
                },
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(categorias) { cat ->
                CategoriaBody(categoria = cat)
            }
        }
    }
}

@Composable
private fun CategoriaFab(
    filtroTipo: String,
    onAgregarCategoriaClick: (String) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd
    ) {
        FloatingActionButton(
            onClick = {
                onAgregarCategoriaClick(if (filtroTipo.isBlank()) "Gasto" else filtroTipo)
            },
            containerColor = Color(0xFF8BC34A),
            shape = RoundedCornerShape(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar", tint = MaterialTheme.colorScheme.onPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Agregar Categoría", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}


@Composable
fun CategoriaBody(
    categoria: CategoriaDto
) {
    val colorFondo = try {
        Color(android.graphics.Color.parseColor("#${categoria?.colorFondo?.removePrefix("#") ?: "CCCCCC"}"))
    } catch (e: Exception) {
        Color.Gray
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(colorFondo, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = categoria.icono,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = categoria.nombre,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
        )
    }
}

