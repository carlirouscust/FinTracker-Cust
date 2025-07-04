package ucne.edu.fintracker.presentation.remote.dto

data class UsuarioDto(
    val usuarioId: Int? = null,
    val nombre: String,
    val apellido: String = "",
    val email: String,
    val contraseña: String,
    val fotoPerfil: String? = null,
    val divisa: String = "",
    val saldoTotal: Double = 0.0
)
