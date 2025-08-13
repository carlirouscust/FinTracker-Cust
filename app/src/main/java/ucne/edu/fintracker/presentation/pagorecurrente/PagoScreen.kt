package ucne.edu.fintracker.presentation.pagorecurrente

import android.app.DatePickerDialog
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.threeten.bp.OffsetDateTime
import ucne.edu.fintracker.remote.dto.CategoriaDto
import ucne.edu.fintracker.remote.dto.PagoRecurrenteDto
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PagoScreen(
    viewModel: PagoViewModel,
    usuarioId: Int,
    pagoParaEditar: PagoRecurrenteDto? = null,
    onGuardar: (Double, Int, String, OffsetDateTime, OffsetDateTime?, Int) -> Unit,
    onCancel: () -> Unit
) {
    val categorias by viewModel.categorias.collectAsState()
    val context = LocalContext.current

    val estadoPago = remember {
        EstadoPago(
            monto = pagoParaEditar?.monto?.toString() ?: "",
            categoriaSeleccionada = null,
            frecuencia = pagoParaEditar?.frecuencia ?: "",
            fechaInicio = pagoParaEditar?.fechaInicio?.toString() ?: "",
            fechaFin = pagoParaEditar?.fechaFin?.toString() ?: ""
        )
    }

    LaunchedEffect(categorias, pagoParaEditar) {
        if (pagoParaEditar != null && categorias.isNotEmpty()) {
            estadoPago.categoriaSeleccionada = categorias.find { it.categoriaId == pagoParaEditar.categoriaId }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            PagoTopBar(
                esEdicion = pagoParaEditar != null,
                onCancel = onCancel
            )
        },
        bottomBar = {
            BotonGuardar(
                estadoPago = estadoPago,
                context = context,
                pagoParaEditar = pagoParaEditar,
                usuarioId = usuarioId,
                onGuardar = onGuardar
            )
        }
    ) { padding ->
        PagoContent(
            estadoPago = estadoPago,
            categorias = categorias,
            padding = padding
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PagoTopBar(
    esEdicion: Boolean,
    onCancel: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = if (esEdicion) "Editar Pago Recurrente" else "Agregar Pago Recurrente",
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
private fun BotonGuardar(
    estadoPago: EstadoPago,
    context: Context,
    pagoParaEditar: PagoRecurrenteDto?,
    usuarioId: Int,
    onGuardar: (Double, Int, String, OffsetDateTime, OffsetDateTime?, Int) -> Unit
) {
    Button(
        onClick = {
            manejarGuardado(
                estadoPago = estadoPago,
                context = context,
                pagoParaEditar = pagoParaEditar,
                usuarioId = usuarioId,
                onGuardar = onGuardar
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A))
    ) {
        Text(if (pagoParaEditar == null) "Agregar Pago Recurrente" else "Guardar Cambios")
    }
}

@Composable
private fun PagoContent(
    estadoPago: EstadoPago,
    categorias: List<CategoriaDto>,
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
        CampoMonto(
            monto = estadoPago.monto,
            onMontoChange = { estadoPago.monto = it }
        )

        SelectorCategoria(
            categorias = categorias,
            categoriaSeleccionada = estadoPago.categoriaSeleccionada,
            onCategoriaSeleccionada = { estadoPago.categoriaSeleccionada = it }
        )

        SelectorFrecuencia(
            frecuencia = estadoPago.frecuencia,
            onFrecuenciaSeleccionada = { estadoPago.frecuencia = it }
        )

        FechaSelector(
            label = "Fecha de Inicio",
            fecha = estadoPago.fechaInicio,
            onFechaSeleccionada = { estadoPago.fechaInicio = it },
            modifier = Modifier.clip(RoundedCornerShape(16.dp))
        )

        FechaSelector(
            label = "Fecha de Finalización (Opcional)",
            fecha = estadoPago.fechaFin,
            onFechaSeleccionada = { estadoPago.fechaFin = it },
            modifier = Modifier.clip(RoundedCornerShape(16.dp))
        )
    }
}

@Composable
private fun CampoMonto(
    monto: String,
    onMontoChange: (String) -> Unit
) {
    var montoFieldValue by remember { mutableStateOf(TextFieldValue(monto)) }

    LaunchedEffect(monto) {
        if (monto != montoFieldValue.text) {
            montoFieldValue = TextFieldValue(monto, TextRange(monto.length))
        }
    }

    OutlinedTextField(
        value = montoFieldValue,
        onValueChange = { nuevoValor ->
            if (nuevoValor.text.matches(Regex("^\\d*(\\.\\d*)?$"))) {
                montoFieldValue = nuevoValor
                onMontoChange(nuevoValor.text)
            }
        },
        label = { Text("Monto") },
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    montoFieldValue = montoFieldValue.copy(selection = TextRange(0, montoFieldValue.text.length))
                } else {
                    val numero = montoFieldValue.text.toDoubleOrNull() ?: 0.0
                    val redondeado = String.format("%.2f", numero)
                    montoFieldValue = TextFieldValue(redondeado, TextRange(redondeado.length))
                    onMontoChange(redondeado)
                }
            },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        colors = obtenerColoresTextField()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectorCategoria(
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
            label = { Text("Categoría") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoria)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = obtenerColoresTextField()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectorFrecuencia(
    frecuencia: String,
    onFrecuenciaSeleccionada: (String) -> Unit
) {
    val periodos = listOf("Diario", "Semanal", "Quincenal", "Mensual", "Anual")
    var expandedFrecuencia by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expandedFrecuencia,
        onExpandedChange = { expandedFrecuencia = !expandedFrecuencia }
    ) {
        OutlinedTextField(
            value = frecuencia,
            onValueChange = {},
            readOnly = true,
            label = { Text("Frecuencia") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFrecuencia)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = obtenerColoresTextField()
        )
        ExposedDropdownMenu(
            expanded = expandedFrecuencia,
            onDismissRequest = { expandedFrecuencia = false }
        ) {
            periodos.forEach { p ->
                DropdownMenuItem(
                    text = { Text(p) },
                    onClick = {
                        onFrecuenciaSeleccionada(p)
                        expandedFrecuencia = false
                    }
                )
            }
        }
    }
}

@Composable
private fun FechaSelector(
    label: String,
    fecha: String,
    modifier: Modifier = Modifier,
    onFechaSeleccionada: (String) -> Unit
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = fecha,
            onValueChange = {},
            label = { Text(label) },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.weight(1f),
            readOnly = true,
            colors = obtenerColoresTextField()
        )
        IconButton(
            onClick = {
                mostrarSelectorFecha(context, onFechaSeleccionada)
            }
        ) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Seleccionar fecha"
            )
        }
    }
}

private class EstadoPago(
    monto: String,
    categoriaSeleccionada: CategoriaDto?,
    frecuencia: String,
    fechaInicio: String,
    fechaFin: String
) {
    var monto by mutableStateOf(monto)
    var categoriaSeleccionada by mutableStateOf(categoriaSeleccionada)
    var frecuencia by mutableStateOf(frecuencia)
    var fechaInicio by mutableStateOf(fechaInicio)
    var fechaFin by mutableStateOf(fechaFin)
}

private data class ResultadoValidacion(
    val esValido: Boolean,
    val mensajeError: String = ""
)

private fun validarDatos(estadoPago: EstadoPago): ResultadoValidacion {
    val montoVal = estadoPago.monto.toDoubleOrNull()

    return when {
        estadoPago.monto.isBlank() -> ResultadoValidacion(false, "El monto es obligatorio")
        montoVal == null || montoVal <= 0.0 -> ResultadoValidacion(false, "El monto debe ser un número válido mayor que cero")
        estadoPago.categoriaSeleccionada == null -> ResultadoValidacion(false, "Selecciona una categoría")
        estadoPago.frecuencia.isBlank() -> ResultadoValidacion(false, "Selecciona una frecuencia")
        estadoPago.fechaInicio.isBlank() -> ResultadoValidacion(false, "Selecciona una fecha de inicio")
        else -> ResultadoValidacion(true)
    }
}

private fun manejarGuardado(
    estadoPago: EstadoPago,
    context: Context,
    pagoParaEditar: PagoRecurrenteDto?,
    usuarioId: Int,
    onGuardar: (Double, Int, String, OffsetDateTime, OffsetDateTime?, Int) -> Unit
) {
    val validacion = validarDatos(estadoPago)

    if (!validacion.esValido) {
        Toast.makeText(context, validacion.mensajeError, Toast.LENGTH_SHORT).show()
        return
    }

    val montoVal = estadoPago.monto.toDoubleOrNull()!!
    val categoriaId = estadoPago.categoriaSeleccionada!!.categoriaId
    val fechaInicioParsed = OffsetDateTime.parse(estadoPago.fechaInicio)
    val fechaFinParsed = estadoPago.fechaFin.takeIf { it.isNotBlank() }?.let { OffsetDateTime.parse(it) }

    onGuardar(montoVal, categoriaId, estadoPago.frecuencia, fechaInicioParsed, fechaFinParsed, usuarioId)

    if (pagoParaEditar == null) {
        limpiarFormulario(estadoPago)
    }
}

private fun limpiarFormulario(estadoPago: EstadoPago) {
    estadoPago.monto = ""
    estadoPago.categoriaSeleccionada = null
    estadoPago.frecuencia = ""
    estadoPago.fechaInicio = ""
    estadoPago.fechaFin = ""
}

private fun mostrarSelectorFecha(
    context: Context,
    onFechaSeleccionada: (String) -> Unit
) {
    val calendario = Calendar.getInstance()
    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val mes = (month + 1).toString().padStart(2, '0')
            val dia = dayOfMonth.toString().padStart(2, '0')
            onFechaSeleccionada("$year-$mes-${dia}T00:00:00Z")
        },
        calendario.get(Calendar.YEAR),
        calendario.get(Calendar.MONTH),
        calendario.get(Calendar.DAY_OF_MONTH)
    ).show()
}

@Composable
private fun obtenerColoresTextField(): TextFieldColors {
    return OutlinedTextFieldDefaults.colors(
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        cursorColor = MaterialTheme.colorScheme.primary
    )
}