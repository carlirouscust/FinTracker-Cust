package ucne.edu.fintracker.presentation.navegation

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ucne.edu.fintracker.presentation.categoria.CategoriaListScreen
import ucne.edu.fintracker.presentation.categoria.CategoriaScreen
import ucne.edu.fintracker.presentation.categoria.CategoriaViewModel
import ucne.edu.fintracker.presentation.gasto.GastoListScreen
import ucne.edu.fintracker.presentation.gasto.GastoScreen
import ucne.edu.fintracker.presentation.login.LoginViewModel
import ucne.edu.fintracker.presentation.login.LoginRegisterScreen
import ucne.edu.fintracker.remote.FinTrackerApi
import ucne.edu.fintracker.remote.dto.TransaccionDto
import androidx.navigation.NavType
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.navArgument
import ucne.edu.fintracker.presentation.ajustes.AjustesListScreen
import ucne.edu.fintracker.presentation.limitegasto.LimiteDetalleScreen
import ucne.edu.fintracker.presentation.asesorIA.ChatIaScreen
import ucne.edu.fintracker.presentation.limitegasto.LimiteScreen
import ucne.edu.fintracker.presentation.limitegasto.LimiteListScreen
import ucne.edu.fintracker.presentation.limitegasto.LimiteViewModel
import ucne.edu.fintracker.presentation.metaahorro.MetaDetalleScreen
import ucne.edu.fintracker.presentation.metaahorro.MetaListScreen
import ucne.edu.fintracker.presentation.metaahorro.MetaScreen
import ucne.edu.fintracker.presentation.metaahorro.MetaViewModel
import ucne.edu.fintracker.presentation.pagorecurrente.PagoDetalleScreen
import ucne.edu.fintracker.presentation.pagorecurrente.PagoListScreen
import ucne.edu.fintracker.presentation.pagorecurrente.PagoScreen
import ucne.edu.fintracker.presentation.pagorecurrente.PagoViewModel
import ucne.edu.fintracker.presentation.gasto.GastoViewModel
import ucne.edu.fintracker.presentation.panelUsuario.PanelUsuarioScreen
import ucne.edu.fintracker.remote.dto.LimiteGastoDto
import ucne.edu.fintracker.remote.dto.MetaAhorroDto
import ucne.edu.fintracker.remote.dto.PagoRecurrenteDto
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import kotlinx.coroutines.launch
import ucne.edu.fintracker.presentation.ajustes.AparienciaScreen
import ucne.edu.fintracker.presentation.ajustes.NotificacionesScreen
import ucne.edu.fintracker.presentation.gasto.GastoDetalleScreen
import ucne.edu.fintracker.presentation.gasto.GraficoScreen
import ucne.edu.fintracker.presentation.login.DataLogin
import ucne.edu.fintracker.presentation.metaahorro.MetaMAhorroScreen
import ucne.edu.fintracker.presentation.panelUsuario.CambiarContrasenaScreen
import ucne.edu.fintracker.presentation.panelUsuario.CambiarFotoScreen
import org.threeten.bp.OffsetDateTime
import ucne.edu.fintracker.presentation.ajustes.CentroAyudaScreen
import ucne.edu.fintracker.presentation.ajustes.SoporteScreen
import ucne.edu.fintracker.presentation.login.ResetPasswordScreen
import ucne.edu.fintracker.presentation.panelUsuario.DivisasScreen

private object NavConstants {
    const val CARGANDO_USUARIO = "Cargando usuario..."
    const val PAGOS_ROUTE = "pagos/{usuarioId}"
    const val SIN_CATEGORIA = "Sin categoría"
}

@Composable
fun FinTrackerNavHost(
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel = hiltViewModel(),
    finTrackerApi: FinTrackerApi = hiltViewModel()
) {
    val context = LocalContext.current
    var pantallaInicial by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val logueado = verificarSesion(context)
        pantallaInicial = if (logueado) "gastos" else "login"
    }

    pantallaInicial?.let { destino ->
        NavHost(
            navController = navHostController,
            startDestination = destino,
            modifier = modifier
        ) {
            setupAuthRoutes(navHostController, loginViewModel)
            setupCategoriaRoutes(navHostController)
            setupGastoRoutes(navHostController)
            setupPagoRoutes(navHostController)
            setupLimiteRoutes(navHostController)
            setupMetaRoutes(navHostController)
            setupSettingsRoutes(navHostController)
            setupMiscRoutes(navHostController)
        }
    }
}

private fun NavGraphBuilder.setupAuthRoutes(
    navHostController: NavHostController,
    loginViewModel: LoginViewModel
) {
    composable("login") {
        LoginRegisterScreen(
            navController = navHostController,
            viewModel = loginViewModel
        )
    }
    composable("register") {
        LoginRegisterScreen(
            navController = navHostController,
            viewModel = loginViewModel
        )
    }
}

private fun NavGraphBuilder.setupCategoriaRoutes(navHostController: NavHostController) {
    composable("categoria/{tipo}") { backStackEntry ->
        CategoriaListRoute(navHostController, backStackEntry)
    }

    composable("categoria_nueva/{tipo}") { backStackEntry ->
        CategoriaNuevaRoute(navHostController, backStackEntry)
    }
}

private fun NavGraphBuilder.setupGastoRoutes(navHostController: NavHostController) {
    composable("gastos") { backStackEntry ->
        GastoListRoute(navHostController)
    }

    composable(
        "gasto_nuevo/{tipo}/{usuarioId}",
        arguments = listOf(
            navArgument("tipo") { type = NavType.StringType },
            navArgument("usuarioId") { type = NavType.IntType }
        )
    ) { backStackEntry ->
        GastoNuevoRoute(navHostController, backStackEntry)
    }

    composable(
        route = "gasto_detalle/{usuarioId}/{trasaccionId}",
        arguments = listOf(
            navArgument("usuarioId") { type = NavType.IntType },
            navArgument("trasaccionId") { type = NavType.IntType }
        )
    ) { backStackEntry ->
        GastoDetalleRoute(navHostController, backStackEntry)
    }

    composable(
        route = "gasto_editar/{usuarioId}/{gastoId}",
        arguments = listOf(
            navArgument("usuarioId") { type = NavType.IntType },
            navArgument("gastoId") { type = NavType.IntType }
        )
    ) { backStackEntry ->
        GastoEditarRoute(navHostController, backStackEntry)
    }
}

private fun NavGraphBuilder.setupPagoRoutes(navHostController: NavHostController) {
    composable("pagos/{usuarioId}") {
        PagoListRoute(navHostController)
    }

    composable(
        "pago_nuevo/{usuarioId}",
        arguments = listOf(navArgument("usuarioId") { type = NavType.IntType })
    ) { backStackEntry ->
        PagoNuevoRoute(navHostController)
    }

    composable(
        route = "pago_detalle/{usuarioId}/{pagoId}",
        arguments = listOf(
            navArgument("usuarioId") { type = NavType.IntType },
            navArgument("pagoId") { type = NavType.IntType }
        )
    ) { backStackEntry ->
        PagoDetalleRoute(navHostController, backStackEntry)
    }

    composable(
        route = "pago_editar/{usuarioId}/{pagoId}",
        arguments = listOf(
            navArgument("usuarioId") { type = NavType.IntType },
            navArgument("pagoId") { type = NavType.IntType }
        )
    ) { backStackEntry ->
        PagoEditarRoute(navHostController, backStackEntry)
    }
}

private fun NavGraphBuilder.setupLimiteRoutes(navHostController: NavHostController) {
    composable("limites/{usuarioId}") {
        LimiteListRoute(navHostController)
    }

    composable(
        "limite_nuevo/{usuarioId}",
        arguments = listOf(navArgument("usuarioId") { type = NavType.IntType })
    ) { backStackEntry ->
        LimiteNuevoRoute(navHostController)
    }

    composable(
        route = "limite_detalle/{usuarioId}/{limiteId}",
        arguments = listOf(
            navArgument("usuarioId") { type = NavType.IntType },
            navArgument("limiteId") { type = NavType.IntType }
        )
    ) { backStackEntry ->
        LimiteDetalleRoute(navHostController, backStackEntry)
    }

    composable(
        route = "limite_editar/{usuarioId}/{limiteId}",
        arguments = listOf(
            navArgument("usuarioId") { type = NavType.IntType },
            navArgument("limiteId") { type = NavType.IntType }
        )
    ) { backStackEntry ->
        LimiteEditarRoute(navHostController, backStackEntry)
    }
}

private fun NavGraphBuilder.setupMetaRoutes(navHostController: NavHostController) {
    composable(
        route = "metaahorros/{usuarioId}",
        arguments = listOf(navArgument("usuarioId") { type = NavType.IntType })
    ) { backStackEntry ->
        MetaListRoute(navHostController, backStackEntry)
    }

    composable(
        route = "meta_nueva/{usuarioId}",
        arguments = listOf(navArgument("usuarioId") { type = NavType.IntType })
    ) { backStackEntry ->
        MetaNuevaRoute(navHostController)
    }

    composable(
        route = "meta_detalle/{usuarioId}/{metaId}",
        arguments = listOf(
            navArgument("usuarioId") { type = NavType.IntType },
            navArgument("metaId") { type = NavType.IntType }
        )
    ) { backStackEntry ->
        MetaDetalleRoute(navHostController, backStackEntry)
    }

    composable(
        route = "meta_editar/{usuarioId}/{metaId}",
        arguments = listOf(
            navArgument("usuarioId") { type = NavType.IntType },
            navArgument("metaId") { type = NavType.IntType }
        )
    ) { backStackEntry ->
        MetaEditarRoute(navHostController, backStackEntry)
    }

    composable(
        route = "meta_monto_ahorro/{usuarioId}/{metaId}",
        arguments = listOf(
            navArgument("usuarioId") { type = NavType.IntType },
            navArgument("metaId") { type = NavType.IntType }
        )
    ) { backStackEntry ->
        MetaMontoRoute(navHostController, backStackEntry)
    }
}

private fun NavGraphBuilder.setupSettingsRoutes(navHostController: NavHostController) {
    composable("cambiarFoto/{usuarioId}") { backStackEntry ->
        CambiarFotoRoute(navHostController, backStackEntry)
    }
    composable(
        route = "divisas/{usuarioId}",
        arguments = listOf(navArgument("usuarioId") { type = NavType.IntType })
    ) { backStackEntry ->
        DivisasRoute(navHostController, backStackEntry)
    }

    composable("ajustes/{usuarioId}") { backStackEntry ->
        AjustesRoute(navHostController)
    }

    composable(
        route = "notificaciones/{usuarioId}",
        arguments = listOf(navArgument("usuarioId") { type = NavType.IntType })
    ) { backStackEntry ->
        NotificacionesRoute(navHostController)
    }

    composable(
        route = "apariencia/{usuarioId}",
        arguments = listOf(navArgument("usuarioId") { type = NavType.IntType })
    ) { backStackEntry ->
        AparienciaRoute(navHostController)
    }

    composable("cambiar_contrasena/{usuarioId}") { backStackEntry ->
        CambiarContrasenaRoute(navHostController, backStackEntry)
    }

    composable("panel_usuario/{usuarioId}") { backStackEntry ->
        PanelUsuarioRoute(navHostController, backStackEntry)
    }

    composable("centro_ayuda") { backStackEntry ->
        CentroAyudaRoute(navHostController)
    }

    composable("soporte") { backStackEntry ->
        SoporteRoute(navHostController)
    }
    composable("reset_password") {
        ResetPasswordRoute(navHostController)
    }
}

private fun NavGraphBuilder.setupMiscRoutes(navHostController: NavHostController) {
    composable(
        route = "chatIA/{usuarioId}",
        arguments = listOf(navArgument("usuarioId") { type = NavType.IntType })
    ) { backStackEntry ->
        ChatIARoute(navHostController, backStackEntry)
    }

    composable(
        route = "grafico/{usuarioId}",
        arguments = listOf(navArgument("usuarioId") { type = NavType.IntType })
    ) { backStackEntry ->
        GraficoRoute(navHostController, backStackEntry)
    }
}

@Composable
private fun CategoriaListRoute(
    navHostController: NavHostController,
    backStackEntry: NavBackStackEntry
) {
    val tipo = backStackEntry.arguments?.getString("tipo") ?: "Gasto"
    val categoriaVM = hiltViewModel<CategoriaViewModel>()
    val context = LocalContext.current
    val usuarioId by produceState(initialValue = 0) {
        value = DataLogin.obtenerUsuarioId(context) ?: 0
    }

    LaunchedEffect(usuarioId) {
        if (usuarioId != 0) {
            categoriaVM.setUsuarioId(usuarioId)
        }
    }

    CategoriaListScreen(
        viewModel = categoriaVM,
        tipoFiltro = tipo,
        usuarioId = usuarioId,
        onBackClick = { navHostController.popBackStack() },
        onAgregarCategoriaClick = { tipoActual ->
            navHostController.navigate("categoria_nueva/$tipoActual")
        },
    )
}

@Composable
private fun CategoriaNuevaRoute(
    navHostController: NavHostController,
    backStackEntry: NavBackStackEntry
) {
    val tipo = backStackEntry.arguments?.getString("tipo") ?: "Gasto"
    val categoriaVM = hiltViewModel<CategoriaViewModel>()
    val context = LocalContext.current
    val usuarioId by produceState(initialValue = 0) {
        value = DataLogin.obtenerUsuarioId(context) ?: 0
    }

    LaunchedEffect(usuarioId) {
        if (usuarioId != 0) {
            categoriaVM.setUsuarioId(usuarioId)
            categoriaVM.onTipoChange(tipo)
        }
    }

    if (usuarioId == 0) {
        LoadingBox()
    } else {
        CategoriaScreen(
            viewModel = categoriaVM,
            usuarioId = usuarioId,
            tipo = tipo,
            onGuardar = { _, _, _, _ ->
                categoriaVM.saveCategoria(usuarioId) {
                    navHostController.popBackStack()
                }
            },
            onCancel = { navHostController.popBackStack() }
        )
    }
}

@Composable
private fun GastoListRoute(
    navHostController: NavHostController,
) {
    val context = LocalContext.current
    val usuarioId by produceState(initialValue = 0) {
        value = DataLogin.obtenerUsuarioId(context) ?: 0
    }

    val gastoViewModel = hiltViewModel<GastoViewModel>()
    val categoriaViewModel = hiltViewModel<CategoriaViewModel>()

    LaunchedEffect(usuarioId) {
        if (usuarioId != 0) {
            gastoViewModel.inicializar(usuarioId)
            categoriaViewModel.fetchCategorias(usuarioId)
        }
    }

    if (usuarioId != 0) {
        GastoListScreen(
            viewModel = gastoViewModel,
            usuarioId = usuarioId,
            categoriaViewModel = categoriaViewModel,
            onNuevoClick = {
                val tipo = "Gasto"
                navHostController.navigate("gasto_nuevo/$tipo/$usuarioId")
            },
            navController = navHostController
        )
    } else {
        LoadingTextBox(NavConstants.CARGANDO_USUARIO)
    }
}

@Composable
private fun GastoNuevoRoute(
    navHostController: NavHostController,
    backStackEntry: NavBackStackEntry
) {
    val tipoInicial = backStackEntry.arguments?.getString("tipo") ?: "Gasto"
    val usuarioId = backStackEntry.arguments?.getInt("usuarioId") ?: 0

    val gastoViewModel: GastoViewModel = hiltViewModel()
    val categoriaViewModel: CategoriaViewModel = hiltViewModel()

    LaunchedEffect(usuarioId) {
        if (usuarioId != 0) {
            categoriaViewModel.fetchCategorias(usuarioId)
            gastoViewModel.inicializar(usuarioId)
        }
    }

    val categoriaUiState by categoriaViewModel.uiState.collectAsState()
    val categoriasFiltradas = categoriaUiState.categorias

    if (usuarioId != 0) {
        GastoScreen(
            categorias = categoriasFiltradas,
            tipoInicial = tipoInicial,
            usuarioId = usuarioId,
            onGuardar = { tipoSeleccionado, monto, categoriaNombre, fechaSeleccionada, notas, usuarioIdGuardado ->
                val categoriaId = categoriasFiltradas.find { it.nombre == categoriaNombre }?.categoriaId ?: 0

                gastoViewModel.crearTransaccion(
                    TransaccionDto(
                        transaccionId = 0,
                        monto = monto,
                        categoriaId = categoriaId,
                        fecha = fechaSeleccionada,
                        notas = notas,
                        tipo = tipoSeleccionado,
                        usuarioId = usuarioIdGuardado
                    )
                )
                navHostController.navigate("gastos") {
                    popUpTo("gastos") { inclusive = true }
                }
            },
            onCancel = { navHostController.popBackStack() }
        )
    } else {
        LoadingTextBox(NavConstants.CARGANDO_USUARIO)
    }
}

@Composable
private fun GastoDetalleRoute(
    navHostController: NavHostController,
    backStackEntry: NavBackStackEntry
) {
    val context = LocalContext.current
    val usuarioId by produceState(initialValue = 0) {
        value = DataLogin.obtenerUsuarioId(context) ?: 0
    }

    val trasaccionId = backStackEntry.arguments?.getInt("trasaccionId") ?: 0
    val gastoViewModel = hiltViewModel<GastoViewModel>()

    LaunchedEffect(usuarioId) {
        if (usuarioId != 0) {
            gastoViewModel.cargarTransacciones(usuarioId)
            gastoViewModel.fetchCategorias(usuarioId)
        }
    }

    val uiState by gastoViewModel.uiState.collectAsState()
    val categorias by gastoViewModel.categorias.collectAsState()

    val gasto = uiState.transacciones.find { it.transaccionId == trasaccionId }

    gasto?.let { transaccion ->
        val categoria = categorias.find { it.categoriaId == transaccion.categoriaId }
        val categoriaIcono = categoria?.icono ?: "💸"
        val categoriaNombre = categoria?.nombre ?: NavConstants.SIN_CATEGORIA

        GastoDetalleScreen(
            transaccionId = trasaccionId,
            categoriaIcono = categoriaIcono,
            categoriaNombre = categoriaNombre,
            onBackClick = { navHostController.popBackStack() },
            onEditarClick = {
                navHostController.navigate("gasto_editar/$usuarioId/$trasaccionId")
            },
            onEliminarConfirmado = {
                gastoViewModel.eliminarTransaccion(trasaccionId)
                navHostController.popBackStack()
            }
        )
    }
}

@Composable
private fun GastoEditarRoute(
    navHostController: NavHostController,
    backStackEntry: NavBackStackEntry
) {
    val context = LocalContext.current
    val usuarioId by produceState(initialValue = 0) {
        value = DataLogin.obtenerUsuarioId(context) ?: 0
    }

    val gastoId = backStackEntry.arguments?.getInt("gastoId") ?: 0
    val gastoViewModel = hiltViewModel<GastoViewModel>()
    val categoriaViewModel = hiltViewModel<CategoriaViewModel>()

    LaunchedEffect(usuarioId) {
        if (usuarioId != 0) {
            gastoViewModel.inicializar(usuarioId)
            categoriaViewModel.fetchCategorias(usuarioId)
        }
    }

    val uiState by gastoViewModel.uiState.collectAsState()
    val categoriaUiState by categoriaViewModel.uiState.collectAsState()
    val transaccion = uiState.transacciones.find { it.transaccionId == gastoId }
    val categoriasFiltradas = categoriaUiState.categorias

    transaccion?.let { txn ->
        GastoScreen(
            categorias = categoriasFiltradas,
            tipoInicial = txn.tipo,
            transaccionParaEditar = txn,
            usuarioId = usuarioId,
            onGuardar = { tipoSeleccionado, monto, categoriaNombre, fecha, notas, usuarioIdGuardado ->
                val categoriaId = categoriasFiltradas.find { it.nombre == categoriaNombre }?.categoriaId ?: 0

                gastoViewModel.actualizarTransaccion(
                    TransaccionDto(
                        transaccionId = txn.transaccionId,
                        monto = monto,
                        categoriaId = categoriaId,
                        fecha = fecha,
                        notas = notas,
                        tipo = tipoSeleccionado,
                        usuarioId = usuarioIdGuardado
                    )
                )

                navHostController.navigate("gastos") {
                    popUpTo("gastos") { inclusive = true }
                }
            },
            onCancel = { navHostController.popBackStack() }
        )
    }
}

@Composable
private fun PagoListRoute(navHostController: NavHostController) {
    val pagoViewModel = hiltViewModel<PagoViewModel>()
    val categorias by pagoViewModel.categorias.collectAsState()
    val context = LocalContext.current
    val usuarioId by produceState(initialValue = 0) {
        value = DataLogin.obtenerUsuarioId(context) ?: 0
    }

    LaunchedEffect(usuarioId) {
        if (usuarioId != 0) {
            pagoViewModel.cargarPagosRecurrentes(usuarioId)
            pagoViewModel.fetchCategorias(usuarioId)
        }
    }

    PagoListScreen(
        viewModel = pagoViewModel,
        categorias = categorias,
        onAgregarPagoClick = {
            navHostController.navigate("pago_nuevo/$usuarioId")
        },
        onBackClick = { navHostController.popBackStack() },
        onPagoClick = { pagoId ->
            navHostController.navigate("pago_detalle/$usuarioId/$pagoId")
        }
    )
}

@Composable
private fun PagoNuevoRoute(
    navHostController: NavHostController,
) {
    val context = LocalContext.current
    val usuarioId by produceState(initialValue = 0) {
        value = DataLogin.obtenerUsuarioId(context) ?: 0
    }

    val pagoViewModel = hiltViewModel<PagoViewModel>()

    LaunchedEffect(usuarioId) {
        if (usuarioId != 0) {
            pagoViewModel.inicializar(usuarioId)
        }
    }

    if (usuarioId == 0) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Usuario no autenticado")
        }
    } else {
        PagoScreen(
            viewModel = pagoViewModel,
            pagoParaEditar = null,
            usuarioId = usuarioId,
            onGuardar = { monto, categoriaId, frecuencia, fechaInicio, fechaFin, usuarioIdGuardado ->
                pagoViewModel.crearPagoRecurrente(
                    PagoRecurrenteDto(
                        monto = monto,
                        categoriaId = categoriaId,
                        frecuencia = frecuencia,
                        fechaInicio = fechaInicio,
                        fechaFin = fechaFin,
                        usuarioId = usuarioIdGuardado
                    )
                )
                navHostController.navigate(NavConstants.PAGOS_ROUTE) {
                    popUpTo(NavConstants.PAGOS_ROUTE) { inclusive = true }
                }
            },
            onCancel = { navHostController.popBackStack() }
        )
    }
}

@Composable
private fun PagoDetalleRoute(
    navHostController: NavHostController,
    backStackEntry: NavBackStackEntry
) {
    val context = LocalContext.current
    val usuarioId by produceState(initialValue = 0) {
        value = DataLogin.obtenerUsuarioId(context) ?: 0
    }

    val pagoId = backStackEntry.arguments?.getInt("pagoId") ?: 0
    val pagoViewModel = hiltViewModel<PagoViewModel>()

    LaunchedEffect(usuarioId) {
        if (usuarioId != 0) {
            pagoViewModel.cargarPagosRecurrentes(usuarioId)
            pagoViewModel.fetchCategorias(usuarioId)
        }
    }

    val uiState by pagoViewModel.uiState.collectAsState()
    val categorias by pagoViewModel.categorias.collectAsState()

    val pago = uiState.pagos.find { it.pagoRecurrenteId == pagoId }
    pago?.let { pagoItem ->
        val categoria = categorias.find { it.categoriaId == pagoItem.categoriaId }
        val categoriaIcono = categoria?.icono ?: "💵"
        val categoriaNombre = categoria?.nombre ?: NavConstants.SIN_CATEGORIA

        PagoDetalleScreen(
            pagoId = pagoId,
            pago = pagoItem,
            categoriaIcono = categoriaIcono,
            categoriaNombre = categoriaNombre,
            onBackClick = { navHostController.popBackStack() },
            onEditarClick = {
                navHostController.navigate("pago_editar/$usuarioId/$pagoId")
            },
            onEliminarClick = { },
            onEliminarConfirmado = {
                pagoViewModel.eliminarPagoRecurrente(pagoId)
            },
            navHostController = navHostController,
            pagoViewModel = pagoViewModel
        )
    }
}

@Composable
private fun PagoEditarRoute(
    navHostController: NavHostController,
    backStackEntry: NavBackStackEntry
) {
    val context = LocalContext.current
    val usuarioId by produceState(initialValue = 0) {
        value = DataLogin.obtenerUsuarioId(context) ?: 0
    }

    val pagoId = backStackEntry.arguments?.getInt("pagoId") ?: 0
    val pagoViewModel = hiltViewModel<PagoViewModel>()

    LaunchedEffect(usuarioId) {
        if (usuarioId != 0) {
            pagoViewModel.cargarPagosRecurrentes(usuarioId)
            pagoViewModel.fetchCategorias(usuarioId)
        }
    }

    val uiState by pagoViewModel.uiState.collectAsState()
    val pago = uiState.pagos.find { it.pagoRecurrenteId == pagoId }

    pago?.let { pagoItem ->
        PagoScreen(
            viewModel = pagoViewModel,
            pagoParaEditar = pagoItem,
            usuarioId = usuarioId,
            onGuardar = { monto, categoriaId, frecuencia, fechaInicio, fechaFin, usuarioIdGuardado ->
                val pagoActualizado = PagoRecurrenteDto(
                    pagoRecurrenteId = pagoId,
                    monto = monto,
                    categoriaId = categoriaId,
                    frecuencia = frecuencia,
                    fechaInicio = fechaInicio,
                    fechaFin = fechaFin,
                    usuarioId = usuarioIdGuardado
                )
                pagoViewModel.actualizarPagoRecurrente(pagoId, pagoActualizado)
                navHostController.navigate(NavConstants.PAGOS_ROUTE) {
                    popUpTo(NavConstants.PAGOS_ROUTE) { inclusive = true }
                }
            },
            onCancel = { navHostController.popBackStack() }
        )
    }
}

@Composable
private fun LimiteListRoute(navHostController: NavHostController) {
    val limiteViewModel = hiltViewModel<LimiteViewModel>()
    val gastoViewModel = hiltViewModel<GastoViewModel>()
    val context = LocalContext.current
    val usuarioId by produceState(initialValue = 0) {
        value = DataLogin.obtenerUsuarioId(context) ?: 0
    }

    LaunchedEffect(usuarioId) {
        if (usuarioId != 0) {
            limiteViewModel.cargarLimites(usuarioId)
            limiteViewModel.fetchCategorias(usuarioId)
            gastoViewModel.inicializar(usuarioId)
        }
    }

    LimiteListScreen(
        viewModel = limiteViewModel,
        gastoViewModel = gastoViewModel,
        onAgregarLimiteClick = {
            navHostController.navigate("limite_nuevo/$usuarioId")
        },
        onBackClick = { navHostController.popBackStack() },
        onLimiteClick = { limiteId ->
            navHostController.navigate("limite_detalle/$usuarioId/$limiteId")
        }
    )
}

@Composable
private fun LimiteNuevoRoute(
    navHostController: NavHostController,
) {
    val context = LocalContext.current
    val usuarioId by produceState(initialValue = 0) {
        value = DataLogin.obtenerUsuarioId(context) ?: 0
    }

    val limiteViewModel = hiltViewModel<LimiteViewModel>()

    LaunchedEffect(usuarioId) {
        if (usuarioId != 0) {
            limiteViewModel.inicializar(usuarioId)
        }
    }

    if (usuarioId == 0) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Usuario no autenticado")
        }
    } else {
        LimiteScreen(
            viewModel = limiteViewModel,
            limiteParaEditar = null,
            onGuardar = { montoLimite, categoriaId, periodo, usuarioIdGuardado ->
                limiteViewModel.crearLimite(
                    LimiteGastoDto(
                        montoLimite = montoLimite,
                        categoriaId = categoriaId,
                        periodo = periodo,
                        usuarioId = usuarioIdGuardado
                    )
                )
                navHostController.navigate("limites/$usuarioId") {
                    popUpTo("limites/$usuarioId") { inclusive = true }
                }
            },
            onCancel = { navHostController.popBackStack() },
            usuarioId = usuarioId
        )
    }
}

@Composable
private fun LimiteDetalleRoute(
    navHostController: NavHostController,
    backStackEntry: NavBackStackEntry
) {
    val context = LocalContext.current
    val usuarioId by produceState(initialValue = 0) {
        value = DataLogin.obtenerUsuarioId(context) ?: 0
    }

    val limiteId = backStackEntry.arguments?.getInt("limiteId") ?: 0
    val limiteViewModel = hiltViewModel<LimiteViewModel>()
    val gastoViewModel = hiltViewModel<GastoViewModel>()

    LaunchedEffect(usuarioId) {
        if (usuarioId != 0) {
            limiteViewModel.cargarLimites(usuarioId)
            limiteViewModel.fetchCategorias(usuarioId)
            gastoViewModel.inicializar(usuarioId)
        }
    }

    val uiState by limiteViewModel.uiState.collectAsState()
    val categorias by limiteViewModel.categorias.collectAsState()

    if (uiState.isLoading) {
        LoadingBox()
    } else {
        val limite = uiState.limites.find { it.limiteGastoId == limiteId }
        limite?.let { limiteItem ->
            val categoria = categorias.find { it.categoriaId == limiteItem.categoriaId }
            val categoriaIcono = categoria?.icono ?: "💵"
            val categoriaNombre = categoria?.nombre ?: NavConstants.SIN_CATEGORIA

            LimiteDetalleScreen(
                limite = limiteItem,
                categoriaIcono = categoriaIcono,
                categoriaNombre = categoriaNombre,
                gastoViewModel = gastoViewModel,
                onBackClick = { navHostController.popBackStack() },
                onEditarClick = { navHostController.navigate("limite_editar/$usuarioId/$limiteId") },
                onEliminarConfirmado = {
                    limiteViewModel.eliminarLimite(limiteId)
                    navHostController.navigate("limites/$usuarioId") {
                        popUpTo("limites/$usuarioId") { inclusive = true }
                    }
                }
            )
        } ?: run {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Límite no encontrado")
            }
        }
    }
}

@Composable
private fun LimiteEditarRoute(
    navHostController: NavHostController,
    backStackEntry: NavBackStackEntry
) {
    val context = LocalContext.current
    val usuarioId by produceState(initialValue = 0) {
        value = DataLogin.obtenerUsuarioId(context) ?: 0
    }

    val limiteId = backStackEntry.arguments?.getInt("limiteId") ?: 0
    val limiteViewModel = hiltViewModel<LimiteViewModel>()

    LaunchedEffect(usuarioId) {
        if (usuarioId != 0) {
            limiteViewModel.cargarLimites(usuarioId)
            limiteViewModel.fetchCategorias(usuarioId)
        }
    }

    val uiState by limiteViewModel.uiState.collectAsState()
    val limite = uiState.limites.find { it.limiteGastoId == limiteId }

    limite?.let { limiteItem ->
        LimiteScreen(
            viewModel = limiteViewModel,
            limiteParaEditar = limiteItem,
            onGuardar = { montoLimite, categoriaId, periodo, usuarioIdGuardado ->
                val limiteActualizado = LimiteGastoDto(
                    limiteGastoId = limiteId,
                    montoLimite = montoLimite,
                    categoriaId = categoriaId,
                    periodo = periodo,
                    usuarioId = usuarioIdGuardado
                )
                limiteViewModel.actualizarLimite(limiteId, limiteActualizado)
                navHostController.navigate("limites/$usuarioId") {
                    popUpTo("limites/$usuarioId") { inclusive = true }
                }
            },
            onCancel = { navHostController.popBackStack() },
            usuarioId = usuarioId
        )
    }
}

@Composable
private fun MetaListRoute(
    navHostController: NavHostController,
    backStackEntry: NavBackStackEntry
) {
    val usuarioId = backStackEntry.arguments?.getInt("usuarioId") ?: 0
    val metaViewModel = hiltViewModel<MetaViewModel>()

    LaunchedEffect(usuarioId) {
        metaViewModel.cargarMetas(usuarioId)
    }

    MetaListScreen(
        viewModel = metaViewModel,
        usuarioId = usuarioId,
        navController = navHostController,
        onBackClick = { navHostController.popBackStack() },
        onAgregarMetaClick = {
            navHostController.navigate("meta_nueva/$usuarioId")
        },
        onMetaClick = { metaId ->
            navHostController.navigate("meta_detalle/$usuarioId/$metaId")
        },
        onAgregarMontoClick = { metaId ->
            navHostController.navigate("meta_monto_ahorro/$usuarioId/$metaId")
        }
    )
}

@Composable
private fun MetaNuevaRoute(
    navHostController: NavHostController,
) {
    val context = LocalContext.current
    val usuarioId by produceState(initialValue = 0) {
        value = DataLogin.obtenerUsuarioId(context) ?: 0
    }

    val metaViewModel = hiltViewModel<MetaViewModel>()

    MetaScreen(
        metaParaEditar = null,
        usuarioId = usuarioId,
        onGuardar = { nombre, montoObjetivo, fechaFinal, contribucion, imagen, usuarioId ->
            metaViewModel.crearMeta(
                MetaAhorroDto(
                    nombreMeta = nombre,
                    montoObjetivo = montoObjetivo,
                    fechaFinalizacion = fechaFinal,
                    contribucionRecurrente = if (contribucion) 0.0 else null,
                    imagen = imagen,
                    montoAhorrado = 0.0,
                    fechaMontoAhorrado = fechaFinal,
                    usuarioId = usuarioId
                ),
                usuarioId = usuarioId
            )
            navHostController.navigate("metaahorros/$usuarioId") {
                popUpTo("metaahorros/$usuarioId") { inclusive = true }
            }
        },
        onCancel = { navHostController.popBackStack() },
        onImagenSeleccionada = { },
    )
}

@Composable
private fun MetaDetalleRoute(
    navHostController: NavHostController,
    backStackEntry: NavBackStackEntry
) {
    val context = LocalContext.current
    val usuarioId by produceState(initialValue = 0) {
        value = DataLogin.obtenerUsuarioId(context) ?: 0
    }

    val metaId = backStackEntry.arguments?.getInt("metaId") ?: 0
    val metaViewModel = hiltViewModel<MetaViewModel>()
    val uiState by metaViewModel.uiState.collectAsState()

    LaunchedEffect(usuarioId, metaId) {
        metaViewModel.cargarMetas(usuarioId, metaId)
    }

    uiState.metaSeleccionada?.let { meta ->
        MetaDetalleScreen(
            meta = meta,
            onBackClick = { navHostController.popBackStack() },
            onEditarClick = {
                navHostController.navigate("meta_editar/$usuarioId/$metaId")
            },
            onEliminarClick = { },
            onEliminarConfirmado = {
                metaViewModel.eliminarMeta(metaId)
                navHostController.navigate("metaahorros/$usuarioId") {
                    popUpTo("metaahorros/$usuarioId") { inclusive = true }
                }
            }
        )
    }
}

@Composable
private fun MetaEditarRoute(
    navHostController: NavHostController,
    backStackEntry: NavBackStackEntry
) {
    val context = LocalContext.current
    val usuarioId by produceState(initialValue = 0) {
        value = DataLogin.obtenerUsuarioId(context) ?: 0
    }

    val metaId = backStackEntry.arguments?.getInt("metaId") ?: 0
    val metaViewModel = hiltViewModel<MetaViewModel>()
    val uiState by metaViewModel.uiState.collectAsState()

    LaunchedEffect(usuarioId, metaId) {
        metaViewModel.setUsuarioId(usuarioId)
        metaViewModel.cargarMetas(usuarioId, metaId)
    }

    uiState.metaSeleccionada?.let { meta ->
        MetaScreen(
            metaParaEditar = meta,
            usuarioId = usuarioId,
            onGuardar = { nombre, montoObjetivo, fechaFinal, contribucion, imagen, _ ->
                val metaActualizada = meta.copy(
                    nombreMeta = nombre,
                    montoObjetivo = montoObjetivo,
                    fechaFinalizacion = fechaFinal,
                    contribucionRecurrente = if (contribucion) 0.0 else null,
                    imagen = imagen,
                    usuarioId = usuarioId
                )
                metaViewModel.actualizarMeta(metaId, metaActualizada)
                navHostController.navigate("metaahorros/$usuarioId") {
                    popUpTo("metaahorros/$usuarioId") { inclusive = true }
                }
            },
            onCancel = { navHostController.popBackStack() },
            onImagenSeleccionada = { }
        )
    }
}

@Composable
private fun MetaMontoRoute(
    navHostController: NavHostController,
    backStackEntry: NavBackStackEntry
) {
    val context = LocalContext.current
    val usuarioId by produceState(initialValue = 0) {
        value = DataLogin.obtenerUsuarioId(context) ?: 0
    }

    val metaId = backStackEntry.arguments?.getInt("metaId") ?: 0
    val metaViewModel = hiltViewModel<MetaViewModel>()

    LaunchedEffect(usuarioId, metaId) {
        metaViewModel.setUsuarioId(usuarioId)
        metaViewModel.cargarMetas(usuarioId, metaId)
    }

    val uiState by metaViewModel.uiState.collectAsState()

    val meta = uiState.metaSeleccionada ?: MetaAhorroDto(
        metaAhorroId = 0,
        nombreMeta = "",
        montoObjetivo = 0.0,
        fechaFinalizacion = OffsetDateTime.now(),
        usuarioId = usuarioId
    )

    MetaMAhorroScreen(
        meta = meta,
        usuarioId = usuarioId,
        navController = navHostController,
        onGuardarMonto = { montoAhorrado, fechaMonto ->
            metaViewModel.actualizarMontoAhorrado(meta.metaAhorroId, montoAhorrado, fechaMonto)
        },
        onCancel = { navHostController.popBackStack() }
    )
}


@Composable
private fun CambiarFotoRoute(
    navHostController: NavHostController,
    backStackEntry: NavBackStackEntry
) {
    val usuarioId = backStackEntry.arguments?.getString("usuarioId")?.toInt() ?: 0
    CambiarFotoScreen(
        usuarioId = usuarioId,
        onNavigateBack = { navHostController.popBackStack() }
    )
}

@Composable
private fun AjustesRoute(
    navHostController: NavHostController,
) {
    val context = LocalContext.current
    val usuarioId by produceState(initialValue = 0) {
        value = DataLogin.obtenerUsuarioId(context) ?: 0
    }
    val scope = rememberCoroutineScope()

    AjustesListScreen(
        navController = navHostController,
        usuarioId = usuarioId,
        onEditarPerfil = {
            navHostController.navigate("panel_usuario/$usuarioId")
        },
        onCambiarContrasena = {
            navHostController.navigate("cambiar_contrasena/$usuarioId")
        },
        onCerrarSesion = {
            scope.launch {
                DataLogin.limpiarSesion(context)
                navHostController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            }
        },
        onNotificaciones = {
            navHostController.navigate("notificaciones/$usuarioId")
        },
        onApariencia = {
            navHostController.navigate("apariencia/$usuarioId")
        },
        onCentroAyuda = {
            navHostController.navigate("centro_ayuda")
        },
        onSoporte = {
            navHostController.navigate("soporte")
        }
    )
}

@Composable
private fun CentroAyudaRoute(
    navHostController: NavHostController,
) {
    val context = LocalContext.current
    val usuarioId by produceState(initialValue = 0) {
        value = DataLogin.obtenerUsuarioId(context) ?: 0
    }

    CentroAyudaScreen(
        navController = navHostController,
        usuarioId = usuarioId
    )
}
@Composable
fun DivisasRoute(
    navHostController: NavHostController,
    backStackEntry: NavBackStackEntry
) {
    val usuarioId = backStackEntry.arguments?.getInt("usuarioId") ?: 0

    DivisasScreen(
        usuarioId = usuarioId,
        onBackClick = { navHostController.popBackStack() }
    )
}
@Composable
private fun SoporteRoute(
    navHostController: NavHostController,
) {
    val context = LocalContext.current
    val usuarioId by produceState(initialValue = 0) {
        value = DataLogin.obtenerUsuarioId(context) ?: 0
    }

    SoporteScreen(
        navController = navHostController,
        usuarioId = usuarioId
    )
}

@Composable
private fun NotificacionesRoute(
    navHostController: NavHostController,
) {
    val context = LocalContext.current
    val usuarioId by produceState(initialValue = 0) {
        value = DataLogin.obtenerUsuarioId(context) ?: 0
    }

    NotificacionesScreen(
        navController = navHostController,
        usuarioId = usuarioId
    )
}

@Composable
private fun AparienciaRoute(
    navHostController: NavHostController,
) {
    val context = LocalContext.current
    val usuarioId by produceState(initialValue = 0) {
        value = DataLogin.obtenerUsuarioId(context) ?: 0
    }

    AparienciaScreen(
        navController = navHostController,
        usuarioId = usuarioId
    )
}

@Composable
private fun CambiarContrasenaRoute(
    navHostController: NavHostController,
    backStackEntry: NavBackStackEntry
) {
    val usuarioId = backStackEntry.arguments?.getString("usuarioId")?.toIntOrNull() ?: 0

    CambiarContrasenaScreen(
        usuarioId = usuarioId,
        onBack = { navHostController.popBackStack() }
    )
}

@Composable
private fun PanelUsuarioRoute(
    navHostController: NavHostController,
    backStackEntry: NavBackStackEntry
) {
    val usuarioId = backStackEntry.arguments?.getString("usuarioId")?.toIntOrNull() ?: 0

    PanelUsuarioScreen(
        navController = navHostController,
        usuarioId = usuarioId,
        onCambiarContrasenaClick = {
            navHostController.navigate("cambiar_contrasena/$usuarioId") {
                launchSingleTop = true
            }
        },
        onCambiarFoto = {
            navHostController.navigate("cambiarFoto/$usuarioId")
        },
        onDivisa = {
            navHostController.navigate("divisas/$usuarioId") {
                launchSingleTop = true
            }
        },        onAjustes = {
            navHostController.navigate("ajustes/$usuarioId")
        },
        onTransacciones = {
            navHostController.navigate("gastos")
        }
    )
}
@Composable
fun ResetPasswordRoute(
    navHostController: NavHostController
) {
    ResetPasswordScreen(
        onBackClick = {
            navHostController.popBackStack()
        }
    )
}
@Composable
private fun ChatIARoute(
    navHostController: NavHostController,
    backStackEntry: NavBackStackEntry
) {
    val usuarioId = backStackEntry.arguments?.getInt("usuarioId") ?: 0


    if (usuarioId == 0) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Error: Usuario no válido")
        }
    } else {
        ChatIaScreen(
            navController = navHostController,
            usuarioId = usuarioId
        )
    }
}

@Composable
private fun GraficoRoute(
    navHostController: NavHostController,
    backStackEntry: NavBackStackEntry
) {
    val usuarioId = backStackEntry.arguments?.getInt("usuarioId") ?: 0
    val gastoViewModel = hiltViewModel<GastoViewModel>()

    LaunchedEffect(usuarioId) {
        if (usuarioId != 0) {
            gastoViewModel.inicializar(usuarioId)
        }
    }

    if (usuarioId != 0) {
        GraficoScreen(
            usuarioId = usuarioId,
            gastoviewModel = hiltViewModel(),
            onBackClick = { navHostController.popBackStack() }
        )
    } else {
        LoadingTextBox(NavConstants.CARGANDO_USUARIO)
    }
}

@Composable
private fun LoadingBox() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun LoadingTextBox(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text, modifier = Modifier.padding(16.dp))
    }
}

suspend fun verificarSesion(context: Context): Boolean {
    val usuarioId = DataLogin.obtenerUsuarioId(context)
    return usuarioId != null && usuarioId != 0
}