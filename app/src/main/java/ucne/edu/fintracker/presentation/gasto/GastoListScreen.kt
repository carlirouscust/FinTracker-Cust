package ucne.edu.fintracker.presentation.gasto

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import org.threeten.bp.format.DateTimeFormatter
import ucne.edu.fintracker.presentation.categoria.CategoriaViewModel
import ucne.edu.fintracker.presentation.components.MenuScreen
import ucne.edu.fintracker.presentation.remote.dto.CategoriaDto
import ucne.edu.fintracker.presentation.remote.dto.TransaccionDto

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
    // Agrupa las transacciones por el id de la categoría
    val transaccionesPorCategoria = transacciones.groupBy { it.categoriaId }
    // Asigna colores a las categorías (si hay más categorías que colores, se repiten)
    val colores = listOf(
        Color(0xFF4CAF50), Color(0xFFFF9800), Color(0xFF03A9F4),
        Color(0xFFF44336), Color(0xFF9C27B0), Color(0xFF009688)
    )
    val categoriaIds = transaccionesPorCategoria.keys.toList()
    val categoriaColores = categoriaIds.mapIndexed { i, id -> id to colores[i % colores.size] }.toMap()

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

    Spacer(modifier = Modifier.height(12.dp))
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        transaccionesPorCategoria.forEach { (categoriaId, _) ->
            val categoria = categorias.find { it.categoriaId == categoriaId }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            color = categoriaColores[categoriaId] ?: Color.Gray,
                            shape = CircleShape
                        )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = categoria?.nombre ?: "Desconocida")
            }
        }
    }
}


// --- PANTALLA PRINCIPAL ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GastoListScreen(
    viewModel: GastoViewModel,
    categoriaViewModel: CategoriaViewModel,
    onNuevoClick: () -> Unit,
    navController: NavController
) {
    val state by viewModel.uiState.collectAsState()
    val categoriaState by categoriaViewModel.uiState.collectAsState()
    var tipo by remember { mutableStateOf("Gasto") }
    val transaccionesFiltradas = state.transacciones.filter { it.tipo == tipo }
    val total = transaccionesFiltradas.sumOf { it.monto }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    MenuScreen(
        drawerState = drawerState,
        navController = navController,
        content = {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Balance", fontSize = 14.sp, color = Color.Gray)
                                Text(
                                    text = "%,.0f RD$".format(total),
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF4CAF50)
                                    )
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch { drawerState.open() }
                            }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        },
                        actions = {
                            IconButton(onClick = { /* perfil */ }) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color.Gray)
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            navController.navigate("gasto_nuevo/$tipo")
                        },
                        containerColor = Color(0xFF8BC34A),
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
                },
                bottomBar = {
                    NavigationBar(containerColor = Color.White) {
                        NavigationBarItem(
                            selected = true,
                            onClick = { navController.navigate("gastos") },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                            label = { Text("Home") }
                        )
                        NavigationBarItem(
                            selected = false,
                            onClick = { /* otra ruta */ },
                            icon = { Icon(Icons.Default.Assistant, contentDescription = "IA Asesor") },
                            label = { Text("IA Asesor") }
                        )
                        NavigationBarItem(
                            selected = false,
                            onClick = { /* otra ruta */ },
                            icon = { Icon(Icons.Default.Star, contentDescription = "Metas") },
                            label = { Text("Metas") }
                        )
                    }
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .padding(padding)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    // Botones tipo
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { tipo = "Gasto" },
                            colors = if (tipo == "Gasto")
                                ButtonDefaults.buttonColors(containerColor = Color(0xFF85D844))
                            else
                                ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                "Gastos",
                                color = if (tipo == "Gasto") Color.White else Color.Black
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { tipo = "Ingreso" },
                            colors = if (tipo == "Ingreso")
                                ButtonDefaults.buttonColors(containerColor = Color(0xFF85D844))
                            else
                                ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                "Ingresos",
                                color = if (tipo == "Ingreso") Color.White else Color.Black
                            )
                        }
                    }

                    // Filtros
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
                                isSelected = state.filtro == filtro,
                                onClick = { viewModel.cambiarFiltro(filtro) }
                            )
                        }
                    }

                    Text(
                        text = "Julio 2025",
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 16.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )

                    if (transaccionesFiltradas.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .size(200.dp)
                                .align(Alignment.CenterHorizontally),
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
                                        pathEffect = android.graphics.DashPathEffect(floatArrayOf(10f, 10f), 0f)
                                        isAntiAlias = true
                                    }
                                    drawCircle(center.x, center.y, innerRadius, paint)
                                }
                            }

                            Text(
                                text = "No hubo\ngastos esta\nsemana",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        GastoPieChart(
                            transacciones = transaccionesFiltradas,
                            categorias = categoriaState.categorias, // <-- pasa aquí tu lista de categorías
                            modifier = Modifier
                                .size(200.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(transaccionesFiltradas) { transaccion ->
                            val categoria = categoriaState.categorias.find { it.categoriaId == transaccion.categoriaId }
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(text = categoria?.nombre ?: "Desconocida", fontWeight = FontWeight.Bold)
                                        Text(
                                            transaccion.fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                            color = Color.Gray
                                        )
                                    }
                                    Text("%,.2f RD$".format(transaccion.monto), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}