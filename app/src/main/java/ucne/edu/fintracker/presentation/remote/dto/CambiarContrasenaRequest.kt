package ucne.edu.fintracker.presentation.remote.dto

data class CambiarContrasenaRequest(
    val contrasenaActual: String,
    val nuevaContrasena: String
)
