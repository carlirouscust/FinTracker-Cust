package ucne.edu.fintracker.presentation.categoria

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ScrollState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriaScreen(
    viewModel: CategoriaViewModel,
    usuarioId: Int,
    tipo: String,
    onGuardar: (nombre: String, tipo: String, icono: String, color: String) -> Unit,
    onCancel: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = { CategoriaTopAppBar(tipo = tipo, onCancel = onCancel) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        CategoriaContent(
            state = state,
            viewModel = viewModel,
            usuarioId = usuarioId,
            scrollState = scrollState,
            innerPadding = innerPadding,
            onGuardar = onGuardar
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoriaTopAppBar(
    tipo: String,
    onCancel: () -> Unit
) {
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
}

@Composable
private fun CategoriaContent(
    state: CategoriaUiState,
    viewModel: CategoriaViewModel,
    usuarioId: Int,
    scrollState: ScrollState,
    innerPadding: PaddingValues,
    onGuardar: (nombre: String, tipo: String, icono: String, color: String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(innerPadding)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            CategoriaForm(state = state, viewModel = viewModel)
            CategoriaGuardarButton(
                state = state,
                usuarioId = usuarioId,
                onGuardar = onGuardar
            )
        }
    }
}

@Composable
private fun CategoriaForm(
    state: CategoriaUiState,
    viewModel: CategoriaViewModel
) {
    Column {
        CategoriaNameField(
            nombre = state.nombre,
            onNombreChange = viewModel::onNombreChange
        )

        Spacer(Modifier.height(16.dp))

        CategoriaIconSection(
            selectedIcon = state.icono,
            onIconChange = viewModel::onIconoChange
        )

        Spacer(Modifier.height(16.dp))

        CategoriaColorSection(
            selectedColor = state.colorFondo,
            onColorChange = viewModel::onColorChange
        )
    }
}

@Composable
private fun CategoriaNameField(
    nombre: String,
    onNombreChange: (String) -> Unit
) {
    OutlinedTextField(
        value = nombre,
        onValueChange = onNombreChange,
        label = { Text("Nombre") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
private fun CategoriaIconSection(
    selectedIcon: String,
    onIconChange: (String) -> Unit
) {
    var mostrarMasIconos by remember { mutableStateOf(false) }

    Text(
        "Elige un icono",
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
    )

    val iconos = getIconos(mostrarMasIconos)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconGrid(
            iconos = iconos,
            selectedIcon = selectedIcon,
            onIconChange = onIconChange
        )

        IconToggleButton(
            mostrarMas = mostrarMasIconos,
            onToggle = { mostrarMasIconos = !mostrarMasIconos }
        )
    }
}

@Composable
private fun IconGrid(
    iconos: List<String>,
    selectedIcon: String,
    onIconChange: (String) -> Unit
) {
    val iconosPorFila = 4

    iconos.chunked(iconosPorFila).forEach { fila ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            fila.forEach { icon ->
                IconButton(
                    icon = icon,
                    isSelected = selectedIcon == icon,
                    onIconChange = onIconChange
                )
            }
            repeat(iconosPorFila - fila.size) {
                Spacer(modifier = Modifier.size(56.dp))
            }
        }
    }
}

@Composable
private fun IconButton(
    icon: String,
    isSelected: Boolean,
    onIconChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                shape = RoundedCornerShape(8.dp)
            )
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onIconChange(icon) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = icon,
            fontSize = 24.sp
        )
    }
}

@Composable
private fun IconToggleButton(
    mostrarMas: Boolean,
    onToggle: () -> Unit
) {
    TextButton(
        onClick = onToggle,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(if (mostrarMas) "Mostrar menos iconos" else "➕ Más iconos")
    }
}

@Composable
private fun CategoriaColorSection(
    selectedColor: String,
    onColorChange: (String) -> Unit
) {
    var mostrarMasColores by remember { mutableStateOf(false) }

    Text(
        "Cambiar color de fondo",
        color = MaterialTheme.colorScheme.onBackground
    )

    val colores = getColores(mostrarMasColores)

    Row(
        modifier = Modifier
            .padding(top = 8.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        colores.forEach { hex ->
            ColorButton(
                hex = hex,
                isSelected = selectedColor == hex,
                onColorChange = onColorChange
            )
        }
    }

    TextButton(onClick = { mostrarMasColores = !mostrarMasColores }) {
        Text(if (mostrarMasColores) "−" else "➕")
    }
}

@Composable
private fun ColorButton(
    hex: String,
    isSelected: Boolean,
    onColorChange: (String) -> Unit
) {
    val color = Color(android.graphics.Color.parseColor(hex))

    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(color)
            .border(
                width = 3.dp,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                shape = CircleShape
            )
            .clickable { onColorChange(hex) }
    )
}

@Composable
private fun CategoriaGuardarButton(
    state: CategoriaUiState,
    usuarioId: Int,
    onGuardar: (nombre: String, tipo: String, icono: String, color: String) -> Unit
) {
    val context = LocalContext.current

    Button(
        onClick = {
            handleGuardarClick(
                state = state,
                usuarioId = usuarioId,
                context = context,
                onGuardar = onGuardar
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A))
    ) {
        Text("Guardar")
    }
}

private fun handleGuardarClick(
    state: CategoriaUiState,
    usuarioId: Int,
    context: Context,
    onGuardar: (nombre: String, tipo: String, icono: String, color: String) -> Unit
) {
    when {
        state.nombre.isBlank() -> {
            Toast.makeText(context, "Ingresa un nombre para la categoría", Toast.LENGTH_SHORT).show()
        }
        state.icono.isBlank() -> {
            Toast.makeText(context, "Selecciona un icono", Toast.LENGTH_SHORT).show()
        }
        state.colorFondo.isBlank() -> {
            Toast.makeText(context, "Selecciona un color", Toast.LENGTH_SHORT).show()
        }
        else -> {
            Log.d("CategoriaScreen", "UsuarioId al guardar: $usuarioId")
            onGuardar(state.nombre, state.tipo, state.icono, state.colorFondo)
        }
    }
}

private fun getIconos(mostrarMas: Boolean): List<String> {
    val iconosBase = listOf(
        "🏠", "🚗", "🍽️", "📱", "💡", "🛍️", "💳", "🛒",
        "❤️", "🎓", "🎮", "🎬", "🎉", "💼", "⛽"
    )
    val iconosExtra = listOf(
        "📺", "🎵", "🎧", "📷", "🧾", "🚌", "✈️", "🛏️",
        "👕", "🐶", "🏥", "🧼", "📚", "💻", "🍻", "🎁",
        "🏦", "📦", "🔧", "🪙", "📍", "🧃", "🪑", "📡", "🕹️", "🧳"
    )
    return if (mostrarMas) iconosBase + iconosExtra else iconosBase
}

private fun getColores(mostrarMas: Boolean): List<String> {
    val coloresBase = listOf("#FF3B30", "#007AFF", "#34C759", "#FFCC00", "#AF52DE", "#5AC8FA")
    val coloresExtra = listOf(
        "#FF9500", "#FF2D55", "#8E8E93", "#D1D1D6", "#FFD60A", "#1C1C1E",
        "#00C49A", "#A52A2A", "#00BFFF", "#8B008B", "#FFC0CB", "#FF1493"
    )
    return if (mostrarMas) coloresBase + coloresExtra else coloresBase
}