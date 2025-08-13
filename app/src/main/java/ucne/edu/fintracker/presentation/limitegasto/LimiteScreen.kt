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
import ucne.edu.fintracker.presentation.remote.dto.CategoriaDto
import ucne.edu.fintracker.presentation.remote.dto.LimiteGastoDto


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

    val estadoLimite = remember {
        EstadoLimite(
            categoriaSeleccionada = null,
            montoLimite = limiteParaEditar?.montoLimite?.toString() ?: "",
            periodoSeleccionado = limiteParaEditar?.periodo ?: ""
        )
    }

    LaunchedEffect(categoriasGasto, limiteParaEditar) {
        if (limiteParaEditar != null && categoriasGasto.isNotEmpty()) {
            estadoLimite.categoriaSeleccionada = categoriasGasto.find {
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
            BotonGuardarLimite(
                estadoLimite = estadoLimite,
                context = context,
                limiteParaEditar = limiteParaEditar,
                usuarioId = usuarioId,
                onGuardar = onGuardar
            )
        }
    ) { padding ->
        LimiteContent(
            estadoLimite = estadoLimite,
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
private fun BotonGuardarLimite(
    estadoLimite: EstadoLimite,
    context: Context,
    limiteParaEditar: LimiteGastoDto?,
    usuarioId: Int,
    onGuardar: (Double, Int, String, Int) -> Unit
) {
    Button(
        onClick = {
            manejarGuardadoLimite(
                estadoLimite = estadoLimite,
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
    estadoLimite: EstadoLimite,
    categoriasGasto: List<CategoriaDto>,
    padding: PaddingValues
) {
    Column(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SelectorCategoriaLimite(
            categorias = categoriasGasto,
            categoriaSeleccionada = estadoLimite.categoriaSeleccionada,
            onCategoriaSeleccionada = { estadoLimite.categoriaSeleccionada = it }
        )

        CampoMontoLimite(
            monto = estadoLimite.montoLimite,
            onMontoChange = { estadoLimite.montoLimite = it }
        )

        SelectorPeriodoLimite(
            periodo = estadoLimite.periodoSeleccionado,
            onPeriodoSeleccionado = { estadoLimite.periodoSeleccionado = it }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectorCategoriaLimite(
    categorias: List<CategoriaDto>,
    categoriaSeleccionada: CategoriaDto?,
    onCategoriaSeleccionada: (CategoriaDto) -> Unit
) {
    var expandedCategoria by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expandedCategoria,
        onExpandedChange = { expandedCategoria = !expandedCategoria }
    ) {
        OutlinedTextField(
            value = categoriaSeleccionada?.nombre ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Categoría (Solo Gastos)") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoria)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = obtenerColoresTextFieldLimite()
        )
        ExposedDropdownMenu(
            expanded = expandedCategoria,
            onDismissRequest = { expandedCategoria = false }
        ) {
            categorias.forEach { cat ->
                DropdownMenuItem(
                    text = { Text(cat.nombre) },
                    onClick = {
                        onCategoriaSeleccionada(cat)
                        expandedCategoria = false
                    }
                )
            }
        }
    }
}

@Composable
private fun CampoMontoLimite(
    monto: String,
    onMontoChange: (String) -> Unit
) {
    OutlinedTextField(
        value = monto,
        onValueChange = onMontoChange,
        label = { Text("Monto del Límite") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = obtenerColoresTextFieldLimite()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectorPeriodoLimite(
    periodo: String,
    onPeriodoSeleccionado: (String) -> Unit
) {
    val periodos = listOf("Diario", "Semanal", "Quincenal", "Mensual", "Anual")
    var expandedPeriodo by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expandedPeriodo,
        onExpandedChange = { expandedPeriodo = !expandedPeriodo }
    ) {
        OutlinedTextField(
            value = periodo,
            onValueChange = {},
            readOnly = true,
            label = { Text("Período") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPeriodo)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = obtenerColoresTextFieldLimite()
        )
        ExposedDropdownMenu(
            expanded = expandedPeriodo,
            onDismissRequest = { expandedPeriodo = false }
        ) {
            periodos.forEach { p ->
                DropdownMenuItem(
                    text = { Text(p) },
                    onClick = {
                        onPeriodoSeleccionado(p)
                        expandedPeriodo = false
                    }
                )
            }
        }
    }
}

private class EstadoLimite(
    categoriaSeleccionada: CategoriaDto?,
    montoLimite: String,
    periodoSeleccionado: String
) {
    var categoriaSeleccionada by mutableStateOf(categoriaSeleccionada)
    var montoLimite by mutableStateOf(montoLimite)
    var periodoSeleccionado by mutableStateOf(periodoSeleccionado)
}

private data class ResultadoValidacionLimite(
    val esValido: Boolean,
    val mensajeError: String = ""
)

private fun validarDatosLimite(estadoLimite: EstadoLimite): ResultadoValidacionLimite {
    return when {
        estadoLimite.categoriaSeleccionada == null ->
            ResultadoValidacionLimite(false, "Selecciona una categoría")
        estadoLimite.montoLimite.isBlank() ->
            ResultadoValidacionLimite(false, "Ingresa el monto del límite")
        estadoLimite.montoLimite.toDoubleOrNull() == null || estadoLimite.montoLimite.toDoubleOrNull()!! <= 0 ->
            ResultadoValidacionLimite(false, "El monto debe ser un número válido mayor que cero")
        estadoLimite.periodoSeleccionado.isBlank() ->
            ResultadoValidacionLimite(false, "Selecciona un período")
        else -> ResultadoValidacionLimite(true)
    }
}

private fun manejarGuardadoLimite(
    estadoLimite: EstadoLimite,
    context: Context,
    limiteParaEditar: LimiteGastoDto?,
    usuarioId: Int,
    onGuardar: (Double, Int, String, Int) -> Unit
) {
    val validacion = validarDatosLimite(estadoLimite)

    if (!validacion.esValido) {
        Toast.makeText(context, validacion.mensajeError, Toast.LENGTH_SHORT).show()
        return
    }

    val monto = estadoLimite.montoLimite.toDoubleOrNull() ?: 0.0
    val categoriaId = estadoLimite.categoriaSeleccionada!!.categoriaId

    onGuardar(monto, categoriaId, estadoLimite.periodoSeleccionado, usuarioId)

    if (limiteParaEditar == null) {
        limpiarFormularioLimite(estadoLimite)
    }
}

private fun limpiarFormularioLimite(estadoLimite: EstadoLimite) {
    estadoLimite.montoLimite = ""
    estadoLimite.categoriaSeleccionada = null
    estadoLimite.periodoSeleccionado = ""
}

@Composable
private fun obtenerColoresTextFieldLimite(): TextFieldColors {
    return OutlinedTextFieldDefaults.colors(
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        cursorColor = MaterialTheme.colorScheme.primary
    )
}