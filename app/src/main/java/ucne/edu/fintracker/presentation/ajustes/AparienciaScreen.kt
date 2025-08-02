package ucne.edu.fintracker.presentation.ajustes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Assistant
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AparienciaScreen(
    navController: NavController,
    usuarioId: Int
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Apariencia",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
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
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            AparienciaSeccion(titulo = "Tema") {
                var temaSeleccionado by remember { mutableStateOf("Claro") }

                OpcionTema(
                    titulo = "Claro",
                    isSelected = temaSeleccionado == "Claro",
                    onSelected = { temaSeleccionado = "Claro" }
                )
                OpcionTema(
                    titulo = "Oscuro",
                    isSelected = temaSeleccionado == "Oscuro",
                    onSelected = { temaSeleccionado = "Oscuro" }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            AparienciaSeccion(titulo = "Idioma") {
                SelectorIdioma()
            }
        }
    }
}

@Composable
fun AparienciaSeccion(titulo: String, contenido: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = titulo,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        contenido()
    }
}

@Composable
fun OpcionTema(
    titulo: String,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = titulo,
            color = Color.Black,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp
        )
        RadioButton(
            selected = isSelected,
            onClick = onSelected,
            colors = RadioButtonDefaults.colors(
                selectedColor = Color(0xFF85D844),
                unselectedColor = Color.Gray
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorIdioma() {
    var expanded by remember { mutableStateOf(false) }
    var selectedIdioma by remember { mutableStateOf("Español") }
    val idiomas = listOf("Español", "English", "Français", "Português")

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedIdioma,
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown",
                    tint = Color.Gray
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Gray,
                unfocusedBorderColor = Color.Gray,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            idiomas.forEach { idioma ->
                DropdownMenuItem(
                    text = { Text(idioma) },
                    onClick = {
                        selectedIdioma = idioma
                        expanded = false
                    }
                )
            }
        }
    }
}
