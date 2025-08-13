package ucne.edu.fintracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Categorias")
data class CategoriaEntity(
    @PrimaryKey(autoGenerate = true)
    val categoriaId: Int = 0,
    val nombre: String = "",
    val tipo: String = "Gasto",
    val icono: String = "",
    val colorFondo: String = "FFFFFF",
    val usuarioId: Int,
    val syncPending: Boolean = false
)

