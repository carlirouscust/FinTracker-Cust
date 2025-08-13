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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import ucne.edu.fintracker.presentation.theme.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AparienciaScreen(
    navController: NavController,
    usuarioId: Int,
    themeViewModel: ThemeViewModel = hiltViewModel()
) {
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

    Scaffold(
        topBar = { MetaTopBar("Apariencia") { navController.popBackStack() } },
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
            Seccion("Tema") {
                FilaOpcion("Claro", isSelected = !isDarkTheme) { themeViewModel.toggleTheme(false) }
                FilaOpcion("Oscuro", isSelected = isDarkTheme) { themeViewModel.toggleTheme(true) }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Seccion("Idioma") {
                SelectorIdioma()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetaTopBar(title: String, onBackClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = MaterialTheme.colorScheme.onSurface)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
    )
}

data class NavItem(val route: String, val icon: ImageVector, val label: String)

@Composable
fun NavegacionInferior(navController: NavController, items: List<NavItem>) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { navController.navigate(item.route) { launchSingleTop = true; restoreState = true; popUpTo(navController.graph.startDestinationId) { saveState = true } } },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}

@Composable
fun Seccion(titulo: String, contenido: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = titulo,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        contenido()
    }
}

@Composable
fun FilaOpcion(titulo: String, isSelected: Boolean, onSelected: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = titulo,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp
        )
        RadioButton(
            selected = isSelected,
            onClick = onSelected,
            colors = RadioButtonDefaults.colors(
                selectedColor = Color(0xFF8BC34A),
                unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
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

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selectedIdioma,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown", tint = MaterialTheme.colorScheme.onSurface) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            idiomas.forEach { idioma ->
                DropdownMenuItem(
                    text = { Text(idioma, color = MaterialTheme.colorScheme.onSurface) },
                    onClick = { selectedIdioma = idioma; expanded = false }
                )
            }
        }
    }
}
