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
import ucne.edu.fintracker.ui.theme.FinTrackerTheme

data class Gasto(
    val categoria: String,
    val monto: Float,
    val color: Color
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinTrackerTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    PantallaGrafico(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun PantallaGrafico(modifier: Modifier = Modifier) {
    val listaGastos = remember {
        mutableStateListOf(
            Gasto("Gasolina", 1200f, Color.Red),
            Gasto("Teléfono", 800f, Color(0xFFFFC107)),
            Gasto("Ocio", 500f, Color(0xFF8BC34A)),
            Gasto("Alimentación", 2500f, Color(0xFFFF9800))
        )
    }

    val total = listaGastos.sumOf { it.monto.toDouble() }.toFloat()
    val gastosPorPorcentaje = listaGastos.map {
        it.copy(monto = (it.monto / total) * 360f)
    }

    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Gastos - Junio 2025", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        DonutChart(gastos = gastosPorPorcentaje, total = total)
        Spacer(modifier = Modifier.height(24.dp))

        listaGastos.forEach {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Box(
                    Modifier
                        .size(16.dp)
                        .background(it.color, shape = CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("${it.categoria}: ${"%.0f".format(it.monto * total / 360)} RD$")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            listaGastos.add(Gasto("Nuevo", 400f, Color.Magenta))
        }) {
            Text("Agregar Gasto")
        }
    }
}

@Composable
fun DonutChart(gastos: List<Gasto>, total: Float) {
    Canvas(modifier = Modifier.size(200.dp)) {
        var startAngle = -90f

        gastos.forEach { gasto ->
            drawArc(
                color = gasto.color,
                startAngle = startAngle,
                sweepAngle = gasto.monto,
                useCenter = false,
                style = Stroke(width = 40f)
            )
            startAngle += gasto.monto
        }

        drawContext.canvas.nativeCanvas.apply {
            drawText(
                "${total.toInt()} RD$",
                size.width / 2,
                size.height / 2 + 15,
                android.graphics.Paint().apply {
                    textAlign = android.graphics.Paint.Align.CENTER
                    textSize = 40f
                    color = android.graphics.Color.parseColor("#2E7D32")
                    isFakeBoldText = true
                }
            )
        }
    }
}
