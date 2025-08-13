package ucne.edu.fintracker.presentation.pagorecurrente

import android.app.DatePickerDialog
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
import ucne.edu.fintracker.presentation.remote.dto.CategoriaDto
import ucne.edu.fintracker.presentation.remote.dto.PagoRecurrenteDto
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

    var monto by remember { mutableStateOf(TextFieldValue(pagoParaEditar?.monto?.toString() ?: "")) }
    var categoriaSeleccionada by remember { mutableStateOf<CategoriaDto?>(null) }
    var frecuencia by remember { mutableStateOf(pagoParaEditar?.frecuencia ?: "") }
    var fechaInicio by remember { mutableStateOf(pagoParaEditar?.fechaInicio?.toString() ?: "") }
    var fechaFin by remember { mutableStateOf(pagoParaEditar?.fechaFin?.toString() ?: "") }

    val periodos = listOf("Diario", "Semanal", "Quincenal", "Mensual", "Anual")

    LaunchedEffect(categorias, pagoParaEditar) {
        if (pagoParaEditar != null && categorias.isNotEmpty()) {
            categoriaSeleccionada = categorias.find { it.categoriaId == pagoParaEditar.categoriaId }
        }
    }
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (pagoParaEditar == null) "Agregar Pago Recurrente" else "Editar Pago Recurrente",
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
        },
        bottomBar = {
            Button(
                onClick = {
                    val montoVal = monto.text.toDoubleOrNull()
                    when {
                        monto.text.isBlank() -> {
                            Toast.makeText(context, "El monto es obligatorio", Toast.LENGTH_SHORT).show()
                        }
                        montoVal == null || montoVal <= 0.0 -> {
                            Toast.makeText(context, "El monto debe ser un número válido mayor que cero", Toast.LENGTH_SHORT).show()
                        }
                        categoriaSeleccionada == null -> {
                            Toast.makeText(context, "Selecciona una categoría", Toast.LENGTH_SHORT).show()
                        }
                        frecuencia.isBlank() -> {
                            Toast.makeText(context, "Selecciona una frecuencia", Toast.LENGTH_SHORT).show()
                        }
                        fechaInicio.isBlank() -> {
                            Toast.makeText(context, "Selecciona una fecha de inicio", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            onGuardar(
                                montoVal,
                                categoriaSeleccionada!!.categoriaId,
                                frecuencia,
                                OffsetDateTime.parse(fechaInicio),
                                fechaFin.takeIf { it.isNotBlank() }?.let { OffsetDateTime.parse(it) },
                                usuarioId
                            )
                            if (pagoParaEditar == null) {
                                monto = TextFieldValue("")
                                categoriaSeleccionada = null
                                frecuencia = ""
                                fechaInicio = ""
                                fechaFin = ""
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A))
            ) {
                Text(if (pagoParaEditar == null) "Agregar Pago Recurrente" else "Guardar Cambios")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = monto,
                onValueChange = { nuevoValor ->
                    if (nuevoValor.text.matches(Regex("^\\d*(\\.\\d*)?$"))) {
                        monto = nuevoValor
                    }
                },
                label = { Text("Monto") },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            monto = monto.copy(selection = TextRange(0, monto.text.length))
                        } else {
                            val numero = monto.text.toDoubleOrNull() ?: 0.0
                            val redondeado = String.format("%.2f", numero)
                            monto = TextFieldValue(redondeado, TextRange(redondeado.length))
                        }
                    },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )


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
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
                )
                ExposedDropdownMenu(
                    expanded = expandedCategoria,
                    onDismissRequest = { expandedCategoria = false }
                ) {
                    categorias.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.nombre) },
                            onClick = {
                                categoriaSeleccionada = cat
                                expandedCategoria = false
                            }
                        )
                    }
                }
            }

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
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )
                ExposedDropdownMenu(
                    expanded = expandedFrecuencia,
                    onDismissRequest = { expandedFrecuencia = false }
                ) {
                    periodos.forEach { p ->
                        DropdownMenuItem(
                            text = { Text(p) },
                            onClick = {
                                frecuencia = p
                                expandedFrecuencia = false
                            }
                        )
                    }
                }
            }

            FechaSelector(
                label = "Fecha de Inicio",
                fecha = fechaInicio,
                onFechaSeleccionada = { fechaInicio = it },
                modifier = Modifier.clip(RoundedCornerShape(16.dp))
            )

            FechaSelector(
                label = "Fecha de Finalización (Opcional)",
                fecha = fechaFin,
                onFechaSeleccionada = { fechaFin = it },
                modifier = Modifier.clip(RoundedCornerShape(16.dp))
            )
        }
    }
}


@Composable
private fun FechaSelector(
label: String,
 fecha: String,
 modifier: Modifier = Modifier,
 onFechaSeleccionada: (String) -> Unit) {
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
            readOnly = true
        )
        IconButton(
            onClick = {
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
        ) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Seleccionar fecha"
            )
        }
    }
}
