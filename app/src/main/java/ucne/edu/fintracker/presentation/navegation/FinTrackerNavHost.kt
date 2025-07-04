package ucne.edu.fintracker.presentation.navegation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ucne.edu.fintracker.presentation.login.LoginViewModel
import ucne.edu.fintracker.presentation.login.LoginRegisterScreen

@Composable
fun FinTrackerNavHost(
    navHostController: NavHostController,
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    NavHost(
        navController = navHostController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginRegisterScreen(
                viewModel = loginViewModel
            )
        }
        composable("register") {
            LoginRegisterScreen(
                viewModel = loginViewModel
            )
        }
    }
}

