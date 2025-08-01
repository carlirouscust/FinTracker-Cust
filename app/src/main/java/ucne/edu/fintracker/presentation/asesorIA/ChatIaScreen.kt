package ucne.edu.fintracker.presentation.asesorIA

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assistant
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.launch

@Composable
fun ChatIaScreen(
    navController: NavHostController,
    usuarioId: String,
    ChatIAViewModel: ChatIAViewModel = viewModel()
) {
    var prompt by remember { mutableStateOf("") }
    val uiState by ChatIAViewModel.uiState.collectAsState()
    var messages by remember { mutableStateOf(listOf<ChatMessage>()) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(uiState) {
        when (uiState) {
            is ChatIaUiState.Loading -> {
                messages = messages + ChatMessage("typing", isUser = false, isTyping = true)
            }

            is ChatIaUiState.Success -> {
                messages = messages.filterNot { it.isTyping } +
                        ChatMessage((uiState as ChatIaUiState.Success).outputText, isUser = false)
            }

            is ChatIaUiState.Error -> {
                messages = messages.filterNot { it.isTyping } +
                        ChatMessage("Error: ${(uiState as ChatIaUiState.Error).errorMessage}", isUser = false)
            }

            else -> {}
        }
    }

    LaunchedEffect(messages.size) {
        coroutineScope.launch {
            if (messages.isNotEmpty()) {
                listState.animateScrollToItem(messages.lastIndex)
            }
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                NavigationBarItem(
                    selected = currentRoute == "gastos",
                    onClick = { navController.navigate("gastos") },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home", fontSize = 10.sp) }
                )

                NavigationBarItem(
                    selected = currentRoute == "chatIA",
                    onClick = { navController.navigate("chatIA/$usuarioId") },
                    icon = { Icon(Icons.Default.Assistant, contentDescription = "IA Asesor") },
                    label = { Text("IA Asesor", fontSize = 10.sp) }
                )

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
                    label = { Text("Metas", fontSize = 10.sp) }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(8.dp)
        ) {
            Text(
                text = "Asesor de IA",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(8.dp)
            )

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(messages) { message ->
                    if (message.isTyping) {
                        TypingBubble()
                    } else {
                        ChatBubble(message = message)
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    placeholder = {
                        Text(
                            "Pregúntame cualquier cosa...",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        disabledBorderColor = Color.Transparent,
                        errorBorderColor = Color.Transparent,
                        focusedContainerColor = Color(0xFFF1F3F4),
                        unfocusedContainerColor = Color(0xFFF1F3F4)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        if (prompt.isNotBlank()) {
                            messages = messages + ChatMessage(prompt, isUser = true)
                            ChatIAViewModel.sendPrompt(prompt)
                            prompt = ""
                        }
                    }
                ) {
                    Text("Enviar")
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val bubbleColor = if (message.isUser) Color(0xFF4CAF50) else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (message.isUser) Color.White else MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .background(color = bubbleColor, shape = RoundedCornerShape(16.dp))
                .padding(12.dp)
                .widthIn(max = 280.dp)
        ) {
            Text(
                text = message.text,
                color = textColor,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Start
            )
        }
    }

    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun TypingBubble() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(12.dp)
        ) {
            TypingDots()
        }
    }

    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun TypingDots() {
    val transition = rememberInfiniteTransition(label = "")
    val dot1 by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )
    val dot2 by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = LinearEasing, delayMillis = 150),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )
    val dot3 by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = LinearEasing, delayMillis = 300),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Dot(opacity = dot1)
        Dot(opacity = dot2)
        Dot(opacity = dot3)
    }
}

@Composable
fun Dot(opacity: Float) {
    Box(
        modifier = Modifier
            .size(8.dp)
            .background(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = opacity),
                shape = RoundedCornerShape(50)
            )
    )
}

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val isTyping: Boolean = false
)
