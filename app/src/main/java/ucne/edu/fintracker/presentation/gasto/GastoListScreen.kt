package ucne.edu.fintracker.presentation.gasto

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import ucne.edu.fintracker.presentation.categoria.CategoriaViewModel
import ucne.edu.fintracker.presentation.components.MenuScreen
import ucne.edu.fintracker.remote.dto.CategoriaDto
import ucne.edu.fintracker.remote.dto.TransaccionDto
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assistant
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CardDefaults
import org.threeten.bp.DayOfWeek
import java.util.Locale
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import org.threeten.bp.LocalDate
import org.threeten.bp.Month
import org.threeten.bp.format.TextStyle as ThreeTextStyle
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import org.threeten.bp.YearMonth
import org.threeten.bp.ZoneId
import org.threeten.bp.temporal.TemporalAdjusters
import ucne.edu.fintracker.presentation.categoria.CategoriaUiState
import ucne.edu.fintracker.presentation.panelUsuario.PanelUsuarioUiState
import java.io.File
import ucne.edu.fintracker.presentation.panelUsuario.PanelUsuarioViewModel

private object GastoListConstants {
    val PRIMARY_GREEN = Color(0xFF8BC34A)
    const val PROFILE_SIZE = 32
    const val ICON_SIZE = 20
    const val CARD_SIZE = 40
    const val BOTTOM_SPACER = 80
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GastoListScreen(
    viewModel: GastoViewModel,
    usuarioId: Int,
    categoriaViewModel: CategoriaViewModel,
    navController: NavController,
    onNuevoClick: () -> Unit,
    panelUsuarioViewModel: PanelUsuarioViewModel = hiltViewModel()
) {
    val categoriaState by categoriaViewModel.uiState.collectAsState()
    val state by viewModel.uiState.collectAsState()
    val panelUiState by panelUsuarioViewModel.uiState.collectAsStateWithLifecycle()

    var fechaSeleccionada by remember { mutableStateOf(OffsetDateTime.now()) }

    LaunchedEffect(usuarioId) {
        panelUsuarioViewModel.cargarUsuario(usuarioId)
    }

    val transaccionesFiltradas = getFilteredTransactions(
        state.transacciones,
        state.filtro,
        fechaSeleccionada,
        state.tipoSeleccionado
    )

    val drawerState = rememberDrawerState(DrawerValue.Closed)

    GastoListContent(
        state = state,
        categoriaState = categoriaState,
        panelUiState = panelUiState,
        transaccionesFiltradas = transaccionesFiltradas,
        fechaSeleccionada = fechaSeleccionada,
        usuarioId = usuarioId,
        navController = navController,
        viewModel = viewModel,
        drawerState = drawerState,
        onFechaSeleccionada = { fechaSeleccionada = it }
    )
}

@Composable
private fun GastoListContent(
    state: GastoUiState,
    categoriaState: CategoriaUiState,
    panelUiState: PanelUsuarioUiState,
    transaccionesFiltradas: List<TransaccionDto>,
    fechaSeleccionada: OffsetDateTime,
    usuarioId: Int,
    navController: NavController,
    viewModel: GastoViewModel,
    drawerState: DrawerState,
    onFechaSeleccionada: (OffsetDateTime) -> Unit
) {
    val scope = rememberCoroutineScope()
    val total = transaccionesFiltradas.sumOf { it.monto }

    MenuScreen(
        drawerState = drawerState,
        navController = navController,
        usuarioId = usuarioId,
        content = {
            Scaffold(
                topBar = {
                    GastoListTopBar(
                        panelUiState = panelUiState,
                        usuarioId = usuarioId,
                        navController = navController,
                        onMenuClick = { scope.launch { drawerState.open() } }
                    )
                },
                floatingActionButton = {
                    GastoListFAB(
                        tipo = state.tipoSeleccionado,
                        usuarioId = usuarioId,
                        navController = navController
                    )
                },
                bottomBar = { BottomNavigationBar(navController, usuarioId) }
            ) { paddingValues ->
                GastoListMainContent(
                    state = state,
                    categoriaState = categoriaState,
                    transaccionesFiltradas = transaccionesFiltradas,
                    fechaSeleccionada = fechaSeleccionada,
                    total = total,
                    viewModel = viewModel,
                    navController = navController,
                    usuarioId = usuarioId,
                    paddingValues = paddingValues,
                    onFechaSeleccionada = onFechaSeleccionada
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GastoListTopBar(
    panelUiState: PanelUsuarioUiState,
    usuarioId: Int,
    navController: NavController,
    onMenuClick: () -> Unit
) {
    TopAppBar(
        title = { GastoListTitle() },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
        },
        actions = {
            GastoListProfileAction(
                panelUiState = panelUiState,
                usuarioId = usuarioId,
                navController = navController
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
private fun GastoListTitle() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Fin",
            color = GastoListConstants.PRIMARY_GREEN,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp
            ),
            modifier = Modifier.padding(end = 2.dp)
        )
        Text(
            text = "Tracker",
            color = GastoListConstants.PRIMARY_GREEN,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Normal,
                fontSize = 32.sp
            )
        )
    }
}

@Composable
private fun GastoListProfileAction(
    panelUiState: PanelUsuarioUiState,
    usuarioId: Int,
    navController: NavController
) {
    IconButton(onClick = { navController.navigate("panel_usuario/$usuarioId") }) {
        Box(
            modifier = Modifier
                .size(GastoListConstants.PROFILE_SIZE.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            if (!panelUiState.usuario?.fotoPerfil.isNullOrEmpty()) {
                AsyncImage(
                    model = File(panelUiState?.usuario?.fotoPerfil),
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Perfil",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(GastoListConstants.ICON_SIZE.dp)
                )
            }
        }
    }
}

@Composable
private fun GastoListFAB(
    tipo: String,
    usuarioId: Int,
    navController: NavController
) {
    FloatingActionButton(
        onClick = { navController.navigate("gasto_nuevo/$tipo/$usuarioId") },
        containerColor = GastoListConstants.PRIMARY_GREEN,
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Nuevo", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun GastoListMainContent(
    state: GastoUiState,
    categoriaState: CategoriaUiState,
    transaccionesFiltradas: List<TransaccionDto>,
    fechaSeleccionada: OffsetDateTime,
    total: Double,
    viewModel: GastoViewModel,
    navController: NavController,
    usuarioId: Int,
    paddingValues: PaddingValues,
    onFechaSeleccionada: (OffsetDateTime) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(paddingValues)
    ) {
        GastoListControls(
            state = state,
            fechaSeleccionada = fechaSeleccionada,
            total = total,
            viewModel = viewModel,
            onFechaSeleccionada = onFechaSeleccionada
        )

        GastoListBody(
            state = state,
            categoriaState = categoriaState,
            transaccionesFiltradas = transaccionesFiltradas,
            navController = navController,
            usuarioId = usuarioId
        )
    }
}

@Composable
private fun GastoListControls(
    state: GastoUiState,
    fechaSeleccionada: OffsetDateTime,
    total: Double,
    viewModel: GastoViewModel,
    onFechaSeleccionada: (OffsetDateTime) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TipoSelector(state.tipoSeleccionado, viewModel::cambiarTipo, total)
        GastoFiltroBar(state.filtro, viewModel::cambiarFiltro)
        FechaTexto(
            filtro = state.filtro,
            fechaActual = fechaSeleccionada,
            onFechaSeleccionada = { nuevaFecha ->
                onFechaSeleccionada(nuevaFecha)
                viewModel.cambiarFecha(nuevaFecha)
            }
        )
    }
}

@Composable
private fun GastoListBody(
    state: GastoUiState,
    categoriaState: CategoriaUiState,
    transaccionesFiltradas: List<TransaccionDto>,
    navController: NavController,
    usuarioId: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        when {
            transaccionesFiltradas.isEmpty() -> {
                MensajeNoHayTransacciones(state.filtro, state.tipoSeleccionado)
            }
            else -> {
                GastoPieChart(
                    transacciones = transaccionesFiltradas,
                    categorias = categoriaState.categorias,
                    modifier = Modifier
                        .size(200.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }

        GastoListStateContent(
            state = state,
            categoriaState = categoriaState,
            transaccionesFiltradas = transaccionesFiltradas,
            navController = navController,
            usuarioId = usuarioId
        )

        Spacer(modifier = Modifier.height(GastoListConstants.BOTTOM_SPACER.dp))
    }
}

@Composable
private fun GastoListStateContent(
    state: GastoUiState,
    categoriaState: CategoriaUiState,
    transaccionesFiltradas: List<TransaccionDto>,
    navController: NavController,
    usuarioId: Int
) {
    when {
        state.isLoading -> {
            GastoListLoadingState()
        }
        state.error != null -> {
            GastoListErrorState(state.error)
        }
        transaccionesFiltradas.isEmpty() -> {
            GastoListEmptyState()
        }
        else -> {
            GastoListTransactions(
                transacciones = transaccionesFiltradas,
                categorias = categoriaState.categorias,
                navController = navController,
                usuarioId = usuarioId
            )
        }
    }
}

@Composable
private fun GastoListLoadingState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = GastoListConstants.PRIMARY_GREEN)
    }
}

@Composable
private fun GastoListErrorState(error: String?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = error ?: "Error desconocido",
            color = Color.Red
        )
    }
}

@Composable
private fun GastoListEmptyState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No hay transacciones.",
            color = Color.Gray
        )
    }
}

@Composable
private fun GastoListTransactions(
    transacciones: List<TransaccionDto>,
    categorias: List<CategoriaDto>,
    navController: NavController,
    usuarioId: Int
) {
    transacciones.forEach { transaccion ->
        TransactionCard(
            transaccion = transaccion,
            categorias = categorias,
            navController = navController,
            usuarioId = usuarioId
        )
    }
}

@Composable
private fun TransactionCard(
    transaccion: TransaccionDto,
    categorias: List<CategoriaDto>,
    navController: NavController,
    usuarioId: Int
) {
    val categoria = categorias.find { it.categoriaId == transaccion.categoriaId }
    val colorFondo = getCategoriaColor(categoria)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("gasto_detalle/$usuarioId/${transaccion.transaccionId}")
            },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TransactionInfo(categoria = categoria, transaccion = transaccion, colorFondo = colorFondo)
            TransactionAmount(transaccion.monto)
        }
    }
}

@Composable
private fun TransactionInfo(
    categoria: CategoriaDto?,
    transaccion: TransaccionDto,
    colorFondo: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(GastoListConstants.CARD_SIZE.dp)
                .clip(CircleShape)
                .background(colorFondo),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = categoria?.icono ?: "❓",
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = categoria?.nombre ?: "Desconocida",
                fontWeight = FontWeight.Bold
            )
            Text(
                transaccion.fecha.format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
                ),
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun TransactionAmount(monto: Double) {
    Text(
        text = "%,.2f RD$".format(monto),
        fontWeight = FontWeight.Bold
    )
}

private fun getFilteredTransactions(
    transacciones: List<TransaccionDto>,
    filtro: String,
    fechaSeleccionada: OffsetDateTime,
    tipoSeleccionado: String
): List<TransaccionDto> {
    val (fechaInicio, fechaFin) = calcularRangoFechas(filtro, fechaSeleccionada)

    return transacciones.filter { transaccion ->
        transaccion.fecha.isAfter(fechaInicio.minusNanos(1)) &&
                transaccion.fecha.isBefore(fechaFin.plusNanos(1)) &&
                transaccion.tipo.equals(tipoSeleccionado, ignoreCase = true)
    }
}

private fun getCategoriaColor(categoria: CategoriaDto?): Color {
    return try {
        Color(android.graphics.Color.parseColor("#${categoria?.colorFondo?.removePrefix("#") ?: "CCCCCC"}"))
    } catch (e: Exception) {
        Color.Gray
    }
}

@Composable
fun ToggleTextButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Text(
        text = text,
        color = if (isSelected) Color.White else Color.Black,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(if (isSelected) Color(0xFF8BC34A) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun GastoPieChart(
    transacciones: List<TransaccionDto>,
    categorias: List<CategoriaDto>,
    modifier: Modifier = Modifier
        .size(200.dp)
        .padding(16.dp)
) {
    if (transacciones.isEmpty()) return

    val total = transacciones.sumOf { it.monto }
    val transaccionesPorCategoria = transacciones.groupBy { it.categoriaId }

    val categoriaColores = categorias.associate { categoria ->
        val hexColor = categoria.colorFondo.removePrefix("#")
        val colorInt = try {
            android.graphics.Color.parseColor("#$hexColor")
        } catch (e: IllegalArgumentException) {
            android.graphics.Color.GRAY
        }
        categoria.categoriaId to Color(colorInt)
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.minDimension / 2
            var startAngle = -90f

            transaccionesPorCategoria.forEach { (categoriaId, lista) ->
                val sweep = (lista.sumOf { it.monto } / total * 360f).toFloat()
                drawArc(
                    color = categoriaColores[categoriaId] ?: Color.Gray,
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = true
                )
                startAngle += sweep
            }

            drawCircle(
                color = Color.White,
                radius = radius * 0.6f
            )
        }

        Text(
            text = "${"%,.2f".format(total)} RD$",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        )
    }
}

@Composable
private fun GastoFiltroBar(
    filtroSeleccionado: String,
    onFiltroChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF0F0F0), RoundedCornerShape(24.dp))
            .border(1.dp, Color.LightGray, RoundedCornerShape(24.dp))
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        listOf("Día", "Semana", "Mes", "Año").forEach { filtro ->
            ToggleTextButton(
                text = filtro,
                isSelected = filtroSeleccionado == filtro,
                onClick = { onFiltroChange(filtro) }
            )
        }
    }
}

@Composable
private fun TipoSelector(
    tipoSeleccionado: String,
    onTipoChange: (String) -> Unit,
    total: Double
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = { onTipoChange("Gasto") },
            colors = if (tipoSeleccionado == "Gasto")
                ButtonDefaults.buttonColors(containerColor = Color(0xFF85D844))
            else
                ButtonDefaults.buttonColors(containerColor = Color.LightGray),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text("Gastos", fontSize = 12.sp, color = if (tipoSeleccionado == "Gasto") Color.White else Color.Black)
        }

        Column(
            modifier = Modifier.weight(2f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Total", fontSize = 14.sp, color = Color.Gray)
            Text(
                text = "%,.0f RD$".format(total),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
            )
        }

        Button(
            onClick = { onTipoChange("Ingreso") },
            colors = if (tipoSeleccionado == "Ingreso")
                ButtonDefaults.buttonColors(containerColor = Color(0xFF85D844))
            else
                ButtonDefaults.buttonColors(containerColor = Color.LightGray),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text("Ingresos", fontSize = 12.sp, color = if (tipoSeleccionado == "Ingreso") Color.White else Color.Black)
        }
    }
}

fun calcularRangoFechas(filtro: String, fechaSeleccionada: OffsetDateTime): Pair<OffsetDateTime, OffsetDateTime> {
    return when (filtro) {
        "Día" -> {
            val inicio = fechaSeleccionada.withHour(0).withMinute(0).withSecond(0).withNano(0)
            val fin = inicio.plusDays(1).minusNanos(1)
            inicio to fin
        }
        "Semana" -> {
            val inicio = fechaSeleccionada.with(DayOfWeek.MONDAY).withHour(0).withMinute(0).withSecond(0).withNano(0)
            val fin = inicio.plusDays(7).minusNanos(1)
            inicio to fin
        }
        "Mes" -> {
            val inicio = fechaSeleccionada.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0)
            val fin = inicio.plusMonths(1).minusNanos(1)
            inicio to fin
        }
        "Año" -> {
            val inicio = fechaSeleccionada.withDayOfYear(1).withHour(0).withMinute(0).withSecond(0).withNano(0)
            val fin = inicio.plusYears(1).minusNanos(1)
            inicio to fin
        }
        else -> fechaSeleccionada to fechaSeleccionada
    }
}

@Composable
fun FechaTexto(
    filtro: String,
    fechaActual: OffsetDateTime,
    onFechaSeleccionada: (OffsetDateTime) -> Unit
) {
    var mostrarDatePicker by remember { mutableStateOf(false) }

    val fechaTexto = when (filtro) {
        "Día" -> "${fechaActual.dayOfMonth} ${fechaActual.month.getDisplayName(ThreeTextStyle.FULL, Locale("es")).replaceFirstChar { it.uppercase() }} ${fechaActual.year}"
        "Semana" -> {
            val inicioSemana = fechaActual.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val finSemana = inicioSemana.plusDays(6)
            val formatter = DateTimeFormatter.ofPattern("d 'de' MMM", Locale("es"))
            "${inicioSemana.format(formatter)} al ${finSemana.format(formatter)}"
        }
        "Mes" -> "${fechaActual.month.getDisplayName(ThreeTextStyle.FULL, Locale("es")).replaceFirstChar { it.uppercase() }} ${fechaActual.year}"
        "Año" -> "${fechaActual.year}"
        else -> ""
    }

    Text(
        text = fechaTexto,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { mostrarDatePicker = true },
        textAlign = TextAlign.Center
    )

    if (mostrarDatePicker) {
        when (filtro) {
            "Día", "Semana" -> {
                CalendarioDialog(
                    mesInicial = YearMonth.of(fechaActual.year, fechaActual.monthValue),
                    onFechaSeleccionada = { localDate ->
                        val fechaOffset = localDate
                            .atStartOfDay(ZoneId.systemDefault())
                            .toOffsetDateTime()
                        val fechaFinal = if (filtro == "Semana") {
                            fechaOffset.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                        } else {
                            fechaOffset
                        }
                        onFechaSeleccionada(fechaFinal)
                        mostrarDatePicker = false
                    },
                    onDismiss = { mostrarDatePicker = false }
                )
            }
            "Mes" -> {
                MonthPickerDialogThreeTen(
                    mesInicial = fechaActual.monthValue,
                    añoInicial = fechaActual.year,
                    onMonthSelected = { mes, año ->
                        val newOd = fechaActual.withMonth(mes).withYear(año)
                        onFechaSeleccionada(newOd)
                        mostrarDatePicker = false
                    },
                    onDismiss = { mostrarDatePicker = false }
                )
            }
            "Año" -> {
                YearPickerDialog(
                    añoInicial = fechaActual.year,
                    onYearSelected = { año ->
                        onFechaSeleccionada(fechaActual.withYear(año))
                        mostrarDatePicker = false
                    },
                    onDismiss = { mostrarDatePicker = false }
                )
            }
        }
    }
}

@Composable
fun CalendarioDialog(
    mesInicial: YearMonth = YearMonth.now(),
    onFechaSeleccionada: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    var mesActual by remember { mutableStateOf(mesInicial) }
    var fechaSeleccionada by remember { mutableStateOf<LocalDate?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar día") },
        text = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { mesActual = mesActual.minusMonths(1) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Mes anterior")
                    }
                    Text(
                        text = mesActual.month.getDisplayName(ThreeTextStyle.FULL, Locale("es"))
                            .replaceFirstChar { it.uppercase() } + " ${mesActual.year}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    IconButton(onClick = { mesActual = mesActual.plusMonths(1) }) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "Mes siguiente")
                    }
                }

                val diasSemana = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    diasSemana.forEach { dia ->
                        Text(
                            text = dia,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                val primerDiaMes = mesActual.atDay(1)
                val ultimoDiaMes = mesActual.atEndOfMonth()

                val primerDiaSemanaMes =
                    (primerDiaMes.dayOfWeek.value + 6) % 7
                val diasDelMes = ultimoDiaMes.dayOfMonth

                val totalCeldasMin = primerDiaSemanaMes + diasDelMes
                val totalCeldas =
                    if (totalCeldasMin % 7 == 0) totalCeldasMin else ((totalCeldasMin / 7) + 1) * 7

                val diasEnGrilla = (0 until totalCeldas).map { index ->
                    val dia = index - primerDiaSemanaMes + 1
                    if (index < primerDiaSemanaMes || dia > diasDelMes) null else dia
                }

                Column {
                    diasEnGrilla.chunked(7).forEach { semana ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            semana.forEach { dia ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .padding(4.dp)
                                        .clickable(enabled = dia != null) {
                                            dia?.let {
                                                val fecha = mesActual.atDay(it)
                                                fechaSeleccionada = fecha
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (dia != null) {
                                        val isSelected =
                                            fechaSeleccionada == mesActual.atDay(dia)
                                        Text(
                                            text = dia.toString(),
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Unspecified,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            modifier = if (isSelected) Modifier
                                                .background(
                                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                                    shape = CircleShape
                                                )
                                                .padding(8.dp)
                                            else Modifier.padding(8.dp),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    fechaSeleccionada?.let { onFechaSeleccionada(it) }
                    onDismiss()
                },
                enabled = fechaSeleccionada != null
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

fun obtenerFiltroPorFecha(fechaTransaccion: OffsetDateTime, fechaActual: OffsetDateTime): String {
    val hoy = fechaActual.toLocalDate()
    val fecha = fechaTransaccion.toLocalDate()

    val inicioSemana = hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val finSemana = inicioSemana.plusDays(6)

    return when {
        fecha == hoy || fecha.isAfter(hoy) -> "Día"
        fecha.isBefore(hoy) && fecha in inicioSemana..finSemana -> "Semana"
        else -> "Otra"
    }
}

@Composable
fun MonthPickerDialogThreeTen(
    mesInicial: Int,
    añoInicial: Int,
    onMonthSelected: (mes: Int, año: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val meses = Month.values().map {
        it.getDisplayName(ThreeTextStyle.FULL, Locale("es")).replaceFirstChar { c -> c.uppercase() }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar mes") },
        text = {
            LazyColumn {
                items(meses.indices.toList()) { index ->
                    Text(
                        text = "${meses[index]} $añoInicial",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onMonthSelected(index + 1, añoInicial)
                                onDismiss()
                            }
                            .padding(12.dp)
                    )
                }
            }
        },
        confirmButton = {}
    )
}

@Composable
fun YearPickerDialog(
    añoInicial: Int,
    onYearSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val años = (1900..OffsetDateTime.now().year).toList().reversed()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar año") },
        text = {
            LazyColumn {
                items(años) { año ->
                    Text(
                        text = año.toString(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onYearSelected(año)
                                onDismiss()
                            }
                            .padding(12.dp)
                    )
                }
            }
        },
        confirmButton = {}
    )
}

@Composable
private fun MensajeNoHayTransacciones(filtro: String, tipo: String) {
    val mensajeNoHay = when (filtro) {
        "Día" -> if (tipo == "Gasto") "No hubo gastos este día" else "No hubo ingresos este día"
        "Semana" -> if (tipo == "Gasto") "No hubo gastos esta semana" else "No hubo ingresos esta semana"
        "Mes" -> if (tipo == "Gasto") "No hubo gastos este mes" else "No hubo ingresos este mes"
        "Año" -> if (tipo == "Gasto") "No hubo gastos este año" else "No hubo ingresos este año"
        else -> "No hay datos"
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val outerRadius = size.minDimension / 2
                val innerRadius = outerRadius * 0.6f

                drawCircle(color = Color(0xFFB0BEC5), radius = outerRadius)

                drawContext.canvas.nativeCanvas.apply {
                    val paint = android.graphics.Paint().apply {
                        color = android.graphics.Color.DKGRAY
                        style = android.graphics.Paint.Style.STROKE
                        strokeWidth = 8f
                        pathEffect = android.graphics.DashPathEffect(
                            floatArrayOf(10f, 10f),
                            0f
                        )
                        isAntiAlias = true
                    }
                    drawCircle(center.x, center.y, innerRadius, paint)
                }
            }

            Text(
                text = mensajeNoHay,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun BottomNavigationBar(navController: NavController, usuarioId: Int) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        NavigationBarItem(
            selected = true,
            onClick = { navController.navigate("gastos") },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("chatIA/$usuarioId") },
            icon = { Icon(Icons.Default.Assistant, contentDescription = "IA Asesor") },
            label = { Text("IA Asesor") }
        )
        NavigationBarItem(
            selected = false,
            onClick = {
                navController.navigate("metaahorros/$usuarioId") {
                    launchSingleTop = true
                    restoreState = true
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                }
            },
            icon = { Icon(Icons.Default.Star, contentDescription = "Metas") },
            label = { Text("Metas") }
        )
    }
}