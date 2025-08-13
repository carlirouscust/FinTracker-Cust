package ucne.edu.fintracker.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ucne.edu.fintracker.R



@Composable
fun ResetPasswordScreen(
    onBackClick: () -> Unit,
    viewModel: ResetPasswordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DisposableEffect(Unit) { onDispose { viewModel.clearState() } }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            SimpleBackTopBar {
                when (uiState.step) {
                    ResetPasswordStep.EMAIL_INPUT -> onBackClick()
                    ResetPasswordStep.PASSWORD_RESET -> viewModel.goBackToEmailInput()
                    ResetPasswordStep.SUCCESS -> onBackClick()
                }
            }
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))

            Image(
                painter = painterResource(R.drawable.logo_fintracker),
                contentDescription = null,
                modifier = Modifier
                    .height(280.dp)
                    .padding(bottom = 5.dp)
            )

            when (uiState.step) {
                ResetPasswordStep.EMAIL_INPUT -> EmailInputStep(
                    email = uiState.email,
                    onEmailChange = viewModel::onEmailChange,
                    onVerifyClick = viewModel::verifyEmail,
                    isLoading = uiState.isLoading,
                    error = uiState.error,
                    emailError = uiState.emailError
                )

                ResetPasswordStep.PASSWORD_RESET -> PasswordResetStep(
                    email = uiState.email,
                    newPassword = uiState.newPassword,
                    confirmPassword = uiState.confirmPassword,
                    onNewPasswordChange = viewModel::onNewPasswordChange,
                    onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
                    onUpdateClick = viewModel::updatePassword,
                    isLoading = uiState.isLoading,
                    error = uiState.error,
                    passwordError = uiState.passwordError,
                    confirmPasswordError = uiState.confirmPasswordError
                )

                ResetPasswordStep.SUCCESS -> SuccessStep(
                    email = uiState.email,
                    onBackToLogin = onBackClick
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleBackTopBar(onClick: () -> Unit) {
    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(onClick = onClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.Black
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
    )
}

@Composable
fun PrimaryButton(
    text: String,
    loading: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF85D844)),
        shape = RoundedCornerShape(24.dp),
        enabled = enabled
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Text(text, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    isError: Boolean = false,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
        trailingIcon = trailingIcon,
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp),
        shape = RoundedCornerShape(14.dp),
        singleLine = true,
        isError = isError,
        enabled = enabled,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean,
    enabled: Boolean
) {
    var visible by remember { mutableStateOf(false) }
    AppTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        icon = Icons.Default.Lock,
        isError = isError,
        enabled = enabled,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { visible = !visible }) {
                Icon(
                    imageVector = if (visible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = null
                )
            }
        }
    )
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black,
        textAlign = TextAlign.Center
    )
}

@Composable
fun SectionSubtitle(
    text: String,
    color: Color = Color.Gray,
    size: Int = 16
) {
    Text(
        text = text,
        fontSize = size.sp,
        color = color,
        textAlign = TextAlign.Center,
        lineHeight = 22.sp
    )
}

@Composable
fun ErrorText(text: String) {
    Text(
        text = text,
        color = Color.Red,
        fontSize = 14.sp,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun EmailInputStep(
    email: String,
    onEmailChange: (String) -> Unit,
    onVerifyClick: () -> Unit,
    isLoading: Boolean,
    error: String?,
    emailError: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        SectionTitle("Recuperar Contraseña")
        Spacer(Modifier.height(8.dp))
        SectionSubtitle("Ingresa tu email para verificar tu cuenta")

        Spacer(Modifier.height(32.dp))

        AppTextField(
            value = email,
            onValueChange = onEmailChange,
            label = "Email",
            icon = Icons.Default.Email,
            isError = emailError,
            enabled = !isLoading,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
        )

        if (error != null) {
            Spacer(Modifier.height(8.dp))
            ErrorText(error)
        }

        Spacer(Modifier.height(24.dp))

        PrimaryButton(
            text = "Verificar Email",
            loading = isLoading,
            enabled = !isLoading,
            onClick = onVerifyClick
        )
    }
}

@Composable
private fun PasswordResetStep(
    email: String,
    newPassword: String,
    confirmPassword: String,
    onNewPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onUpdateClick: () -> Unit,
    isLoading: Boolean,
    error: String?,
    passwordError: Boolean,
    confirmPasswordError: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        SectionTitle("Nueva Contraseña")
        Spacer(Modifier.height(8.dp))
        SectionSubtitle("Cuenta verificada: $email", color = Color(0xFF4CAF50), size = 14)
        Spacer(Modifier.height(8.dp))
        SectionSubtitle("Ingresa tu nueva contraseña")

        Spacer(Modifier.height(32.dp))

        PasswordField(
            value = newPassword,
            onValueChange = onNewPasswordChange,
            label = "Nueva Contraseña",
            isError = passwordError,
            enabled = !isLoading
        )

        if (passwordError) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Debe tener 8+ caracteres, letras, números y símbolos",
                color = Color.Red,
                fontSize = 12.sp
            )
        }

        Spacer(Modifier.height(16.dp))

        PasswordField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = "Confirmar Contraseña",
            isError = confirmPasswordError,
            enabled = !isLoading
        )

        if (error != null) {
            Spacer(Modifier.height(8.dp))
            ErrorText(error)
        }

        Spacer(Modifier.height(24.dp))

        PrimaryButton(
            text = "Actualizar Contraseña",
            loading = isLoading,
            enabled = !isLoading,
            onClick = onUpdateClick
        )
    }
}

@Composable
private fun SuccessStep(
    email: String,
    onBackToLogin: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 32.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(80.dp)
        )

        Spacer(Modifier.height(24.dp))

        SectionTitle("¡Contraseña Actualizada!")
        Spacer(Modifier.height(16.dp))
        SectionSubtitle("Tu contraseña ha sido actualizada exitosamente para:")
        Spacer(Modifier.height(8.dp))
        Text(
            text = email,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        SectionSubtitle("Ya puedes iniciar sesión con tu nueva contraseña.", size = 14)

        Spacer(Modifier.height(32.dp))

        PrimaryButton(
            text = "Ir al Login",
            loading = false,
            onClick = onBackToLogin
        )
    }
}
