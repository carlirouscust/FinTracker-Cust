package ucne.edu.fintracker.presentation.metaahorro

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import java.util.Calendar
import ucne.edu.fintracker.presentation.remote.dto.MetaAhorroDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetaMontoAhorroScreen(
    meta: MetaAhorroDto,
    onGuardarMonto: (Double, OffsetDateTime) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current

    var monto by remember { mutableStateOf("") }
    var fechaMonto by remember { mutableStateOf(OffsetDateTime.now(ZoneOffset.UTC)) }

    val fechaFormatter = DateTimeFormatter.ofPattern("dd 'de' MMMM, yyyy")

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Agregar Monto Ahorro",
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
        },
        bottomBar = {
            Button(
                onClick = {
                    if (monto.isNotBlank()) {
                        onGuardarMonto(monto.toDoubleOrNull() ?: 0.0, fechaMonto)
                        monto = ""
                        fechaMonto = OffsetDateTime.now(ZoneOffset.UTC)
                    } else {
                        Toast.makeText(context, "Ingrese un monto válido", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A))
            ) {
                Text("Guardar Monto")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (meta.imagen != null) {
                Image(
                    painter = rememberAsyncImagePainter(meta.imagen),
                    contentDescription = "Imagen meta",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color(0xFFEFEFEF)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Sin imagen", color = Color.Gray)
                }
            }

            Text(
                text = "Meta: RD$ ${meta.montoObjetivo}",
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Fecha límite: ${meta.fechaFinalizacion.format(fechaFormatter)}",
                fontWeight = FontWeight.Medium,
                color = Color.DarkGray,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            OutlinedTextField(
                value = monto,
                onValueChange = { monto = it },
                label = { Text("Monto de ahorro (RD$)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            FechaMontoSelector(
                label = "Fecha del monto",
                fecha = fechaMonto,
                onFechaSeleccionada = { fechaMonto = it }
            )
        }
    }
}

@Composable
private fun FechaMontoSelector(
    label: String,
    fecha: OffsetDateTime,
    onFechaSeleccionada: (OffsetDateTime) -> Unit
) {
    val context = LocalContext.current
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = fecha.format(formatter),
            onValueChange = {},
            label = { Text(label) },
            modifier = Modifier.weight(1f),
            readOnly = true
        )
        IconButton(
            onClick = {
                val calendario = Calendar.getInstance()
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        val seleccionada = OffsetDateTime.of(
                            year, month + 1, dayOfMonth,
                            0, 0, 0, 0,
                            ZoneOffset.UTC
                        )
                        onFechaSeleccionada(seleccionada)
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
