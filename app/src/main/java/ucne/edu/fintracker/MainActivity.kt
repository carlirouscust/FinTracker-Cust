package ucne.edu.fintracker


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import ucne.edu.fintracker.presentation.login.LoginRegisterScreen
import ucne.edu.fintracker.ui.theme.FinTrackerTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinTrackerTheme {
                Scaffold { innerPadding ->
                    LoginRegisterScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
