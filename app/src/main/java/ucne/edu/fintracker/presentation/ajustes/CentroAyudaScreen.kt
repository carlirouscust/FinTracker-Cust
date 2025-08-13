package ucne.edu.fintracker.presentation.ajustes

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CentroAyudaScreen(
    navController: NavController,
    usuarioId: Int
) {
    var textoBusqueda by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Centro de Ayuda",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Atrás",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                NavigationBarItem(
                    selected = currentRoute == "gastos",
                    onClick = { navController.navigate("gastos") },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )

                NavigationBarItem(
                    selected = currentRoute == "chatIA",
                    onClick = { navController.navigate("chatIA/$usuarioId") },
                    icon = { Icon(Icons.Default.Assistant, contentDescription = "IA Asesor") },
                    label = { Text("IA Asesor") }
                )

                NavigationBarItem(
                    selected = currentRoute == "metaahorros/$usuarioId",
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = textoBusqueda,
                onValueChange = { textoBusqueda = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                placeholder = {
                    Text(
                        text = "Buscar en el Centro de Ayuda",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                singleLine = true
            )

            Text(
                text = "Preguntas frecuentes",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            val preguntas = listOf(
                "¿Cómo puedo crear un presupuesto?",
                "¿Cómo funciona el asesor financiero?",
                "¿Cómo puedo vincular mis cuentas bancarias?",
                "¿Qué tipos de inversiones puedo hacer?",
                "¿Cómo puedo contactar al soporte técnico?"
            )

            preguntas.forEach { pregunta ->
                PreguntaFrecuente(
                    pregunta = pregunta,
                    respuesta = obtenerRespuesta(pregunta)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun PreguntaFrecuente(
    pregunta: String,
    respuesta: String
) {
    var expandido by remember { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (expandido) 180f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "rotation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expandido = !expandido },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = pregunta,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = if (expandido) "Contraer" else "Expandir",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.rotate(rotationAngle)
                )
            }

            if (expandido) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    thickness = 1.dp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = respuesta,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
fun obtenerRespuesta(pregunta: String): String {
    return when (pregunta) {
        "¿Cómo puedo crear un presupuesto?" ->
            "Para crear un presupuesto, ve a la sección de 'Metas' en la aplicación. Allí podrás establecer límites de gasto por categoría y definir objetivos de ahorro mensuales."

        "¿Cómo funciona el asesor financiero?" ->
            "Nuestro asesor financiero con IA analiza tus patrones de gasto y te proporciona recomendaciones personalizadas para mejorar tu salud financiera. Puedes acceder desde la pestaña 'IA Asesor'."

        "¿Cómo puedo vincular mis cuentas bancarias?" ->
            "Actualmente puedes agregar transacciones manualmente. En el futuro implementaremos la funcionalidad de vincular cuentas bancarias directamente para sincronización automática."

        "¿Qué tipos de inversiones puedo hacer?" ->
            "La aplicación te permite registrar diferentes tipos de inversiones como acciones, bonos, fondos mutuos y criptomonedas. También puedes establecer metas de inversión a largo plazo."

        "¿Cómo puedo contactar al soporte técnico?" ->
            "Puedes contactar al soporte técnico desde la sección de 'Ajustes' > 'Soporte', donde encontrarás opciones para enviar un mensaje o programar una llamada con nuestro equipo."

        else -> "Respuesta no disponible para esta pregunta."
    }
}