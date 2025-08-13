package ucne.edu.fintracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime

@Entity(tableName = "PagosRecurrentes")
data class PagoRecurrenteEntity(
    @PrimaryKey(autoGenerate = true)
    val pagoRecurrenteId: Int = 0,
    val monto: Double,
    val categoriaId: Int,
    val frecuencia: String,
    val fechaInicio: OffsetDateTime,
    val fechaFin: OffsetDateTime? = null,
    val activo: Boolean = true,
    val usuarioId: Int,
    val syncPending: Boolean = false
)
