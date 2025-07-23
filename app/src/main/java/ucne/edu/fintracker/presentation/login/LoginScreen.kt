package ucne.edu.fintracker.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun LoginRegisterScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(36.dp))

        Row(verticalAlignment = Alignment.Bottom) {
            Text("Fin", fontWeight = FontWeight.Bold, fontSize = 28.sp, color = Color(0xFF232323))
            Text("Tracker", fontSize = 28.sp, color = Color(0xFF8A8A8A))
        }

        Spacer(modifier = Modifier.height(16.dp))

        TabRow(
            selectedTabIndex = state.tabIndex,
            containerColor = Color.White
        ) {
            Tab(
                selected = state.tabIndex == 0,
                onClick = { viewModel.changeTab(0) },
                text = { Text("Login", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = state.tabIndex == 1,
                onClick = { viewModel.changeTab(1) },
                text = { Text("Sign up", color = Color(0xFF8A8A8A)) }
            )
        }


        Spacer(modifier = Modifier.height(20.dp))

        LaunchedEffect(state.usuarioId) {
            if (state.usuarioId != 0) {
                navController.navigate("gastos") {
                    popUpTo("login") { inclusive = true }
                }
            }
        }

        if (state.tabIndex == 0) {
            LoginForm(
                email = state.loginEmail,
                password = state.loginPassword,
                onEmailChange = viewModel::onLoginEmailChange,
                onPasswordChange = viewModel::onLoginPasswordChange,
                onLoginClick = {
                    viewModel.login()
                },
                onNavigateToRegister = {
                    viewModel.changeTab(1)
                },
                onForgotPassword = {
                    navController.navigate("reset_password")
                },
                 loginError = state.loginError
            )
        } else {
            RegisterForm(
                nombre = state.registerNombre,
                apellido = state.registerApellido,
                email = state.registerEmail,
                password = state.registerPassword,
                onNombreChange = viewModel::onRegisterNombreChange,
                onApellidoChange = viewModel::onRegisterApellidoChange,
                onEmailChange = viewModel::onRegisterEmailChange,
                onPasswordChange = viewModel::onRegisterPasswordChange,
                onRegisterClick = {
                    scope.launch {
                        viewModel.registerUser(
                            onSuccess = {
                                println("Registrado correctamente")
                                viewModel.changeTab(0)
                            },
                            onError = { e -> println("Error: ${e.message}") }
                        )
                    }
                }
            )
        }

    }
}

@Composable
fun LoginForm(
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onNavigateToRegister: () -> Unit,
    loginError: Boolean,
    onForgotPassword: () -> Unit
) {
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    Column {
        OutlinedTextField(
            value = email,
            onValueChange = {
                onEmailChange(it)
                emailError = false
            },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .height(65.dp),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            isError = emailError,
            textStyle = LocalTextStyle.current.copy(color = Color.Black),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )
        if (emailError) {
            Text("El email es obligatorio", color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                onPasswordChange(it)
                passwordError = false
            },
            label = { Text("Contraseña") },
            modifier = Modifier
                .fillMaxWidth()
                .height(65.dp),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = null
                    )
                }
            },
            isError = passwordError,
            textStyle = LocalTextStyle.current.copy(color = Color.Black),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )
        if (passwordError) {
            Text("La contraseña es obligatoria", color = Color.Red, fontSize = 12.sp)
        }
        if (loginError) {
            Text(
                "Usuario o Contraseña incorrectos",
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        Text(
            text = "¿Olvidaste tu contraseña?",
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 4.dp)
                .clickable {
                    onForgotPassword()
                },
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                emailError = email.isBlank()
                passwordError = password.isBlank()
                if (!emailError && !passwordError) {
                    onLoginClick()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF85D844)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("Login", color = Color.White, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "¿No tienes una cuenta? ",
                fontSize = 13.sp,
                color = Color.Gray
            )
            TextButton(
                onClick = onNavigateToRegister,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    "Regístrate aquí",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

    }
}
@Composable
fun RegisterForm(
    nombre: String,
    apellido: String,
    email: String,
    password: String,
    onNombreChange: (String) -> Unit,
    onApellidoChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRegisterClick: () -> Unit
) {
    var nombreError by remember { mutableStateOf(false) }
    var apellidoError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    Column {
        OutlinedTextField(
            value = nombre,
            onValueChange = {
                onNombreChange(it)
                nombreError = false
            },
            label = { Text("Nombre") },
            modifier = Modifier
                .fillMaxWidth()
                .height(65.dp),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            isError = nombreError,
            textStyle = LocalTextStyle.current.copy(color = Color.Black),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = Color.Black
            )
        )
        if (nombreError) {
            Text("El nombre es obligatorio", color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = apellido,
            onValueChange = {
                onApellidoChange(it)
                apellidoError = false
            },
            label = { Text("Apellido") },
            modifier = Modifier
                .fillMaxWidth()
                .height(65.dp),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            isError = apellidoError,
            textStyle = LocalTextStyle.current.copy(color = Color.Black),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = Color.Black
            )
        )
        if (apellidoError) {
            Text("El apellido es obligatorio", color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                onEmailChange(it)
                emailError = false
            },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .height(65.dp),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            isError = emailError,
            textStyle = LocalTextStyle.current.copy(color = Color.Black),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = Color.Black
            )
        )
        if (emailError) {
            Text("El email es obligatorio", color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                onPasswordChange(it)
                passwordError = false
            },
            label = { Text("Contraseña") },
            modifier = Modifier
                .fillMaxWidth()
                .height(65.dp),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = null
                    )
                }
            },
            isError = passwordError,
            textStyle = LocalTextStyle.current.copy(color = Color.Black),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = Color.Black
            )
        )
        if (passwordError) {
            Text("La contraseña es obligatoria", color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                nombreError = nombre.isBlank()
                apellidoError = apellido.isBlank()
                emailError = email.isBlank()
                passwordError = password.isBlank()

                if (!nombreError && !apellidoError && !emailError && !passwordError) {
                    onRegisterClick()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF85D844)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("Sign up", color = Color.White, fontSize = 16.sp)
        }
    }
}

@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(14.dp),
        singleLine = true,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )
}