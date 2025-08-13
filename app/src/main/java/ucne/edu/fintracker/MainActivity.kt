package ucne.edu.fintracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ucne.edu.fintracker.presentation.navegation.FinTrackerNavHost
import ucne.edu.fintracker.remote.FinTrackerApi
import ucne.edu.fintracker.presentation.theme.ThemeViewModel
import ucne.edu.fintracker.ui.theme.FinTrackerTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var finTrackerApi: FinTrackerApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeViewModel: ThemeViewModel = hiltViewModel()
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

            FinTrackerTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()

                Scaffold { innerPadding ->
                    FinTrackerNavHost(
                        navHostController = navController,
                        finTrackerApi = finTrackerApi,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}