package ucne.edu.fintracker.presentation.metaahorro

import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assistant
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage

@Composable
fun MetaListScreen(
    viewModel: MetaViewModel,
    navController: NavHostController,
    onBackClick: () -> Unit,
    usuarioId: Int,
    onAgregarMetaClick: () -> Unit,
    onMetaClick: (Int) -> Unit,
    onAgregarMontoClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.Black
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Metas de Ahorro",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAgregarMetaClick,
                containerColor = Color(0xFF8BC34A)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar Meta",
                    tint = Color.White
                )
            }
        },
            bottomBar = {
                NavigationBar(
                    containerColor = Color.White,
                ) {
                    NavigationBarItem(
                        selected = true,
                        onClick = { navController.navigate("gastos") },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { navController.navigate("chatIA/$usuarioId") },
                        icon = { Icon(Icons.Default.Assistant, contentDescription = "IA Asesor") },
                        label = { Text("IA Asesor") }
                    )
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    NavigationBarItem(
                        selected = currentRoute == "metaahorros/$usuarioId",
                        onClick = {
                            navController.navigate("metaahorros/$usuarioId") {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                            }
                        },
                        icon = { Icon(Icons.Default.Star, contentDescription = "Metas") },
                        label = { Text("Metas") }
                    )
                }

            }
            ) { padding ->
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                            .padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.error != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                            .padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = uiState.error ?: "Error desconocido",
                            color = Color.Red,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                            .padding(padding)
                    ) {
                        Text(
                            text = "Mis metas",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(16.dp)
                        )

                        LazyColumn {
                            items(uiState.metas) { meta ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "Meta",
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 14.sp,
                                                color = Color.Gray
                                            )
                                            Spacer(Modifier.height(4.dp))
                                            Text(
                                                text = meta.nombreMeta,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp
                                            )
                                            Spacer(Modifier.height(4.dp))
                                            Text(
                                                text = "RD$ ${meta.montoObjetivo}",
                                                fontSize = 14.sp,
                                                color = Color.DarkGray
                                            )

                                            Spacer(Modifier.height(8.dp))
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Button(
                                                    onClick = { onMetaClick(meta.metaAhorroId) },
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = Color.LightGray,
                                                        contentColor = Color.Black
                                                    ),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Text("Ver")
                                                }
                                                Button(
                                                    onClick = { onAgregarMontoClick(meta.metaAhorroId) },
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = Color.LightGray,
                                                        contentColor = Color.Black
                                                    ),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Text("Agregar")
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        if (meta.imagen != null) {
                                            AsyncImage(
                                                model = meta.imagen,
                                                contentDescription = "Imagen de la meta",
                                                modifier = Modifier
                                                    .size(64.dp)
                                                    .clip(RoundedCornerShape(12.dp)),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .size(64.dp)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(Color(0xFFEFEFEF)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("Sin imagen", fontSize = 10.sp, color = Color.Gray)
                                            }
                                        }
                                    }
                                    Divider(modifier = Modifier.padding(top = 8.dp))
                                }
                             }
                        }
                    }
                }
            }
        }
}
