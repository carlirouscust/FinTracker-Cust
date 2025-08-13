package ucne.edu.fintracker.presentation.gasto

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import ucne.edu.fintracker.remote.dto.CategoriaDto
import ucne.edu.fintracker.remote.dto.TransaccionDto

@Composable
fun GastoScreen(
    gastoViewModel: GastoViewModel = hiltViewModel(),
    categorias: List<CategoriaDto>,
    usuarioId: Int,
    transaccionParaEditar: TransaccionDto? = null,
    tipoInicial: String = "Gasto",
    onGuardar: (tipo: String, monto: Double, categoriaNombre: String, fecha: OffsetDateTime, notas: String, usuarioId: Int) -> Unit,
    onCancel: () -> Unit
) {
    var tipo by remember { mutableStateOf(transaccionParaEditar?.tipo ?: tipoInicial) }
    var monto by remember { mutableStateOf(TextFieldValue(transaccionParaEditar?.monto?.toString() ?: "")) }
    var expandedCategoria by remember { mutableStateOf(false) }
    var fechaSeleccionada by remember {
        mutableStateOf(
            transaccionParaEditar?.fecha ?: OffsetDateTime.now()
        )
    }
    var notas by remember { mutableStateOf(transaccionParaEditar?.notas ?: "") }
    var categoriaSeleccionada by remember { mutableStateOf<CategoriaDto?>(null) }


    LaunchedEffect(transaccionParaEditar, categorias) {
        categoriaSeleccionada = transaccionParaEditar?.let { trans ->
            categorias.find { it.categoriaId == trans.categoriaId }
        }
    }
    val categorias by gastoViewModel.categorias.collectAsState()
    val context = LocalContext.current
    val categoriasFiltradas = categorias.filter { it.tipo.equals(tipo, ignoreCase = true) }

    Scaffold(
        topBar = { GastoTopBar(onCancel, transaccionParaEditar == null) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            TipoSelector(tipo, onTipoChange = { tipo = it })

            MontoInput(monto) { nuevoMonto ->
                if (nuevoMonto.text.matches(Regex("^\\d*\\.?\\d*\$"))) monto = nuevoMonto
            }

            CategoriaDropdown(
                categoriasFiltradas,
                categoriaSeleccionada,
                expandedCategoria,
                onExpandChange = { expandedCategoria = it },
                onCategoriaSelect = {
                    categoriaSeleccionada = it
                    expandedCategoria = false
                }
            )

            FechaSelector(fechaSeleccionada) { nuevaFecha ->
                fechaSeleccionada = nuevaFecha
            }

            NotasInput(notas) { nuevasNotas -> notas = nuevasNotas }

            GuardarBoton(
                monto,
                categoriaSeleccionada,
                fechaSeleccionada,
                tipo,
                notas,
                usuarioId,
                transaccionParaEditar,
                context,
                onGuardar
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GastoTopBar(onCancel: () -> Unit, isNuevo: Boolean) {
    TopAppBar(
        title = {
            Text(
                text = if (isNuevo) "Agregar Transacción" else "Editar Transacción",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        navigationIcon = {
            IconButton(onClick = onCancel) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = MaterialTheme.colorScheme.onSurface)
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
private fun TipoSelector(tipo: String, onTipoChange: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        val tipos = listOf("Gasto", "Ingreso")
        tipos.forEach { t ->
            val selected = tipo == t
            Button(
                onClick = { onTipoChange(t) },
                colors = if (selected) ButtonDefaults.buttonColors(containerColor = Color(0xFF85D844))
                else ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.weight(1f).then(if (t == "Ingreso") Modifier.padding(start = 8.dp) else Modifier)
            ) {
                Text(t, color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun MontoInput(monto: TextFieldValue, onValueChange: (TextFieldValue) -> Unit) {
//    val context = LocalContext.current
    OutlinedTextField(
        value = monto,
        onValueChange = onValueChange,
        label = { Text("Monto") },
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    onValueChange(monto.copy(selection = TextRange(0, monto.text.length)))
                } else {
                    val numero = monto.text.toDoubleOrNull() ?: 0.0
                    val redondeado = String.format("%.2f", numero)
                    onValueChange(TextFieldValue(redondeado, TextRange(redondeado.length)))
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoriaDropdown(
    categoriasFiltradas: List<CategoriaDto>,
    categoriaSeleccionada: CategoriaDto?,
    expandedCategoria: Boolean,
    onExpandChange: (Boolean) -> Unit,
    onCategoriaSelect: (CategoriaDto) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expandedCategoria,
        onExpandedChange = onExpandChange
    ) {
        OutlinedTextField(
            value = categoriaSeleccionada?.nombre ?: "Seleccionar categoría",
            onValueChange = {},
            label = { Text("Categoría") },
            readOnly = true,
            shape = RoundedCornerShape(16.dp),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoria) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )
        ExposedDropdownMenu(
            expanded = expandedCategoria,
            onDismissRequest = { onExpandChange(false) }
        ) {
            categoriasFiltradas.forEach { cat ->
                DropdownMenuItem(
                    text = { Text(cat.nombre) },
                    onClick = { onCategoriaSelect(cat) }
                )
            }
        }
    }
}

@Composable
fun FechaSelector(fechaSeleccionada: OffsetDateTime, onFechaChange: (OffsetDateTime) -> Unit) {
    val context = LocalContext.current

    Text(
        text = "Fecha seleccionada: ${fechaSeleccionada.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.fillMaxWidth()
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val hoy = OffsetDateTime.now()
        val ayer = hoy.minusDays(1)

        listOf(hoy, ayer).forEach { fecha ->
            val selected = fechaSeleccionada.toLocalDate() == fecha.toLocalDate()

            println("DEBUG - Comparando: fechaSeleccionada=${fechaSeleccionada.toLocalDate()} vs fecha=${fecha.toLocalDate()} = $selected")

            Button(
                onClick = {
                    println("DEBUG - Botón clickeado: ${fecha.toLocalDate()}")
                    onFechaChange(fecha)
                },
                colors = if (selected)
                    ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A))
                else
                    ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (fecha.toLocalDate() == hoy.toLocalDate()) "Hoy" else "Ayer",
                    color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        IconButton(
            onClick = {
                val fechaActual = fechaSeleccionada.toLocalDate()
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        val nuevaFecha = LocalDate.of(year, month + 1, dayOfMonth)
                            .atStartOfDay()
                            .atOffset(ZoneOffset.UTC)

                        println("DEBUG - DatePicker seleccionó: ${nuevaFecha.toLocalDate()}")
                        onFechaChange(nuevaFecha)
                    },
                    fechaActual.year,
                    fechaActual.monthValue - 1,
                    fechaActual.dayOfMonth
                ).show()
            }
        ) {
            Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha", tint = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun NotasInput(notas: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = notas,
        onValueChange = onValueChange,
        label = { Text("Notas") },
        modifier = Modifier.fillMaxWidth().height(120.dp),
        maxLines = 5,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
private fun GuardarBoton(
    monto: TextFieldValue,
    categoriaSeleccionada: CategoriaDto?,
    fechaSeleccionada: OffsetDateTime,
    tipo: String,
    notas: String,
    usuarioId: Int,
    transaccionParaEditar: TransaccionDto?,
    context: Context,
    onGuardar: (tipo: String, monto: Double, categoriaNombre: String, fecha: OffsetDateTime, notas: String, usuarioId: Int) -> Unit
) {
    Button(
        onClick = {
            val montoDouble = monto.text.toDoubleOrNull() ?: 0.0
            val categoriaId = categoriaSeleccionada?.categoriaId ?: 0

            if (montoDouble <= 0.0 || categoriaId == 0) {
                Toast.makeText(context, "Completa los campos correctamente", Toast.LENGTH_SHORT).show()
                return@Button
            }

            onGuardar(tipo, montoDouble, categoriaSeleccionada?.nombre ?: "", fechaSeleccionada, notas, usuarioId)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .height(50.dp),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF85D844))
    ) {
        Text(
            if (transaccionParaEditar == null) "Guardar" else "Guardar Cambios",
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 18.sp
        )
    }
}

