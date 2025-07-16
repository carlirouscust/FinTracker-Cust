package ucne.edu.fintracker.presentation.navegation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import ucne.edu.fintracker.presentation.remote.dto.CategoriaDto

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
            GastoListScreen(
                viewModel = gastoViewModel,
                onNuevoClick = {
                    navHostController.navigate("gasto_nuevo")
                },
                navController = navHostController
            )
        }


        composable("gasto_nuevo") {
            val gastoViewModel = hiltViewModel<GastoViewModel>()

            GastoScreen(
                categorias = listOf("Comida", "Transporte", "Salario", "Internet", "Otros"),
                onGuardar = { tipo, monto, categoria, fecha, notas ->
                    val nuevaTransaccion = TransaccionDto(
                        transaccionId = DateUtil.generateUniqueId(),
                        monto = monto,
                        categoriaId = 0,
                        categoria = CategoriaDto(0, categoria),
                        fecha = DateUtil.parseFecha(fecha),
                        notas = notas,
                        tipo = tipo
                    )
                    gastoViewModel.agregarTransaccion(nuevaTransaccion)
                    navHostController.popBackStack()
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
                },
                onCancel = {
                    navHostController.popBackStack()
                }
            )
        }
    }
}
