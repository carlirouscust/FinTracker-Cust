package ucne.edu.fintracker.presentation.gasto

import android.app.DatePickerDialog
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
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GastoScreen(
    categorias: List<String>,
    usuarioId: Int,
    tipoInicial: String = "Gasto",
    onGuardar: (tipo: String, monto: Double, categoriaNombre: String, fecha: String, notas: String, UsuarioId: Int) -> Unit,
    onCancel: () -> Unit
) {
    var tipo by remember { mutableStateOf(tipoInicial) }
    var monto by remember { mutableStateOf(TextFieldValue("")) }
    var categoriaSeleccionada by remember { mutableStateOf<String?>(null) }
    var expandedCategoria by remember { mutableStateOf(false) }
    var fechaSeleccionada by remember { mutableStateOf("Hoy") }
    var notas by remember { mutableStateOf("") }

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Agregar Transacción",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Botones Gasto / Ingreso
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { tipo = "Gasto" },
                    colors = if (tipo == "Gasto")
                        ButtonDefaults.buttonColors(containerColor = Color(0xFF85D844))
                    else
                        ButtonDefaults.buttonColors(containerColor = Color(0xFFD3D3D3)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Gasto", color = if (tipo == "Gasto") Color.White else Color.Black)
                }
                Button(
                    onClick = { tipo = "Ingreso" },
                    colors = if (tipo == "Ingreso")
                        ButtonDefaults.buttonColors(containerColor = Color(0xFF85D844))
                    else
                        ButtonDefaults.buttonColors(containerColor = Color(0xFFD3D3D3)),
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                ) {
                    Text("Ingreso", color = if (tipo == "Ingreso") Color.White else Color.Black)
                }
            }

            // Campo monto
            OutlinedTextField(
                value = monto,
                onValueChange = {
                    if (it.text.matches(Regex("^\\d*\\.?\\d*\$"))) {
                        monto = it
                    }
                },
                label = { Text("Monto") },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            monto = monto.copy(selection = TextRange(0, monto.text.length))
                        }
                        if (!focusState.isFocused) {
                            val numero = monto.text.toDoubleOrNull() ?: 0.0
                            val redondeado = String.format("%.2f", numero)
                            monto = TextFieldValue(redondeado, TextRange(redondeado.length))
                        }
                    },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.Black,
                    cursorColor = Color.Black
                )
            )

            // Categoría
            ExposedDropdownMenuBox(
                expanded = expandedCategoria,
                onExpandedChange = { expandedCategoria = !expandedCategoria }
            ) {
                OutlinedTextField(
                    value = categoriaSeleccionada ?: "Seleccionar categoría",
                    onValueChange = {},
                    label = { Text("Categoría") },
                    readOnly = true,
                    shape = RoundedCornerShape(16.dp),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoria)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Black,
                        cursorColor = Color.Black
                    )
                )
                ExposedDropdownMenu(
                    expanded = expandedCategoria,
                    onDismissRequest = { expandedCategoria = false }
                ) {
                    categorias.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                categoriaSeleccionada = cat
                                expandedCategoria = false
                            }
                        )
                    }
                }
            }

            // Fecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listOf("Hoy", "Ayer").forEach { dia ->
                    val selected = fechaSeleccionada == dia
                    Button(
                        onClick = { fechaSeleccionada = dia },
                        colors = if (selected)
                            ButtonDefaults.buttonColors(containerColor = Color(0xFF85D844))
                        else
                            ButtonDefaults.buttonColors(containerColor = Color(0xFFD3D3D3)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(dia, color = if (selected) Color.White else Color.Black)
                    }
                }

                IconButton(
                    onClick = {
                        val calendario = Calendar.getInstance()
                        DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                fechaSeleccionada = "$dayOfMonth/${month + 1}/$year"
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

            // Notas
            OutlinedTextField(
                value = notas,
                onValueChange = { notas = it },
                label = { Text("Notas") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.Black,
                    cursorColor = Color.Black
                )
            )

            Button(
                onClick = {
                    val montoDouble = monto.text.toDoubleOrNull() ?: 0.0
                    val cat = categoriaSeleccionada ?: ""

                    onGuardar(tipo, montoDouble, cat, fechaSeleccionada, notas, usuarioId)

                    // limpiar campos
                    monto = TextFieldValue("")
                    notas = ""
                    categoriaSeleccionada = null
                    fechaSeleccionada = "Hoy"
                    tipo = tipoInicial
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .height(50.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF85D844))
            ) {
                Text("Guardar", color = Color.White, fontSize = 18.sp)
            }
        }
    }
}
