package ucne.edu.fintracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime

@Entity(tableName = "MetasAhorro")
data class MetaAhorroEntity(
    @PrimaryKey(autoGenerate = true)
    val metaAhorroId: Int = 0,
    val nombreMeta: String,
    val montoObjetivo: Double,
    val fechaFinalizacion: OffsetDateTime,
    val contribucionRecurrente: Double? = null,
    val imagen: String? = null,
    val montoActual: Double? = null,
    val montoAhorrado: Double? = null,
    val fechaMontoAhorrado: OffsetDateTime? = null,
    val usuarioId: Int,
    val syncPending: Boolean = false
)

