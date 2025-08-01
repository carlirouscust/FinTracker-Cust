package ucne.edu.fintracker.presentation.categoria

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.FloatingActionButton
import androidx.compose.ui.unit.sp
import ucne.edu.fintracker.presentation.remote.dto.CategoriaDto
import androidx.compose.material3.TextButton

@Composable
fun CategoriaListScreen(
    viewModel: CategoriaViewModel,
    usuarioId: Int,
    tipoFiltro: String = "",
    onBackClick: () -> Unit = {},
    onAgregarCategoriaClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(usuarioId) {
        viewModel.fetchCategorias(usuarioId)

        if (tipoFiltro.isBlank()) {
            viewModel.inicializarSinFiltro()
        }
    }

    LaunchedEffect(tipoFiltro) {
        if (tipoFiltro.isNotBlank()) {
            viewModel.onFiltroTipoChange(tipoFiltro)
        }
    }


    val categorias = viewModel.getCategoriasFiltradas()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.Black
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Categorías",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(16.dp))


        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                    viewModel.onFiltroTipoChange("Gasto")
                },
                colors = if (uiState.filtroTipo == "Gasto")
                    ButtonDefaults.buttonColors(containerColor = Color(0xFF85D844))
                else
                    ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                modifier = Modifier.weight(1f)
            ) {
                Text("Gastos", color = if (uiState.filtroTipo == "Gasto") Color.White else Color.Black)
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    viewModel.onFiltroTipoChange("Ingreso")
                },
                colors = if (uiState.filtroTipo == "Ingreso")
                    ButtonDefaults.buttonColors(containerColor = Color(0xFF85D844))
                else
                    ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                modifier = Modifier.weight(1f)
            ) {
                Text("Ingresos", color = if (uiState.filtroTipo == "Ingreso") Color.White else Color.Black)
            }
        }


        if (uiState.filtroTipo.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                onClick = {
                    viewModel.limpiarFiltro()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver todas las categorías", color = Color(0xFF85D844))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))


        if (categorias.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when {
                        uiState.categorias.isEmpty() -> "Sin categorías aún"
                        uiState.filtroTipo == "Gasto" -> "Sin categorías de gastos"
                        uiState.filtroTipo == "Ingreso" -> "Sin categorías de ingresos"
                        else -> "Sin categorías aún"
                    },
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(categorias) { cat ->
                    CategoriaBody(categoria = cat)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            FloatingActionButton(
                onClick = {

                    onAgregarCategoriaClick(if (uiState.filtroTipo.isBlank()) "Gasto" else uiState.filtroTipo)
                },
                containerColor = Color(0xFF85D844),
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Agregar Categoría", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}


@Composable
fun CategoriaBody(
    categoria: CategoriaDto
) {
    val backgroundColor = try {
        Color(android.graphics.Color.parseColor("#${categoria.colorFondo}"))
    } catch (e: Exception) {
        Color.LightGray
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(backgroundColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = categoria.icono,
                fontSize = 24.sp,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = categoria.nombre,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "Ver",
            tint = Color.Gray
        )
    }
}

