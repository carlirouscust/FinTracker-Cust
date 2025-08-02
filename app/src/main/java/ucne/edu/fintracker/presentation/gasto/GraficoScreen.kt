package ucne.edu.fintracker.presentation.gasto

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.items
import ucne.edu.fintracker.presentation.remote.dto.TotalMes

@Composable
fun BarChart(datos: List<TotalMes>, modifier: Modifier = Modifier) {
    val maxTotal = datos.maxOfOrNull { it.total } ?: 1.0
    val barWidth = 24.dp

    Row(
        modifier = modifier.height(150.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        datos.forEach { dato ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .height(((dato.total / maxTotal).toFloat().coerceIn(0f, 1f) * 100).dp)
                        .width(barWidth)
                        .background(Color(0xFF8BC34A), RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(dato.mes, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun GraficoScreen(
    usuarioId: Int,
    viewModel: GastoViewModel)
{

    LaunchedEffect(usuarioId) {
        viewModel.cargarDatos(usuarioId)
    }

    val tipo by viewModel.uiState.collectAsState()
    val datosMensuales = viewModel.totalesMensuales.value
    val datosAnuales = viewModel.totalesAnuales.value

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Gráfico de ${tipo.tipoSeleccionado}s", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        Text("Resumen mensual")
        BarChart(datosMensuales)

        Spacer(modifier = Modifier.height(24.dp))

        Text("Resumen anual")
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(datosAnuales) { dato ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${dato.ano}", fontWeight = FontWeight.Bold)
                    Text("%.0f RD$".format(dato.total), color = Color.Gray)
                }
            }
        }
    }
}


