package ucne.edu.fintracker.presentation.gasto

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GastoScreen(
    categorias: List<String>,
    onGuardar: (
        tipo: String,
        monto: Double,
        categoria: String,
        fecha: String,
        notas: String
    ) -> Unit,
    onCancel: () -> Unit
) {
    var tipo by remember { mutableStateOf("Gastos") }
    var monto by remember { mutableStateOf("0.00") }
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { tipo = "Gastos" },
                    colors = if (tipo == "Gastos")
                        ButtonDefaults.buttonColors(containerColor = Color(0xFF85D844))
                    else
                        ButtonDefaults.buttonColors(containerColor = Color(0xFFD3D3D3)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Gastos", color = if (tipo == "Gastos") Color.White else Color.Black)
                }
                Button(
                    onClick = { tipo = "Ingresos" },
                    colors = if (tipo == "Ingresos")
                        ButtonDefaults.buttonColors(containerColor = Color(0xFF85D844))
                    else
                        ButtonDefaults.buttonColors(containerColor = Color(0xFFD3D3D3)),
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                ) {
                    Text("Ingresos", color = if (tipo == "Ingresos") Color.White else Color.Black)
                }
            }

            OutlinedTextField(
                value = monto,
                onValueChange = {
                    if (it.matches(Regex("^\\d*\\.?\\d*\$"))) monto = it
                },
                label = { Text("Monto") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.Black,
                    cursorColor = Color.Black
                )
            )

            ExposedDropdownMenuBox(
                expanded = expandedCategoria,
                onExpandedChange = { expandedCategoria = !expandedCategoria }
            ) {
                OutlinedTextField(
                    value = categoriaSeleccionada ?: "Seleccionar categoría",
                    onValueChange = {},
                    label = { Text("Categoría") },
                    readOnly = true,
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

            OutlinedTextField(
                value = notas,
                onValueChange = { notas = it },
                label = { Text("Notas") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5,
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
                    val montoDouble = monto.toDoubleOrNull() ?: 0.0
                    val cat = categoriaSeleccionada ?: ""
                    onGuardar(tipo, montoDouble, cat, fechaSeleccionada, notas)
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

