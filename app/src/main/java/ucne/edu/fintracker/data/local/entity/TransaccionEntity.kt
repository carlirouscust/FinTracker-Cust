package ucne.edu.fintracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime

@Entity(tableName = "Transacciones")
data class TransaccionEntity(
    @PrimaryKey(autoGenerate = true)
    val transaccionId: Int = 0,
    val monto: Double,
    val categoriaId: Int,
    val fecha: OffsetDateTime,
    val notas: String? = null,
    val tipo: String,
    val usuarioId: Int,
    val syncPending: Boolean = true
)

