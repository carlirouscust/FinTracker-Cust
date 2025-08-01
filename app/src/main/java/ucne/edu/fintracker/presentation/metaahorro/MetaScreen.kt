package ucne.edu.fintracker.presentation.metaahorro

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import ucne.edu.fintracker.presentation.remote.dto.MetaAhorroDto
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetaScreen(
    metaParaEditar: MetaAhorroDto? = null,
    usuarioId: Int,
    onGuardar: (String, Double, OffsetDateTime, Boolean, String?, Int) -> Unit,
    onCancel: () -> Unit,
    onImagenSeleccionada: (String?) -> Unit
) {
    val contexto = LocalContext.current

    var nombreMeta by remember { mutableStateOf(metaParaEditar?.nombreMeta ?: "") }
    var montoObjetivo by remember { mutableStateOf(metaParaEditar?.montoObjetivo?.toString() ?: "") }
    var fechaFinalizacion by remember {
        mutableStateOf(
            metaParaEditar?.fechaFinalizacion
                ?: OffsetDateTime.now(ZoneOffset.UTC)
        )
    }
    var contribucionEstablecida by remember {
        mutableStateOf(metaParaEditar?.contribucionRecurrente != null)
    }
    var imagenUri by remember { mutableStateOf(metaParaEditar?.imagen) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            imagenUri = selectedUri.toString()
            onImagenSeleccionada(selectedUri.toString())
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (metaParaEditar == null) "Nueva Meta" else "Editar Meta",
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
                    if (nombreMeta.isNotBlank() && montoObjetivo.isNotBlank() && fechaFinalizacion != null) {
                        onGuardar(
                            nombreMeta,
                            montoObjetivo.toDoubleOrNull() ?: 0.0,
                            fechaFinalizacion,
                            contribucionEstablecida,
                            imagenUri,
                            usuarioId
                        )
                        if (metaParaEditar == null) {
                            nombreMeta = ""
                            montoObjetivo = ""
                            fechaFinalizacion = OffsetDateTime.now(ZoneOffset.UTC)
                            contribucionEstablecida = false
                            imagenUri = null
                        }
                    } else {
                        Toast.makeText(contexto, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A))
            ) {
                Text(if (metaParaEditar == null) "Guardar Meta" else "Guardar Cambios")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = nombreMeta,
                onValueChange = { nombreMeta = it },
                label = { Text("Nombre de la Meta") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = montoObjetivo,
                onValueChange = { montoObjetivo = it },
                label = { Text("Monto Objetivo (RD$)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            FechaSelector(
                label = "Fecha de Finalización",
                fecha = fechaFinalizacion,
                onFechaSeleccionada = { fechaFinalizacion = it }
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (contribucionEstablecida) "Establecido" else "No establecido",
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Medium
                )
                Switch(
                    checked = contribucionEstablecida,
                    onCheckedChange = { contribucionEstablecida = it }
                )
            }

            Text("Imagen de la Meta")

            Button(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cargar Imagen")
            }

            imagenUri?.let { uriStr ->
                val painter = rememberAsyncImagePainter(uriStr)
                Image(
                    painter = painter,
                    contentDescription = "Imagen seleccionada",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(top = 8.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
private fun FechaSelector(
    label: String,
    fecha: OffsetDateTime?,
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
            label = { Text(label) },
            modifier = Modifier.weight(1f),
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
                contentDescription = "Seleccionar fecha"
            )
        }
    }
}
