package ucne.edu.fintracker.presentation.navegation

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
    }
}
