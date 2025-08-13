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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material.icons.filled.Assistant
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star

@Composable
fun CentroAyudaScreen(
    navController: NavController,
    usuarioId: Int
) {
    var textoBusqueda by remember { mutableStateOf("") }

    Scaffold(
        topBar = { MetaTopBar("Centro de Ayuda") { navController.popBackStack() } },
        bottomBar = {
            NavegacionInferior(
                navController = navController,
                items = listOf(
                    NavItem("gastos", Icons.Default.Home, "Home"),
                    NavItem("chatIA/$usuarioId", Icons.Default.Assistant, "IA Asesor"),
                    NavItem("metaahorros/$usuarioId", Icons.Default.Star, "Metas")
                )
            )
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
                placeholder = { Text("Buscar en el Centro de Ayuda") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                singleLine = true
            )

            Text(
                text = "Preguntas frecuentes",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
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
                PreguntaFrecuente(pregunta, obtenerRespuesta(pregunta))
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun PreguntaFrecuente(pregunta: String, respuesta: String) {
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(pregunta, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                Icon(Icons.Default.ExpandMore, contentDescription = null, modifier = Modifier.rotate(rotationAngle))
            }
            if (expandido) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider()
                Spacer(modifier = Modifier.height(12.dp))
                Text(respuesta, lineHeight = 20.sp)
            }
        }
    }
}

fun obtenerRespuesta(pregunta: String): String = when (pregunta) {
    "¿Cómo puedo crear un presupuesto?" ->
        "Ve a la sección de 'Metas'. Allí estableces límites de gasto y objetivos de ahorro."
    "¿Cómo funciona el asesor financiero?" ->
        "La IA analiza tus gastos y recomienda mejoras personalizadas."
    "¿Cómo puedo vincular mis cuentas bancarias?" ->
        "Por ahora es manual. En el futuro habrá sincronización automática."
    "¿Qué tipos de inversiones puedo hacer?" ->
        "Puedes registrar acciones, bonos, fondos y criptomonedas."
    "¿Cómo puedo contactar al soporte técnico?" ->
        "En 'Ajustes' > 'Soporte', puedes enviar mensajes o programar llamadas."
    else -> "Respuesta no disponible."
}