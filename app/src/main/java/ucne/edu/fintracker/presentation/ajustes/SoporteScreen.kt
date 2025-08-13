package ucne.edu.fintracker.presentation.ajustes

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import ucne.edu.fintracker.presentation.panelUsuario.PanelUsuarioViewModel

data class FormField(
    val label: String,
    val value: String,
    val placeholder: String,
    val onValueChange: (String) -> Unit,
    val keyboardType: KeyboardType = KeyboardType.Text,
    val imeAction: ImeAction = ImeAction.Next,
    val isReadOnly: Boolean = false,
    val maxLines: Int = 1,
    val height: Int? = null
)

data class NavigationItem(
    val route: String,
    val icon: ImageVector,
    val label: String,
    val navigationAction: () -> Unit
)

data class AlertMessage(
    val message: String,
    val icon: ImageVector,
    val backgroundColor: Color,
    val contentColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoporteScreen(
    navController: NavController,
    usuarioId: Int,
    viewModel: SoporteViewModel = hiltViewModel(),
    panelUsuarioViewModel: PanelUsuarioViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val panelUiState by panelUsuarioViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(usuarioId) {
        if (usuarioId != 0) {
            panelUsuarioViewModel.cargarUsuario(usuarioId)
        }
    }

    LaunchedEffect(panelUiState.usuario?.email) {
        panelUiState.usuario?.email?.let { correo ->
            viewModel.cargarDatosUsuario(correo)
        }
    }

    Scaffold(
        topBar = { SoporteTopBar(onBackClick = { navController.popBackStack() }) },
        bottomBar = { SoporteBottomBar(navController, usuarioId) },
        snackbarHost = { SoporteSnackbarHost(uiState.mensajeEnviado) }
    ) { paddingValues ->
        SoporteContent(
            paddingValues = paddingValues,
            panelUiState = panelUiState,
            uiState = uiState,
            viewModel = viewModel
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SoporteTopBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "Soporte",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
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
}

@Composable
private fun SoporteBottomBar(navController: NavController, usuarioId: Int) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        val navigationItems = createNavigationItems(navController, usuarioId)

        navigationItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = item.navigationAction,
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}

@Composable
private fun SoporteSnackbarHost(mensajeEnviado: Boolean) {
    if (mensajeEnviado) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            AlertCard(
                AlertMessage(
                    message = "Mensaje enviado exitosamente",
                    icon = Icons.Default.CheckCircle,
                    backgroundColor = Color(0xFF4CAF50),
                    contentColor = Color.White
                )
            )
        }
    }
}

@Composable
private fun SoporteContent(
    paddingValues: PaddingValues,
    panelUiState: ucne.edu.fintracker.presentation.panelUsuario.PanelUsuarioUiState,
    uiState: SoporteUiState,
    viewModel: SoporteViewModel
) {
    when {
        panelUiState.isLoading -> {
            LoadingState(paddingValues)
        }

        panelUiState.isError -> {
            ErrorState(paddingValues)
        }

        else -> {
            SoporteForm(paddingValues, uiState, viewModel)
        }
    }
}

@Composable
private fun LoadingState(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(paddingValues: PaddingValues) {
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
                text = "Error al cargar datos del usuario",
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SoporteForm(
    paddingValues: PaddingValues,
    uiState: SoporteUiState,
    viewModel: SoporteViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(paddingValues)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val formFields = createFormFields(uiState, viewModel)

        formFields.forEach { field ->
            FormFieldComponent(field)
        }

        Spacer(modifier = Modifier.weight(1f))

        if (uiState.isError) {
            AlertCard(
                AlertMessage(
                    message = uiState.errorMessage,
                    icon = Icons.Default.Error,
                    backgroundColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            )
        }

        SubmitButton(
            isLoading = uiState.isLoading,
            onClick = {
                viewModel.limpiarError()
                viewModel.enviarMensaje()
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun FormFieldComponent(field: FormField) {
    Text(
        text = field.label,
        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
        color = MaterialTheme.colorScheme.onBackground
    )

    OutlinedTextField(
        value = field.value,
        onValueChange = field.onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .let { modifier ->
                field.height?.let { height ->
                    modifier.height(height.dp)
                } ?: modifier
            },
        placeholder = {
            Text(
                text = field.placeholder,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = field.keyboardType,
            imeAction = field.imeAction
        ),
        singleLine = field.maxLines == 1,
        maxLines = field.maxLines,
        readOnly = field.isReadOnly
    )
}

@Composable
private fun AlertCard(alertMessage: AlertMessage) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = alertMessage.backgroundColor
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = alertMessage.icon,
                contentDescription = null,
                tint = alertMessage.contentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = alertMessage.message,
                color = alertMessage.contentColor,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (alertMessage.backgroundColor == Color(0xFF4CAF50)) {
                    FontWeight.Medium
                } else {
                    FontWeight.Normal
                }
            )
        }
    }
}

@Composable
private fun SubmitButton(
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        enabled = !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF8BC34A)
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        if (isLoading) {
            ButtonLoadingContent()
        } else {
            ButtonDefaultContent()
        }
    }
}

@Composable
private fun ButtonLoadingContent() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(16.dp),
            color = Color.White,
            strokeWidth = 2.dp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Enviando...",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ButtonDefaultContent() {
    Text(
        text = "Enviar",
        color = Color.White,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium
    )
}

private fun createNavigationItems(
    navController: NavController,
    usuarioId: Int
): List<NavigationItem> {
    return listOf(
        NavigationItem(
            route = "gastos",
            icon = Icons.Default.Home,
            label = "Home",
            navigationAction = { navController.navigate("gastos") }
        ),
        NavigationItem(
            route = "chatIA",
            icon = Icons.Default.Assistant,
            label = "IA Asesor",
            navigationAction = { navController.navigate("chatIA/$usuarioId") }
        ),
        NavigationItem(
            route = "metaahorros/$usuarioId",
            icon = Icons.Default.Star,
            label = "Metas",
            navigationAction = {
                navController.navigate("metaahorros/$usuarioId") {
                    launchSingleTop = true
                    restoreState = true
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                }
            }
        )
    )
}

private fun createFormFields(
    uiState: SoporteUiState,
    viewModel: SoporteViewModel
): List<FormField> {
    return listOf(
        FormField(
            label = "Asunto",
            value = uiState.asunto,
            placeholder = "Ingresa el asunto",
            onValueChange = viewModel::onAsuntoChange,
            imeAction = ImeAction.Next
        ),
        FormField(
            label = "Correo electrónico",
            value = uiState.correoElectronico,
            placeholder = "",
            onValueChange = viewModel::onCorreoElectronicoChange,
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
            isReadOnly = true
        ),
        FormField(
            label = "Mensaje",
            value = uiState.mensaje,
            placeholder = "Describe tu consulta o problema...",
            onValueChange = viewModel::onMensajeChange,
            imeAction = ImeAction.Default,
            maxLines = 8,
            height = 200
        )
    )
}