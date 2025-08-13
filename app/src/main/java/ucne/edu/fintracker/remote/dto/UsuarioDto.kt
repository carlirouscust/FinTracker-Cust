package ucne.edu.fintracker.remote.dto

data class UsuarioDto(
    val usuarioId: Int? = null,
    val nombre: String,
    val apellido: String = "",
    val email: String,
    val contraseña: String,
    val fotoPerfil: String?,
    val divisa: String = "",
    val saldoTotal: Double = 0.0
)
