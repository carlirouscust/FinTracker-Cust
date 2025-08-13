package ucne.edu.fintracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Usuarios")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    val usuarioId: Int? = null,
    val nombre: String,
    val apellido: String = "",
    val email: String,
    val contraseña: String,
    val fotoPerfil: String? = null,
    val divisa: String = "",
    val saldoTotal: Double = 0.0
)

