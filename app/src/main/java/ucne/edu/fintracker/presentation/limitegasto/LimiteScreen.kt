package ucne.edu.fintracker.presentation.limitegasto

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ucne.edu.fintracker.remote.dto.CategoriaDto
import ucne.edu.fintracker.remote.dto.LimiteGastoDto

data class TextFieldConfig(
    val value: String,
    val onValueChange: (String) -> Unit,
    val label: String,
    val keyboardType: KeyboardType = KeyboardType.Text,
    val readOnly: Boolean = false
)

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String = ""
)

private class LimiteState(
    categoriaSeleccionada: CategoriaDto?,
    montoLimite: String,
    periodoSeleccionado: String
) {
    var categoriaSeleccionada by mutableStateOf(categoriaSeleccionada)
    var montoLimite by mutableStateOf(montoLimite)
    var periodoSeleccionado by mutableStateOf(periodoSeleccionado)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LimiteScreen(
    viewModel: LimiteViewModel,
    usuarioId: Int,
    limiteParaEditar: LimiteGastoDto? = null,
    onGuardar: (Double, Int, String, usuarioId: Int) -> Unit,
    onCancel: () -> Unit
) {
    val categorias by viewModel.categorias.collectAsState()
    val context = LocalContext.current

    val categoriasGasto = remember(categorias) {
        categorias.filter { it.tipo.equals("Gasto", ignoreCase = true) }
    }

    val limiteState = remember {
        LimiteState(
            categoriaSeleccionada = null,
            montoLimite = limiteParaEditar?.montoLimite?.toString() ?: "",
            periodoSeleccionado = limiteParaEditar?.periodo ?: ""
        )
    }

    LaunchedEffect(categoriasGasto, limiteParaEditar) {
        if (limiteParaEditar != null && categoriasGasto.isNotEmpty()) {
            limiteState.categoriaSeleccionada = categoriasGasto.find {
                it.categoriaId == limiteParaEditar.categoriaId
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            LimiteTopBar(
                esEdicion = limiteParaEditar != null,
                onCancel = onCancel
            )
        },
        bottomBar = {
            LimiteSaveButton(
                limiteState = limiteState,
                context = context,
                limiteParaEditar = limiteParaEditar,
                usuarioId = usuarioId,
                onGuardar = onGuardar
            )
        }
    ) { padding ->
        LimiteContent(
            limiteState = limiteState,
            categoriasGasto = categoriasGasto,
            padding = padding
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LimiteTopBar(
    esEdicion: Boolean,
    onCancel: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = if (esEdicion) "Editar Límite" else "Nuevo Límite",
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
private fun LimiteSaveButton(
    limiteState: LimiteState,
    context: Context,
    limiteParaEditar: LimiteGastoDto?,
    usuarioId: Int,
    onGuardar: (Double, Int, String, Int) -> Unit
) {
    Button(
        onClick = {
            handleLimiteSave(
                limiteState = limiteState,
                context = context,
                limiteParaEditar = limiteParaEditar,
                usuarioId = usuarioId,
                onGuardar = onGuardar
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A))
    ) {
        Text(if (limiteParaEditar == null) "Agregar Límite" else "Guardar Cambios")
    }
}

@Composable
private fun LimiteContent(
    limiteState: LimiteState,
    categoriasGasto: List<CategoriaDto>,
    padding: PaddingValues
) {
    var expandedCategoria by remember { mutableStateOf(false) }
    var expandedPeriodo by remember { mutableStateOf(false) }
    val periodos = listOf("Diario", "Semanal", "Quincenal", "Mensual", "Anual")

    Column(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        LimiteCategoriaDropdown(
            categorias = categoriasGasto,
            categoriaSeleccionada = limiteState.categoriaSeleccionada,
            onCategoriaSeleccionada = { limiteState.categoriaSeleccionada = it },
            expanded = expandedCategoria,
            onExpandedChange = { expandedCategoria = it }
        )

        LimiteTextField(
            config = TextFieldConfig(
                value = limiteState.montoLimite,
                onValueChange = { limiteState.montoLimite = it },
                label = "Monto del Límite",
                keyboardType = KeyboardType.Number
            )
        )

        LimitePeriodoDropdown(
            periodos = periodos,
            periodoSeleccionado = limiteState.periodoSeleccionado,
            onPeriodoSeleccionado = { limiteState.periodoSeleccionado = it },
            expanded = expandedPeriodo,
            onExpandedChange = { expandedPeriodo = it }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LimiteCategoriaDropdown(
    categorias: List<CategoriaDto>,
    categoriaSeleccionada: CategoriaDto?,
    onCategoriaSeleccionada: (CategoriaDto) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange
    ) {
        OutlinedTextField(
            value = categoriaSeleccionada?.nombre ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Categoría (Solo Gastos)") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = getLimiteTextFieldColors()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            categorias.forEach { categoria ->
                DropdownMenuItem(
                    text = { Text(categoria.nombre) },
                    onClick = {
                        onCategoriaSeleccionada(categoria)
                        onExpandedChange(false)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LimitePeriodoDropdown(
    periodos: List<String>,
    periodoSeleccionado: String,
    onPeriodoSeleccionado: (String) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange
    ) {
        OutlinedTextField(
            value = periodoSeleccionado,
            onValueChange = {},
            readOnly = true,
            label = { Text("Período") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = getLimiteTextFieldColors()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            periodos.forEach { periodo ->
                DropdownMenuItem(
                    text = { Text(periodo) },
                    onClick = {
                        onPeriodoSeleccionado(periodo)
                        onExpandedChange(false)
                    }
                )
            }
        }
    }
}

@Composable
private fun LimiteTextField(
    config: TextFieldConfig
) {
    OutlinedTextField(
        value = config.value,
        onValueChange = config.onValueChange,
        label = { Text(config.label) },
        keyboardOptions = KeyboardOptions(keyboardType = config.keyboardType),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = getLimiteTextFieldColors(),
        readOnly = config.readOnly
    )
}

private fun validateLimiteData(limiteState: LimiteState): ValidationResult {
    return when {
        limiteState.categoriaSeleccionada == null ->
            ValidationResult(false, "Selecciona una categoría")
        limiteState.montoLimite.isBlank() ->
            ValidationResult(false, "Ingresa el monto del límite")
        limiteState.montoLimite.toDoubleOrNull() == null || limiteState.montoLimite.toDoubleOrNull()!! <= 0 ->
            ValidationResult(false, "El monto debe ser un número válido mayor que cero")
        limiteState.periodoSeleccionado.isBlank() ->
            ValidationResult(false, "Selecciona un período")
        else -> ValidationResult(true)
    }
}

private fun handleLimiteSave(
    limiteState: LimiteState,
    context: Context,
    limiteParaEditar: LimiteGastoDto?,
    usuarioId: Int,
    onGuardar: (Double, Int, String, Int) -> Unit
) {
    val validacion = validateLimiteData(limiteState)

    if (!validacion.isValid) {
        Toast.makeText(context, validacion.errorMessage, Toast.LENGTH_SHORT).show()
        return
    }

    val monto = limiteState.montoLimite.toDoubleOrNull() ?: 0.0
    val categoriaId = limiteState.categoriaSeleccionada!!.categoriaId

    onGuardar(monto, categoriaId, limiteState.periodoSeleccionado, usuarioId)

    if (limiteParaEditar == null) {
        clearLimiteForm(limiteState)
    }
}

private fun clearLimiteForm(limiteState: LimiteState) {
    limiteState.montoLimite = ""
    limiteState.categoriaSeleccionada = null
    limiteState.periodoSeleccionado = ""
}

@Composable
private fun getLimiteTextFieldColors(): TextFieldColors {
    return OutlinedTextFieldDefaults.colors(
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        cursorColor = MaterialTheme.colorScheme.primary
    )
}