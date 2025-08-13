package ucne.edu.fintracker.presentation.metaahorro

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneOffset
import java.util.Calendar
import ucne.edu.fintracker.presentation.remote.dto.MetaAhorroDto


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetaMAhorroScreen(
    meta: MetaAhorroDto,
    usuarioId: Int,
    navController: NavController,
    onGuardarMonto: (Double, OffsetDateTime) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current

    var montoAhorrado by remember { mutableStateOf("") }
    var fechaMonto by remember { mutableStateOf(meta.fechaMontoAhorrado ?: OffsetDateTime.now(ZoneOffset.UTC)) }

    val fechaFormatter = DateTimeFormatter.ofPattern("dd 'de' MMMM, yyyy")

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Agregar Ahorro",
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
                    val monto = montoAhorrado.toDoubleOrNull()
                    if (monto != null && monto > 0) {
                        onGuardarMonto(monto, fechaMonto)
                        Toast.makeText(context, "Ahorro guardado exitosamente", Toast.LENGTH_SHORT).show()
                        navController.navigate("metaahorros/$usuarioId") {
                            popUpTo("metaahorros/$usuarioId") { inclusive = true }
                        }
                    } else {
                        Toast.makeText(context, "Ingrese un monto válido mayor a 0", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8BC34A)
                )
            ) {
                Text("Guardar Ahorro", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (meta.imagen != null) {
                Log.d("MetaMAhorroScreen", "Imagen recibida: ${meta.imagen}")
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
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Sin imagen", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Text(
                text = "Meta: RD$ ${meta.montoObjetivo}",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Fecha límite: ${meta.fechaFinalizacion.format(fechaFormatter)}",
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            OutlinedTextField(
                value = montoAhorrado,
                onValueChange = { montoAhorrado = it },
                label = { Text("Monto Ahorrado (RD$)", color = MaterialTheme.colorScheme.onSurface) },
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ej: 1000", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            )

            FechaSelector(
                label = "Fecha de Monto Ahorrado",
                fecha = fechaMonto,
                onFechaSeleccionada = { fechaMonto = it },
                modifier = Modifier.clip(RoundedCornerShape(16.dp))
            )
        }
    }
}

@Composable
private fun FechaSelector(
    label: String,
    fecha: OffsetDateTime?,
    modifier: Modifier = Modifier,
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
            value = fecha?.format(formatter) ?: "",
            onValueChange = {},
            label = { Text(label, color = MaterialTheme.colorScheme.onSurface) },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            readOnly = true
        )
        IconButton(
            onClick = {
                val calendario = Calendar.getInstance()
                android.app.DatePickerDialog(
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
                contentDescription = "Seleccionar fecha",
                tint = Color(0xFF8BC34A)
            )
        }
    }
}