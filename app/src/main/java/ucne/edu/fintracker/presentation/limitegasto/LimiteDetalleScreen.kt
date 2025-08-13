package ucne.edu.fintracker.presentation.limitegasto

import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ucne.edu.fintracker.remote.dto.LimiteGastoDto
import ucne.edu.fintracker.remote.dto.TransaccionDto
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import ucne.edu.fintracker.presentation.gasto.GastoViewModel
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.DayOfWeek

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LimiteDetalleScreen(
    limite: LimiteGastoDto,
    categoriaIcono: String,
    categoriaNombre: String,
    gastoViewModel: GastoViewModel,
    onBackClick: () -> Unit,
    onEditarClick: () -> Unit,
    onEliminarConfirmado: () -> Unit
) {
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }
    val uiState by gastoViewModel.uiState.collectAsState()
    val transacciones = uiState.transacciones

    val datosPresupuesto = remember(transacciones, limite.categoriaId, limite.periodo) {
        calcularDatosPresupuesto(transacciones, limite)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            LimiteDetalleTopBar(onBackClick = onBackClick)
        }
    ) { padding ->
        LimiteDetalleContent(
            limite = limite,
            categoriaIcono = categoriaIcono,
            categoriaNombre = categoriaNombre,
            datosPresupuesto = datosPresupuesto,
            transacciones = transacciones,
            padding = padding,
            onEditarClick = onEditarClick,
            onEliminarClick = { mostrarDialogoEliminar = true }
        )
    }

    if (mostrarDialogoEliminar) {
        DialogoConfirmacionEliminacion(
            onDismiss = { mostrarDialogoEliminar = false },
            onConfirm = {
                mostrarDialogoEliminar = false
                onEliminarConfirmado()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LimiteDetalleTopBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = { Text("Detalle del Límite") },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
private fun LimiteDetalleContent(
    limite: LimiteGastoDto,
    categoriaIcono: String,
    categoriaNombre: String,
    datosPresupuesto: DatosPresupuesto,
    transacciones: List<TransaccionDto>,
    padding: PaddingValues,
    onEditarClick: () -> Unit,
    onEliminarClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(padding)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        PresupuestoProgressSection(datosPresupuesto = datosPresupuesto)
        Divider()
        CategoriaSection(categoriaIcono = categoriaIcono, categoriaNombre = categoriaNombre)
        Divider()
        DetallesLimiteSection(
            limite = limite,
            datosPresupuesto = datosPresupuesto,
            transacciones = transacciones
        )
        Spacer(modifier = Modifier.weight(1f))
        AccionesSection(
            onEditarClick = onEditarClick,
            onEliminarClick = onEliminarClick
        )
    }
}

@Composable
private fun PresupuestoProgressSection(datosPresupuesto: DatosPresupuesto) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BarraProgreso(
            porcentaje = datosPresupuesto.porcentaje,
            excedePresupuesto = datosPresupuesto.excedePresupuesto
        )

        Spacer(modifier = Modifier.height(12.dp))

        EstadoPresupuestoTexts(datosPresupuesto = datosPresupuesto)
    }
}

@Composable
private fun BarraProgreso(porcentaje: Double, excedePresupuesto: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(24.dp)
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth((porcentaje / 100f).toFloat().coerceAtMost(1f))
                .background(
                    color = obtenerColorProgreso(porcentaje, excedePresupuesto),
                    shape = RoundedCornerShape(12.dp)
                )
        )

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${porcentaje.toInt()}%",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.5f),
                        blurRadius = 2f
                    )
                )
            )
        }
    }
}

@Composable
private fun EstadoPresupuestoTexts(datosPresupuesto: DatosPresupuesto) {
    Text(
        text = if (datosPresupuesto.excedePresupuesto) "Presupuesto excedido" else "Presupuesto consumido",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = if (datosPresupuesto.excedePresupuesto) Color.Red else MaterialTheme.colorScheme.onBackground
    )

    Spacer(modifier = Modifier.height(4.dp))

    Text(
        text = "RD$ ${String.format("%.2f", datosPresupuesto.totalGastado)} / RD$ ${String.format("%.2f", datosPresupuesto.montoLimite)}",
        fontSize = 16.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    if (datosPresupuesto.excedePresupuesto) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Excedido por RD$ ${String.format("%.2f", datosPresupuesto.totalGastado - datosPresupuesto.montoLimite)}",
            fontSize = 14.sp,
            color = Color.Red,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun CategoriaSection(categoriaIcono: String, categoriaNombre: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = categoriaIcono.takeIf { it.isNotBlank() } ?: "💵",
            fontSize = 36.sp,
            modifier = Modifier.padding(end = 12.dp)
        )
        Text(
            text = categoriaNombre,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun DetallesLimiteSection(
    limite: LimiteGastoDto,
    datosPresupuesto: DatosPresupuesto,
    transacciones: List<TransaccionDto>
) {
    val cantidadTransacciones = remember(transacciones, limite.categoriaId, limite.periodo) {
        contarTransaccionesPorPeriodo(transacciones, limite.categoriaId, limite.periodo)
    }

    DetalleItem(titulo = "Período", valor = limite.periodo)
    Divider()
    DetalleItem(titulo = "Monto Límite", valor = "RD$ ${String.format("%.2f", limite.montoLimite)}")
    Divider()
    DetalleItem(
        titulo = "Total Gastado",
        valor = "RD$ ${String.format("%.2f", datosPresupuesto.totalGastado)}",
        colorValor = if (datosPresupuesto.excedePresupuesto) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
    )
    Divider()
    DetalleItem(titulo = "Transacciones", valor = "$cantidadTransacciones transacciones de gasto")
    Divider()
}

@Composable
private fun DetalleItem(
    titulo: String,
    valor: String,
    colorValor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Text(titulo, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground)
    Text(valor, fontSize = 16.sp, color = colorValor)
}

@Composable
private fun AccionesSection(onEditarClick: () -> Unit, onEliminarClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            onClick = onEditarClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A)),
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.onPrimary)
            Spacer(Modifier.width(8.dp))
            Text("Editar", color = MaterialTheme.colorScheme.onPrimary)
        }

        Button(
            onClick = onEliminarClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.onError)
            Spacer(Modifier.width(8.dp))
            Text("Eliminar", color = MaterialTheme.colorScheme.onError)
        }
    }
}

@Composable
private fun DialogoConfirmacionEliminacion(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirmar eliminación") },
        text = { Text("¿Seguro que deseas eliminar este límite de gasto?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Eliminar", color = Color.Red)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = MaterialTheme.colorScheme.onSurface)
            }
        }
    )
}

private data class DatosPresupuesto(
    val totalGastado: Double,
    val montoLimite: Double,
    val porcentaje: Double,
    val excedePresupuesto: Boolean
)

private fun calcularDatosPresupuesto(
    transacciones: List<TransaccionDto>,
    limite: LimiteGastoDto
): DatosPresupuesto {
    val totalGastado = calcularTotalGastadoPorPeriodo(transacciones, limite.categoriaId, limite.periodo)
    val porcentaje = if (limite.montoLimite > 0) {
        ((totalGastado / limite.montoLimite) * 100).coerceAtMost(100.0)
    } else 0.0
    val excedePresupuesto = totalGastado > limite.montoLimite

    return DatosPresupuesto(
        totalGastado = totalGastado,
        montoLimite = limite.montoLimite,
        porcentaje = porcentaje,
        excedePresupuesto = excedePresupuesto
    )
}

private fun obtenerColorProgreso(porcentaje: Double, excedePresupuesto: Boolean): androidx.compose.ui.graphics.Color {
    return when {
        excedePresupuesto -> Color.Red
        porcentaje >= 80 -> Color(0xFFFF9800)
        porcentaje >= 60 -> Color(0xFFFFC107)
        else -> Color(0xFF4CAF50)
    }
}

private fun calcularTotalGastadoPorPeriodo(
    transacciones: List<TransaccionDto>,
    categoriaId: Int,
    periodo: String
): Double {
    val ahora = OffsetDateTime.now()
    return transacciones
        .filter { transaccion ->
            transaccion.tipo.trim().equals("Gasto", ignoreCase = true) &&
                    transaccion.categoriaId == categoriaId &&
                    estaEnPeriodo(transaccion.fecha, periodo, ahora)
        }
        .sumOf { it.monto }
}

private fun contarTransaccionesPorPeriodo(
    transacciones: List<TransaccionDto>,
    categoriaId: Int,
    periodo: String
): Int {
    val ahora = OffsetDateTime.now()
    return transacciones
        .filter { transaccion ->
            transaccion.tipo.trim().equals("Gasto", ignoreCase = true) &&
                    transaccion.categoriaId == categoriaId &&
                    estaEnPeriodo(transaccion.fecha, periodo, ahora)
        }
        .size
}

private fun estaEnPeriodo(
    fechaTransaccion: OffsetDateTime,
    periodo: String,
    fechaReferencia: OffsetDateTime
): Boolean {
    return when (periodo.lowercase()) {
        "diario" -> {
            val hoy = fechaReferencia.toLocalDate()
            fechaTransaccion.toLocalDate().isEqual(hoy)
        }
        "semanal" -> {
            val lunes = fechaReferencia.toLocalDate().with(DayOfWeek.MONDAY)
            val domingo = fechaReferencia.toLocalDate().with(DayOfWeek.SUNDAY)
            val fecha = fechaTransaccion.toLocalDate()
            !fecha.isBefore(lunes) && !fecha.isAfter(domingo)
        }
        "mensual" -> {
            val fecha = fechaTransaccion.toLocalDate()
            val referencia = fechaReferencia.toLocalDate()
            fecha.month == referencia.month && fecha.year == referencia.year
        }
        "anual" -> {
            fechaTransaccion.toLocalDate().year == fechaReferencia.toLocalDate().year
        }
        else -> false
    }
}