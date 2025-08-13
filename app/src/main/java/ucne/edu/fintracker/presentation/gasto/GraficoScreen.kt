package ucne.edu.fintracker.presentation.gasto

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun <T> BarChartGenerico(
    datos: List<T>,
    totalSelector: (T) -> Double,
    labelSelector: (T) -> String,
    barColor: Color,
    modifier: Modifier = Modifier
) {
    val maxTotal = datos.maxOfOrNull(totalSelector) ?: 1.0
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
                        .height(((totalSelector(dato) / maxTotal).toFloat().coerceIn(0f, 1f) * 100).dp)
                        .width(barWidth)
                        .background(barColor, RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(labelSelector(dato), fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun CustomTopBar(
    titulo: String,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun <T> GraficoSeccion(
    titulo: String,
    datos: List<T>,
    totalSelector: (T) -> Double,
    labelSelector: (T) -> String,
    barColor: Color
) {
    Column {
        Spacer(modifier = Modifier.height(24.dp))
        Text(titulo, fontWeight = FontWeight.SemiBold)
        BarChartGenerico(
            datos = datos,
            totalSelector = totalSelector,
            labelSelector = labelSelector,
            barColor = barColor,
            modifier = Modifier.fillMaxWidth()
        )
    }
}


@Composable
fun GraficoScreen(
    usuarioId: Int,
    gastoviewModel: GastoViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val datosMensuales = gastoviewModel.totalesMensuales.value
    val datosAnuales = gastoviewModel.totalesAnuales.value

    LaunchedEffect(usuarioId) {
        gastoviewModel.cambiarTipo("Gasto")
        gastoviewModel.cargarDatos(usuarioId)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { CustomTopBar("Gráfico de Gasto", onBackClick) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            GraficoSeccion(
                titulo = "Resumen mensual",
                datos = datosMensuales,
                totalSelector = { it.total },
                labelSelector = { it.mes },
                barColor = Color(0xFF8BC34A)
            )
            GraficoSeccion(
                titulo = "Resumen anual",
                datos = datosAnuales,
                totalSelector = { it.total },
                labelSelector = { it.ano.toString() },
                barColor = Color(0xFF03A9F4)
            )
        }
    }
}



