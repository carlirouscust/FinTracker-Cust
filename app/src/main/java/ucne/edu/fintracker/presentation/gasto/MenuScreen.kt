package ucne.edu.fintracker.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

data class DrawerItem(val label: String, val icon: ImageVector)

val drawerItems = listOf(
    DrawerItem("Inicio", Icons.Default.Home),
    DrawerItem("Categorías", Icons.Default.Category),
    DrawerItem("Gráfico", Icons.Default.PieChart),
    DrawerItem("Pagos recurrentes", Icons.Default.Schedule),
    DrawerItem("Límites de gastos", Icons.Default.Warning),
    DrawerItem("Ajustes", Icons.Default.Settings)
)

@Composable
fun MenuScreen(
    drawerState: DrawerState,
    navController: NavController,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .background(Color.White)
                    .widthIn(max = 280.dp)
            ) {
                // Encabezado del Drawer
                TextButton(
                    onClick = { /* Acción perfil */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(Color.White)
                ) {
                    Text(
                        text = "👤 Cliente: Juan Pérez",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Divider()

                // Items del Drawer
                drawerItems.forEach { item ->
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = Color.Black
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                color = Color.Black
                            )
                        },
                        selected = false,
                        onClick = {
                            // Cerramos el drawer primero
                            scope.launch { drawerState.close() }

                            // Navegación según el label
                            when (item.label) {
                                "Inicio" -> navController.navigate("gastos")
                                "Categorías" -> navController.navigate("categorias")
                                // puedes agregar otros destinos más adelante
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        },
        content = content
    )
}
