package ucne.edu.fintracker.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

data class DrawerItem(val label: String, val icon: ImageVector)

val drawerItems = listOf(
    DrawerItem("Inicio", Icons.Default.Home),
    DrawerItem("Gráfico", Icons.Default.PieChart),
    DrawerItem("Pagos recurrentes", Icons.Default.Schedule),
    DrawerItem("Categorías", Icons.Default.Category),
    DrawerItem("Límites de gastos", Icons.Default.Warning),
    DrawerItem("Ajustes", Icons.Default.Settings)
)

@Composable
fun MenuScreen(
    drawerState: DrawerState,
    onItemClick: (String) -> Unit,
    content: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .background(Color.White)
                    .widthIn(max = 280.dp)
            ) {
                TextButton(
                    onClick = { /* ir a perfil */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(Color.White)
                ) {
                    Text("👤 Cliente: Juan Pérez", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                }

                Divider()

                drawerItems.forEach { item ->
                    NavigationDrawerItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = false,
                        onClick = { onItemClick(item.label) },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        },
        content = content
    )
}

