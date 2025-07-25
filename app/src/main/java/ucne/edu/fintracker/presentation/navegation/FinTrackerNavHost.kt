package ucne.edu.fintracker.presentation.navegation

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
import androidx.navigation.navArgument
import ucne.edu.fintracker.presentation.limitegasto.LimiteDetalleScreen
import ucne.edu.fintracker.presentation.asesorIA.ChatIaScreen
import ucne.edu.fintracker.presentation.limitegasto.LimiteScreen
import ucne.edu.fintracker.presentation.limitegasto.LimiteListScreen
import ucne.edu.fintracker.presentation.limitegasto.LimiteViewModel
import ucne.edu.fintracker.presentation.metaahorro.MetaDetalleScreen
import ucne.edu.fintracker.presentation.metaahorro.MetaListScreen
import ucne.edu.fintracker.presentation.metaahorro.MetaViewModel
import ucne.edu.fintracker.presentation.metaahorro.NuevaMetaScreen
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
    loginViewModel: LoginViewModel = hiltViewModel(),
    finTrackerApi: FinTrackerApi = hiltViewModel()
) {
    NavHost(
        navController = navHostController,
        startDestination = "login",
        modifier = modifier
    ) {
        composable("login") {
            val loginViewModel = hiltViewModel<LoginViewModel>()
            LoginRegisterScreen(
                navController = navHostController,
                viewModel = loginViewModel
            )
        }
        composable("register") {
            val loginViewModel = hiltViewModel<LoginViewModel>()
            LoginRegisterScreen(
                navController = navHostController,
                viewModel = loginViewModel
            )
        }
        composable("reset_password") {
            ResetPasswordScreen(
                navController = navHostController,
                finTrackerApi = finTrackerApi
            )
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

            val categoriaUiState = categoriaViewModel.uiState.collectAsState().value
            val categoriasFiltradas = categoriaUiState.categorias

            GastoScreen(
                categorias = categoriasFiltradas.map { it.nombre },
                tipoInicial = tipoInicial,
                onGuardar = { tipoSeleccionado, monto, categoriaNombre, fecha, notas ->
                    val categoriaSeleccionada =
                        categoriasFiltradas.find { it.nombre == categoriaNombre }
                    if (categoriaSeleccionada != null) {
                        val fechaActualUtc = OffsetDateTime.now(ZoneOffset.UTC)

                        gastoViewModel.crearTransaccion(
                            TransaccionDto(
                                transaccionId = 0,
                                monto = monto,
                                categoriaId = categoriaSeleccionada.categoriaId,
                                fecha = fechaActualUtc,
                                notas = notas,
                                tipo = tipoSeleccionado
                            )
                        )

                        navHostController.navigate("gastos") {
                            popUpTo("gastos") { inclusive = true }
                        }
                    }
                },
                onCancel = {
                    navHostController.popBackStack()
                }
            )
        }





        composable("categoria/{tipo}") { backStackEntry ->
            val tipo = backStackEntry.arguments?.getString("tipo") ?: "Gasto"
            val categoriaVM = hiltViewModel<CategoriaViewModel>()
            CategoriaListScreen(
                viewModel = categoriaVM,
                tipoFiltro = tipo,
                onBackClick = { navHostController.popBackStack() },
                onAgregarCategoriaClick = { tipoActual ->
                    navHostController.navigate("categoria_nueva/${tipoActual}")
                },
                onCategoriaClick = { categoria ->
                    navHostController.navigate("categoria_detalle/${categoria.categoriaId}")
                }
            )
        }

        composable("categoria_nueva/{tipo}") { backStackEntry ->
            val tipo = backStackEntry.arguments?.getString("tipo") ?: "Gasto"
            val categoriaVM = hiltViewModel<CategoriaViewModel>()
            LaunchedEffect(tipo) {
                categoriaVM.onTipoChange(tipo)
            }
            CategoriaScreen(
                navController = navHostController,
                viewModel = categoriaVM,
                tipo = tipo,
                onGuardar = { _, _, _, _ ->
                    categoriaVM.saveCategoria {
                        navHostController.popBackStack()
                    }

//                    navHostController.navigate("categoria/{tipo}") {
//                        popUpTo("categoria/{tipo}") { inclusive = true }
//                    }
                },
                onCancel = {
                    navHostController.popBackStack()
                }
            )
        }

        composable("pagos") {
            val pagoViewModel = hiltViewModel<PagoViewModel>()

            val categorias by pagoViewModel.categorias.collectAsState()

            LaunchedEffect(Unit) {
                pagoViewModel.cargarPagosRecurrentes()
                pagoViewModel.cargarCategorias()
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

            PagoScreen(
                viewModel = pagoViewModel,
                pagoParaEditar = null,
                onGuardar = { monto, categoriaId, frecuencia, fechaInicio, fechaFin ->
                    pagoViewModel.crearPagoRecurrente(
                        PagoRecurrenteDto(
                            monto = monto,
                            categoriaId = categoriaId,
                            frecuencia = frecuencia,
                            fechaInicio = fechaInicio,
                            fechaFin = fechaFin
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
                        // aquí puedes mostrar diálogo si quieres
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

            val pago = uiState.pagos.find { it.pagoRecurrenteId == pagoId }

            if (pago != null) {
                PagoScreen(
                    viewModel = pagoViewModel,
                    pagoParaEditar = pago,
                    onGuardar = { monto, categoriaId, frecuencia, fechaInicio, fechaFin ->
                        val pagoActualizado = PagoRecurrenteDto(
                            pagoRecurrenteId = pagoId,
                            monto = monto,
                            categoriaId = categoriaId,
                            frecuencia = frecuencia,
                            fechaInicio = fechaInicio,
                            fechaFin = fechaFin
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

            val categorias by limiteViewModel.categorias.collectAsState()

            LaunchedEffect(Unit) {
                limiteViewModel.cargarLimites()
                limiteViewModel.cargarCategorias()
            }

            LimiteListScreen(
                viewModel = limiteViewModel,
                onAgregarLimiteClick = {
                    navHostController.navigate("limite_nuevo")
                },
                onBackClick = {
                    navHostController.popBackStack()
                },
                onLimiteClick = { limiteId ->
                    navHostController.navigate("limite_detalle/$limiteId")
                }
            )
        }

        composable("limite_nuevo") {
            val limiteViewModel = hiltViewModel<LimiteViewModel>()

            LimiteScreen(
                viewModel = limiteViewModel,
                limiteParaEditar = null,
                onGuardar = { montoLimite, categoriaId, periodo ->
                    limiteViewModel.crearLimite(
                        LimiteGastoDto(
                            montoLimite = montoLimite,
                            categoriaId = categoriaId,
                            periodo = periodo
                        )
                    )
                    navHostController.navigate("limites") {
                        popUpTo("limites") { inclusive = true }
                    }
                },
                onCancel = { navHostController.popBackStack() }
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
            val uiState by limiteViewModel.uiState.collectAsState()

            val limite = uiState.limites.find { it.limiteGastoId == limiteId }

            if (limite != null) {
                LimiteScreen(
                    viewModel = limiteViewModel,
                    limiteParaEditar = limite,
                    onGuardar = { montoLimite, categoriaId, periodo ->
                        val limiteActualizado = LimiteGastoDto(
                            limiteGastoId = limiteId,
                            montoLimite = montoLimite,
                            categoriaId = categoriaId,
                            periodo = periodo
                        )
                        limiteViewModel.actualizarLimite(limiteId, limiteActualizado)
                        navHostController.navigate("limites") {
                            popUpTo("limites") { inclusive = true }
                        }
                    },
                    onCancel = { navHostController.popBackStack() }
                )
            }
        }

        // 📌 LISTA DE METAS
        composable("metaahorros") {
            val metaViewModel = hiltViewModel<MetaViewModel>()

            // Cargar datos al entrar
            LaunchedEffect(Unit) {
                metaViewModel.cargarMetas()
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

// 📌 NUEVA META
        composable("meta_nueva") {
            val metaViewModel = hiltViewModel<MetaViewModel>()

            NuevaMetaScreen(
                metaParaEditar = null,
                onGuardar = { nombre, montoObjetivo, fechaFinal, contribucion, imagen ->
                    metaViewModel.crearMeta(
                        MetaAhorroDto(
                            nombreMeta = nombre,
                            montoObjetivo = montoObjetivo,
                            fechaFinalizacion = fechaFinal,
                            contribucionRecurrente = if (contribucion) 0.0 else null,
                            imagen = imagen,
                            montoAhorrado = 0.0,
                            fechaMontoAhorrado = fechaFinal // o la fecha actual
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

// 📌 EDITAR META
        composable(
            route = "meta_editar/{metaId}",
            arguments = listOf(navArgument("metaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val metaId = backStackEntry.arguments?.getInt("metaId") ?: 0
            val metaViewModel = hiltViewModel<MetaViewModel>()
            val uiState by metaViewModel.uiState.collectAsState()

            val meta = uiState.metas.find { it.metaAhorroId == metaId }

            meta?.let {
                NuevaMetaScreen(
                    metaParaEditar = it,
                    onGuardar = { nombre, montoObjetivo, fechaFinal, contribucion, imagen ->
                        val metaActualizada = it.copy(
                            nombreMeta = nombre,
                            montoObjetivo = montoObjetivo,
                            fechaFinalizacion = fechaFinal,
                            contribucionRecurrente = if (contribucion) 0.0 else null,
                            imagen = imagen
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

        composable("chatIA") {
            ChatIaScreen()
        }

    }
}
