package ucne.edu.fintracker


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ucne.edu.fintracker.presentation.navegation.FinTrackerNavHost
import ucne.edu.fintracker.presentation.remote.FinTrackerApi
import ucne.edu.fintracker.ui.theme.FinTrackerTheme
import javax.inject.Inject
import androidx.compose.material3.Scaffold

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var finTrackerApi: FinTrackerApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinTrackerTheme {
                val navController = rememberNavController()

                Scaffold { innerPadding ->
                    FinTrackerNavHost(navHostController = rememberNavController(),
                    finTrackerApi = finTrackerApi,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

}

