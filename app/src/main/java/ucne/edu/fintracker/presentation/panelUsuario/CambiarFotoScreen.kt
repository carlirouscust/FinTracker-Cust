package ucne.edu.fintracker.presentation.panelUsuario

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import ucne.edu.fintracker.remote.ImagenAdapter
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

private const val FOTO_DE_PERFIL = "Foto de perfil"

@Composable
fun CambiarFotoScreen(
    usuarioId: Int,
    onNavigateBack: () -> Unit,
    viewModel: CambiarFotoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val photoUri = rememberPhotoUri(context)

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val path = ImagenAdapter(context, it)
            if (path != null) {
                viewModel.seleccionarFotoDesdeGaleria(path)
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            val path = ImagenAdapter(context, photoUri)
            if (path != null) {
                viewModel.seleccionarFotoDesdeCamara(path)
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted -> if (isGranted) cameraLauncher.launch(photoUri) }

    LaunchedEffect(usuarioId) {
        viewModel.cargarDatosUsuario(usuarioId)
    }

    LaunchedEffect(uiState.fotoGuardadaExitosamente) {
        if (uiState.fotoGuardadaExitosamente) {
            kotlinx.coroutines.delay(1500)
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = { CambiarFotoTopBar(onNavigateBack) }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when {
                uiState.isLoading -> LoadingContent()
                uiState.isError -> ErrorContent(uiState.errorMessage) {
                    viewModel.limpiarError()
                    viewModel.cargarDatosUsuario(usuarioId)
                }
                else -> Content(
                    uiState = uiState,
                    context = context,
                    galleryLauncher = galleryLauncher,
                    cameraLauncher = cameraLauncher,
                    cameraPermissionLauncher = cameraPermissionLauncher,
                    photoUri = photoUri,
                    onGuardar = { viewModel.guardarFotoPerfil() }
                )
            }
        }
    }

    if (uiState.fotoGuardadaExitosamente) {
        FotoGuardadaExitosamente()
    }
}

@Composable
private fun rememberPhotoUri(context: Context): Uri {
    return remember {
        val photoFile = File(
            context.cacheDir,
            "foto_perfil_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.jpg"
        )
        FileProvider.getUriForFile(context, "${context.packageName}.provider", photoFile)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CambiarFotoTopBar(onNavigateBack: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = FOTO_DE_PERFIL,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium)
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = MaterialTheme.colorScheme.onSurface)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
    )
}

@Composable
private fun FotoGuardadaExitosamente() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(32.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Éxito",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Foto guardada exitosamente",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(errorMessage: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
            Icon(Icons.Default.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text("Reintentar")
            }
        }
    }
}

@Composable
private fun Content(
    uiState: CambiarFotoUiState,
    context: Context,
    galleryLauncher: ManagedActivityResultLauncher<String, Uri?>,
    cameraLauncher: ManagedActivityResultLauncher<Uri, Boolean>,
    cameraPermissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
    photoUri: Uri,
    onGuardar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(40.dp))

        PerfilFoto(uiState)

        Spacer(Modifier.height(16.dp))

        Text(
            text = "${uiState.nombreUsuario} ${uiState.apellidoUsuario}".trim(),
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(48.dp))

        Text(
            text = "Cambiar foto de perfil",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        OpcionFoto(
            titulo = "Subir foto",
            icono = Icons.Default.PhotoLibrary,
            onClick = { galleryLauncher.launch("image/*") }
        )

        Spacer(Modifier.height(12.dp))

        OpcionFoto(
            titulo = "Tomar foto",
            icono = Icons.Default.PhotoCamera,
            onClick = {
                val permission = Manifest.permission.CAMERA
                if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                    cameraLauncher.launch(photoUri)
                } else {
                    cameraPermissionLauncher.launch(permission)
                }
            }
        )

        Spacer(Modifier.weight(1f))

        if (uiState.fotoPerfilPath != null) {
            GuardarFotoButton(onGuardar, uiState.isUploadingFoto)
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun PerfilFoto(uiState: CambiarFotoUiState) {
    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondaryContainer),
        contentAlignment = Alignment.Center
    ) {
        when {
            uiState.fotoPerfilPath != null -> AsyncImage(
                model = File(uiState.fotoPerfilPath),
                contentDescription = FOTO_DE_PERFIL,
                modifier = Modifier.fillMaxSize().clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            uiState.fotoPerfilUrl != null -> AsyncImage(
                model = uiState.fotoPerfilUrl,
                contentDescription = FOTO_DE_PERFIL,
                modifier = Modifier.fillMaxSize().clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            else -> Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Foto de perfil por defecto",
                modifier = Modifier.size(70.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
private fun GuardarFotoButton(onGuardar: () -> Unit, isUploadingFoto: Boolean) {
    Button(
        onClick = onGuardar,
        modifier = Modifier.fillMaxWidth().height(48.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
        enabled = !isUploadingFoto
    ) {
        if (isUploadingFoto) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                Spacer(Modifier.width(8.dp))
                Text("Guardando...", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
        } else {
            Text("Guardar", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}


@Composable
fun OpcionFoto(
    titulo: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icono,
            contentDescription = titulo,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = titulo,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}