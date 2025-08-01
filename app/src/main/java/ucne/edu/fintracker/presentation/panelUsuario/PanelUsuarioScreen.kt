package ucne.edu.fintracker.presentation.panelUsuario

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.launch
import ucne.edu.fintracker.presentation.components.MenuScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PanelUsuarioScreen(
    navController: NavController,
    usuarioId: Int,
    nombreUsuario: String = "Sofia Rodriguez",
    emailUsuario: String = "sofia.rodriguez@gmail.com",
    saldoTotal: Double = 30795.0,
    onCambiarFoto: () -> Unit = {},
    onCambiarContrasena: () -> Unit = {},
    onDivisa: () -> Unit = {},
    onAjustes: () -> Unit = {},
    onTransacciones: () -> Unit = {}
) {
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
                            Text(
                                text = "Mi Cuenta",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                ),
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(
                                    Icons.Default.Menu,
                                    contentDescription = "Menu",
                                    tint = Color.Black
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.White
                        )
                    )
                },
                bottomBar = {
                    NavigationBar(
                        containerColor = Color.White,
                        tonalElevation = 8.dp
                    ) {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentRoute = navBackStackEntry?.destination?.route

                        NavigationBarItem(
                            selected = currentRoute == "gastos",
                            onClick = { navController.navigate("gastos") },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                            label = { Text("Home", fontSize = 10.sp) }
                        )

                        NavigationBarItem(
                            selected = currentRoute == "chatIA",
                            onClick = { navController.navigate("chatIA/$usuarioId") },
                            icon = { Icon(Icons.Default.Assistant, contentDescription = "IA Asesor") },
                            label = { Text("IA Asesor", fontSize = 10.sp) }
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
                            label = { Text("Metas", fontSize = 10.sp) }
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
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(32.dp))

                    // Foto de perfil con imagen realista
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8D5C4)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Simulamos una foto de perfil femenina
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Color(0xFFD4A574),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Foto de perfil",
                                modifier = Modifier.size(60.dp),
                                tint = Color(0xFFA67C52)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Nombre del usuario
                    Text(
                        text = nombreUsuario,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        ),
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Usuario Premium
                    Text(
                        text = "Usuario Premium",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp
                        ),
                        color = Color(0xFF6B7280)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Email
                    Text(
                        text = "Email: $emailUsuario",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp
                        ),
                        color = Color(0xFF6B7280)
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    // Sección Resumen Financiero
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Resumen Financiero",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.Black,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        )

                        // Saldo Total
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Saldo Total",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.Black
                                )
                                Text(
                                    text = "Saldo total en todas las cuentas",
                                    fontSize = 12.sp,
                                    color = Color(0xFF6B7280)
                                )
                            }
                            Text(
                                text = "${String.format("%,.0f", saldoTotal)} RD$",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Transacciones
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onTransacciones() }
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Transacciones",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.Black
                                )
                                Text(
                                    text = "Transacciones recientes",
                                    fontSize = 12.sp,
                                    color = Color(0xFF6B7280)
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Ver transacciones",
                                tint = Color(0xFF6B7280),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // Sección Opciones
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Opciones",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.Black,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        )

                        // Cambiar Foto de Perfil
                        OpcionItem(
                            titulo = "Cambiar Foto de Perfil",
                            icono = Icons.Default.PhotoCamera,
                            onClick = onCambiarFoto,
                            showArrow = false
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Cambiar Contraseña
                        OpcionItem(
                            titulo = "Cambiar Contraseña",
                            icono = Icons.Default.Lock,
                            onClick = onCambiarContrasena
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Divisa
                        OpcionItem(
                            titulo = "Divisa",
                            icono = Icons.Default.AttachMoney,
                            onClick = onDivisa
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Ajustes
                        OpcionItem(
                            titulo = "Ajustes",
                            icono = Icons.Default.Settings,
                            onClick = { navController.navigate("ajustes/$usuarioId") }
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    )
}

@Composable
fun OpcionItem(
    titulo: String,
    icono: ImageVector,
    onClick: () -> Unit,
    showArrow: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icono,
                contentDescription = titulo,
                tint = Color(0xFF6B7280),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = titulo,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = Color.Black
            )
        }

        if (showArrow) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Ir a $titulo",
                tint = Color(0xFF6B7280),
                modifier = Modifier.size(20.dp)
            )
        } else {
            // Para "Cambiar Foto de Perfil" mostramos el ícono de cámara
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "Cambiar foto",
                tint = Color(0xFF6B7280),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}