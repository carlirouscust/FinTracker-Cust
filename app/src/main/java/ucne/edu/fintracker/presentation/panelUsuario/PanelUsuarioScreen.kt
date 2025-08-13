package ucne.edu.fintracker.presentation.panelUsuario

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import ucne.edu.fintracker.presentation.components.MenuScreen
import ucne.edu.fintracker.remote.dto.UsuarioDto
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PanelUsuarioScreen(
    navController: NavController,
    usuarioId: Int,
    onCambiarContrasenaClick: () -> Unit,
    onCambiarFoto: () -> Unit = {},
    onDivisa: () -> Unit = {},
    onAjustes: () -> Unit = {},
    onTransacciones: () -> Unit = {},
    viewModel: PanelUsuarioViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(usuarioId) {
        viewModel.cargarUsuario(usuarioId)
    }

    val accionesPantalla = AccionesPantalla(
        onCambiarContrasenaClick = onCambiarContrasenaClick,
        onCambiarFoto = onCambiarFoto,
        onDivisa = onDivisa,
        onAjustes = onAjustes,
        onTransacciones = onTransacciones
    )

    MenuScreen(
        drawerState = drawerState,
        navController = navController,
        usuarioId = usuarioId,
        content = {
            Scaffold(
                topBar = {
                    PanelUsuarioTopBar(
                        onMenuClick = { scope.launch { drawerState.open() } }
                    )
                },
                bottomBar = {
                    PanelUsuarioBottomBar(
                        navController = navController,
                        usuarioId = usuarioId
                    )
                }
            ) { paddingValues ->
                PanelUsuarioContent(
                    uiState = uiState,
                    usuarioId = usuarioId,
                    viewModel = viewModel,
                    paddingValues = paddingValues,
                    accionesPantalla = accionesPantalla
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PanelUsuarioTopBar(onMenuClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "Mi Cuenta",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
private fun PanelUsuarioBottomBar(
    navController: NavController,
    usuarioId: Int
) {
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

@Composable
private fun PanelUsuarioContent(
    uiState: PanelUsuarioUiState,
    usuarioId: Int,
    viewModel: PanelUsuarioViewModel,
    paddingValues: PaddingValues,
    accionesPantalla: AccionesPantalla
) {
    when {
        uiState.isLoading -> EstadoCarga(paddingValues)
        uiState.isError -> EstadoError(
            paddingValues = paddingValues,
            errorMessage = uiState.errorMessage,
            onReintentar = {
                viewModel.limpiarError()
                viewModel.cargarUsuario(usuarioId)
            }
        )
        uiState.usuario != null -> PerfilUsuario(
            usuario = uiState.usuario,
            usuarioId = usuarioId,
            viewModel = viewModel,
            paddingValues = paddingValues,
            accionesPantalla = accionesPantalla
        )
    }
}

@Composable
private fun EstadoCarga(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Cargando datos del usuario...",
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun EstadoError(
    paddingValues: PaddingValues,
    errorMessage: String,
    onReintentar: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "Error",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Error: $errorMessage",
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onReintentar) {
                Text("Reintentar")
            }
        }
    }
}

@Composable
private fun PerfilUsuario(
    usuario: UsuarioDto,
    usuarioId: Int,
    viewModel: PanelUsuarioViewModel,
    paddingValues: PaddingValues,
    accionesPantalla: AccionesPantalla
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(paddingValues)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        SeccionPerfilUsuario(usuario = usuario)

        Spacer(modifier = Modifier.height(40.dp))

        SeccionResumenFinanciero(
            usuario = usuario,
            usuarioId = usuarioId,
            viewModel = viewModel,
            onTransacciones = accionesPantalla.onTransacciones
        )

        Spacer(modifier = Modifier.height(40.dp))

        SeccionOpciones(accionesPantalla = accionesPantalla)

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun SeccionPerfilUsuario(usuario: UsuarioDto) {
    FotoPerfilUsuario(fotoPerfil = usuario.fotoPerfil)

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "${usuario.nombre} ${usuario.apellido}".trim(),
        style = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        ),
        color = MaterialTheme.colorScheme.onBackground
    )

    Spacer(modifier = Modifier.height(4.dp))

    Text(
        text = "Usuario Premium",
        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = "Email: ${usuario.email}",
        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun FotoPerfilUsuario(fotoPerfil: String?) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondaryContainer),
        contentAlignment = Alignment.Center
    ) {
        if (!fotoPerfil.isNullOrEmpty()) {
            AsyncImage(
                model = File(fotoPerfil),
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Foto de perfil",
                modifier = Modifier.size(60.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
private fun SeccionResumenFinanciero(
    usuario: UsuarioDto,
    usuarioId: Int,
    viewModel: PanelUsuarioViewModel,
    onTransacciones: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Resumen Financiero",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        SaldoTotalItem(
            usuario = usuario,
            onActualizarSaldo = { viewModel.actualizarSaldoUsuario(usuarioId) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TransaccionesItem(onTransacciones = onTransacciones)
    }
}

@Composable
private fun SaldoTotalItem(
    usuario: UsuarioDto,
    onActualizarSaldo: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Saldo Total",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onActualizarSaldo,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Actualizar saldo",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Text(
                text = "Saldo calculado en tiempo real",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        SaldoDisplay(usuario = usuario)
    }
}

@Composable
private fun SaldoDisplay(usuario: UsuarioDto) {
    Column(horizontalAlignment = Alignment.End) {
        Text(
            text = "${String.format("%,.2f", usuario.saldoTotal)} ${usuario.divisa.ifEmpty { "RD$" }}",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = if (usuario.saldoTotal >= 0) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.error
            }
        )
        Text(
            text = if (usuario.saldoTotal >= 0) "Saldo positivo" else "Saldo negativo",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TransaccionesItem(onTransacciones: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTransacciones() }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Transacciones",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Transacciones recientes",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = "Ver transacciones",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SeccionOpciones(accionesPantalla: AccionesPantalla) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Opciones",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        val opciones = listOf(
            OpcionConfig("Cambiar Foto de Perfil", Icons.Default.PhotoCamera, accionesPantalla.onCambiarFoto, false),
            OpcionConfig("Cambiar Contraseña", Icons.Default.Lock, accionesPantalla.onCambiarContrasenaClick, true),
            OpcionConfig("Divisa", Icons.Default.AttachMoney, accionesPantalla.onDivisa, true),
            OpcionConfig("Ajustes", Icons.Default.Settings, accionesPantalla.onAjustes, true)
        )

        opciones.forEachIndexed { index, opcion ->
            OpcionItem(
                titulo = opcion.titulo,
                icono = opcion.icono,
                onClick = opcion.onClick,
                showArrow = opcion.showArrow
            )
            if (index < opciones.size - 1) {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
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
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = titulo,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (showArrow) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Ir a $titulo",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "Cambiar foto",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

private data class AccionesPantalla(
    val onCambiarContrasenaClick: () -> Unit,
    val onCambiarFoto: () -> Unit,
    val onDivisa: () -> Unit,
    val onAjustes: () -> Unit,
    val onTransacciones: () -> Unit
)

private data class OpcionConfig(
    val titulo: String,
    val icono: ImageVector,
    val onClick: () -> Unit,
    val showArrow: Boolean
)