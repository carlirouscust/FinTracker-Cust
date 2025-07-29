package ucne.edu.fintracker.presentation.navegation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import ucne.edu.fintracker.presentation.gasto.GastoViewModel
import ucne.edu.fintracker.presentation.login.LoginViewModel
import ucne.edu.fintracker.presentation.login.LoginRegisterScreen
import ucne.edu.fintracker.presentation.login.ResetPasswordScreen
import ucne.edu.fintracker.presentation.remote.DateUtil
import ucne.edu.fintracker.presentation.remote.FinTrackerApi
import ucne.edu.fintracker.presentation.remote.dto.TransaccionDto
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneOffset
import androidx.navigation.NavType
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.navigation.navArgument
import ucne.edu.fintracker.presentation.limitegasto.LimiteDetalleScreen
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
import ucne.edu.fintracker.presentation.remote.dto.LimiteGastoDto
import ucne.edu.fintracker.presentation.remote.dto.MetaAhorroDto
import ucne.edu.fintracker.presentation.remote.dto.PagoRecurrenteDto


@Composable
fun FinTrackerNavHost(
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel = hiltViewModel(),  // <-- aquí recibes loginViewModel
    finTrackerApi: FinTrackerApi = hiltViewModel()
) {
    NavHost(
        navController = navHostController,
        startDestination = "login",
        modifier = modifier
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



        composable("categoria/{tipo}") { backStackEntry ->
            val tipo = backStackEntry.arguments?.getString("tipo") ?: "Gasto"
            val categoriaVM = hiltViewModel<CategoriaViewModel>()
            val loginState = loginViewModel.uiState.collectAsState().value
            val usuarioId = loginState.usuarioId

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
                onCategoriaClick = { categoria ->
                    navHostController.navigate("categoria_detalle/${categoria.categoriaId}")
                }
            )
        }

        composable("categoria_nueva/{tipo}") { backStackEntry ->
            val tipo = backStackEntry.arguments?.getString("tipo") ?: "Gasto"
            val categoriaVM = hiltViewModel<CategoriaViewModel>()
            val usuarioId = loginViewModel.uiState.collectAsState().value.usuarioId ?: 0
            Log.d("CategoriaNueva", "usuarioId: $usuarioId")


            LaunchedEffect(usuarioId) {
                if (usuarioId != 0) {
                    categoriaVM.setUsuarioId(usuarioId)
                    categoriaVM.onTipoChange(tipo)
                }
            }

            if (usuarioId == 0) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                CategoriaScreen(
                    navController = navHostController,
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


        composable("gastos") {
            val gastoViewModel = hiltViewModel<GastoViewModel>()
            val categoriaViewModel = hiltViewModel<CategoriaViewModel>()
            GastoListScreen(
                viewModel = gastoViewModel,
                categoriaViewModel = categoriaViewModel,
                onNuevoClick = {
                    navHostController.navigate("gasto_nuevo/{tipo}")
                },
                navController = navHostController
            )
        }



        composable("gasto_nuevo/{tipo}") { backStackEntry ->
            val tipoInicial = backStackEntry.arguments?.getString("tipo") ?: "Gasto"

            val gastoViewModel: GastoViewModel = hiltViewModel()
            val categoriaViewModel: CategoriaViewModel = hiltViewModel()
            val loginViewModel: LoginViewModel = hiltViewModel()

            val usuarioId = loginViewModel.uiState.collectAsState().value.usuarioId ?: 0

            val categoriaUiState = categoriaViewModel.uiState.collectAsState().value
            val categoriasFiltradas = categoriaUiState.categorias

            GastoScreen(
                categorias = categoriasFiltradas.map { it.nombre },
                tipoInicial = tipoInicial,
                usuarioId = usuarioId,
                onGuardar = { tipoSeleccionado, monto, categoriaNombre, fecha, notas, UsuarioId ->
                    gastoViewModel.crearTransaccion(
                        TransaccionDto(
                            transaccionId = 0,
                            monto = monto,
                            categoriaId = categoriasFiltradas.find { it.nombre == categoriaNombre }?.categoriaId ?: 0,
                            fecha = OffsetDateTime.now(ZoneOffset.UTC),
                            notas = notas,
                            tipo = tipoSeleccionado,
                            usuarioId = UsuarioId
                        )
                    )
                    navHostController.navigate("gastos") {
                        popUpTo("gastos") { inclusive = true }
                    }
                },
                onCancel = { navHostController.popBackStack() }
            )
        }



        composable("pagos") {
            val pagoViewModel = hiltViewModel<PagoViewModel>()
            val categorias by pagoViewModel.categorias.collectAsState()
            val usuarioId = loginViewModel.uiState.collectAsState().value.usuarioId ?: 0

            LaunchedEffect(usuarioId) {
                if (usuarioId != 0) {
                    pagoViewModel.cargarPagosRecurrentes(usuarioId)
                    pagoViewModel.cargarCategorias(usuarioId)
                }
            }

            PagoListScreen(
                viewModel = pagoViewModel,
                categorias = categorias,
                onAgregarPagoClick = {
                    navHostController.navigate("pago_nuevo")
                },
                onBackClick = {
                    navHostController.popBackStack()
                },
                onPagoClick = { pagoId ->
                    navHostController.navigate("pago_detalle/$pagoId")
                }
            )
        }

        composable("pago_nuevo") {
            val pagoViewModel = hiltViewModel<PagoViewModel>()
            val loginViewModel: LoginViewModel = hiltViewModel()

            val usuarioId = loginViewModel.uiState.collectAsState().value.usuarioId ?: 0
            Log.d("PagoNuevo", "usuarioId: $usuarioId")

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

                        navHostController.navigate("pagos") {
                            popUpTo("pagos") { inclusive = true }
                        }
                    },
                    onCancel = {
                        navHostController.popBackStack()
                    }
                )
            }
        }


        composable(
            route = "pago_detalle/{pagoId}",
            arguments = listOf(navArgument("pagoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val pagoId = backStackEntry.arguments?.getInt("pagoId") ?: 0
            val pagoViewModel = hiltViewModel<PagoViewModel>()

            val uiState by pagoViewModel.uiState.collectAsState()
            val categorias by pagoViewModel.categorias.collectAsState()

            val pago = uiState.pagos.find { it.pagoRecurrenteId == pagoId }
            if (pago != null) {
                val categoria = categorias.find { it.categoriaId == pago.categoriaId }
                val categoriaIcono = categoria?.icono ?: "💵"
                val categoriaNombre = categoria?.nombre ?: "Sin categoría"

                PagoDetalleScreen(
                    pagoId = pagoId,
                    pago = pago,
                    categoriaIcono = categoriaIcono,
                    categoriaNombre = categoriaNombre,
                    onBackClick = { navHostController.popBackStack() },
                    onEditarClick = {
                        navHostController.navigate("pago_editar/$pagoId")
                    },
                    onEliminarClick = {

                    },
                    onEliminarConfirmado = {
                        pagoViewModel.eliminarPagoRecurrente(pagoId)
                        navHostController.navigate("pagos") {
                            popUpTo("pagos") { inclusive = true }
                        }
                    }
                )
            }
        }

        composable(
            route = "pago_editar/{pagoId}",
            arguments = listOf(navArgument("pagoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val pagoId = backStackEntry.arguments?.getInt("pagoId") ?: 0
            val pagoViewModel = hiltViewModel<PagoViewModel>()
            val uiState by pagoViewModel.uiState.collectAsState()
            val loginViewModel: LoginViewModel = hiltViewModel()

            val usuarioId = loginViewModel.uiState.collectAsState().value.usuarioId ?: 0

            val pago = uiState.pagos.find { it.pagoRecurrenteId == pagoId }

            if (pago != null) {
                PagoScreen(
                    viewModel = pagoViewModel,
                    pagoParaEditar = pago,
                    usuarioId = usuarioId,
                    onGuardar = { monto, categoriaId, frecuencia, fechaInicio, fechaFin, usuarioId ->
                        val pagoActualizado = PagoRecurrenteDto(
                            pagoRecurrenteId = pagoId,
                            monto = monto,
                            categoriaId = categoriaId,
                            frecuencia = frecuencia,
                            fechaInicio = fechaInicio,
                            fechaFin = fechaFin,
                            usuarioId = usuarioId
                        )
                        pagoViewModel.actualizarPagoRecurrente(pagoId, pagoActualizado)
                        navHostController.navigate("pagos") {
                            popUpTo("pagos") { inclusive = true }
                        }
                    },
                    onCancel = { navHostController.popBackStack() }
                )
            }
        }

        composable("limites") {
            val limiteViewModel = hiltViewModel<LimiteViewModel>()

            val usuarioId = loginViewModel.uiState.collectAsState().value.usuarioId ?: 0

            LaunchedEffect(usuarioId) {
                if (usuarioId != 0) {
                    limiteViewModel.cargarLimites(usuarioId)
                    limiteViewModel.fetchCategorias(usuarioId)
                }
            }

            LimiteListScreen(
                viewModel = limiteViewModel,
                onAgregarLimiteClick = {
                    navHostController.navigate("limite_nuevo/$usuarioId")
                },
                onBackClick = {
                    navHostController.popBackStack()
                },
                onLimiteClick = { limiteId ->
                    navHostController.navigate("limite_detalle/$limiteId")
                }
            )
        }


        composable(
            "limite_nuevo/{usuarioId}",
            arguments = listOf(navArgument("usuarioId") { type = NavType.IntType })
        ) { backStackEntry ->

            val usuarioId = loginViewModel.uiState.collectAsState().value.usuarioId ?: 0
            val limiteViewModel = hiltViewModel<LimiteViewModel>()

            LaunchedEffect(usuarioId) {
                limiteViewModel.inicializar(usuarioId)
            }

            LimiteScreen(
                viewModel = limiteViewModel,
                limiteParaEditar = null,
                onGuardar = { montoLimite, categoriaId, periodo, _ ->
                    limiteViewModel.crearLimite(
                        LimiteGastoDto(
                            montoLimite = montoLimite,
                            categoriaId = categoriaId,
                            periodo = periodo,
                            usuarioId = usuarioId
                        )
                    )
                    navHostController.navigate("limites") {
                        popUpTo("limites") { inclusive = true }
                    }
                },
                onCancel = { navHostController.popBackStack() },
                usuarioId = usuarioId
            )
        }





        composable(
            route = "limite_detalle/{limiteId}",
            arguments = listOf(navArgument("limiteId") { type = NavType.IntType })
        ) { backStackEntry ->
            val limiteId = backStackEntry.arguments?.getInt("limiteId") ?: 0
            val limiteViewModel = hiltViewModel<LimiteViewModel>()

            val uiState by limiteViewModel.uiState.collectAsState()
            val categorias by limiteViewModel.categorias.collectAsState()

            val limite = uiState.limites.find { it.limiteGastoId == limiteId }
            if (limite != null) {
                val categoria = categorias.find { it.categoriaId == limite.categoriaId }
                val categoriaIcono = categoria?.icono ?: "💵"
                val categoriaNombre = categoria?.nombre ?: "Sin categoría"

                LimiteDetalleScreen(
                    limite = limite,
                    categoriaIcono = categoriaIcono,
                    categoriaNombre = categoriaNombre,
                    onBackClick = { navHostController.popBackStack() },
                    onEditarClick = { navHostController.navigate("limite_editar/$limiteId") },
                    onEliminarClick = { /* puedes abrir un diálogo */ },
                    onEliminarConfirmado = {
                        limiteViewModel.eliminarLimite(limiteId)
                        navHostController.navigate("limites") {
                            popUpTo("limites") { inclusive = true }
                        }
                    }
                )
            }
        }

        composable(
            route = "limite_editar/{limiteId}",
            arguments = listOf(navArgument("limiteId") { type = NavType.IntType })
        ) { backStackEntry ->
            val limiteId = backStackEntry.arguments?.getInt("limiteId") ?: 0
            val limiteViewModel = hiltViewModel<LimiteViewModel>()
            val loginViewModel: LoginViewModel = hiltViewModel()
            val usuarioId = loginViewModel.uiState.collectAsState().value.usuarioId ?: 0
            val uiState by limiteViewModel.uiState.collectAsState()

            val limite = uiState.limites.find { it.limiteGastoId == limiteId }

            if (limite != null) {
                LimiteScreen(
                    viewModel = limiteViewModel,
                    limiteParaEditar = limite,
                    onGuardar = { montoLimite, categoriaId, periodo, usuarioId ->
                        val limiteActualizado = LimiteGastoDto(
                            limiteGastoId = limiteId,
                            montoLimite = montoLimite,
                            categoriaId = categoriaId,
                            periodo = periodo,
                            usuarioId = usuarioId
                        )
                        limiteViewModel.actualizarLimite(limiteId, limiteActualizado)
                        navHostController.navigate("limites") {
                            popUpTo("limites") { inclusive = true }
                        }
                    },
                    onCancel = { navHostController.popBackStack() }, usuarioId = usuarioId
                )
            }
        }


        //  LISTA DE METAS
        composable("metaahorros") {
            val metaViewModel = hiltViewModel<MetaViewModel>()
            val usuarioId = loginViewModel.uiState.collectAsState().value.usuarioId ?: 0

            LaunchedEffect(usuarioId) {
                if (usuarioId != 0) {
                    metaViewModel.cargarMetas(usuarioId)
                }
            }

            MetaListScreen(
                viewModel = metaViewModel,
                onBackClick = { navHostController.popBackStack() },
                onAgregarMetaClick = {
                    navHostController.navigate("meta_nueva")
                },
                onMetaClick = { metaId ->
                    navHostController.navigate("meta_detalle/$metaId")
                },
                onAgregarMontoClick = { metaId ->
                    navHostController.navigate("meta_montoahorro/$metaId")
                }
            )
        }

        // NUEVA META
        composable("meta_nueva") {
            val metaViewModel = hiltViewModel<MetaViewModel>()
            val loginViewModel: LoginViewModel = hiltViewModel()

            val usuarioId = loginViewModel.uiState.collectAsState().value.usuarioId ?: 0

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
                        )
                    )
                    navHostController.navigate("metaahorros") {
                        popUpTo("metaahorros") { inclusive = true }
                    }
                },
                onCancel = { navHostController.popBackStack() },
                onImagenSeleccionada = { /* manejar imagen si deseas */ }
            )
        }

// 📌 DETALLE META
        composable(
            route = "meta_detalle/{metaId}",
            arguments = listOf(navArgument("metaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val metaId = backStackEntry.arguments?.getInt("metaId") ?: 0
            val metaViewModel = hiltViewModel<MetaViewModel>()

            val uiState by metaViewModel.uiState.collectAsState()
            val meta = uiState.metas.find { it.metaAhorroId == metaId }

            meta?.let {
                MetaDetalleScreen(
                    meta = it,
                    onBackClick = { navHostController.popBackStack() },
                    onEditarClick = { navHostController.navigate("meta_editar/$metaId") },
                    onEliminarClick = {
                    },
                    onEliminarConfirmado = {
                        metaViewModel.eliminarMeta(metaId)
                        navHostController.popBackStack()
                    }
                )
            }
        }

// EDITAR META
        composable(
            route = "meta_editar/{metaId}",
            arguments = listOf(navArgument("metaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val metaId = backStackEntry.arguments?.getInt("metaId") ?: 0
            val metaViewModel = hiltViewModel<MetaViewModel>()
            val uiState by metaViewModel.uiState.collectAsState()

            val meta = uiState.metas.find { it.metaAhorroId == metaId }

            meta?.let {
                MetaScreen(
                    metaParaEditar = it,
                    usuarioId = it.usuarioId,
                    onGuardar = { nombre, montoObjetivo, fechaFinal, contribucion, imagen, usuarioId ->
                        val metaActualizada = it.copy(
                            nombreMeta = nombre,
                            montoObjetivo = montoObjetivo,
                            fechaFinalizacion = fechaFinal,
                            contribucionRecurrente = if (contribucion) 0.0 else null,
                            imagen = imagen,
                            usuarioId = usuarioId
                        )
                        metaViewModel.actualizarMeta(metaId, metaActualizada)
                        navHostController.navigate("metaahorros") {
                            popUpTo("metaahorros") { inclusive = true }
                        }
                    },
                    onCancel = { navHostController.popBackStack() },
                    onImagenSeleccionada = { /* manejar imagen */ }
                )
            }
        }

    }
}
