package ucne.edu.fintracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "LimitesGasto")
data class LimiteGastoEntity(
    @PrimaryKey(autoGenerate = true)
    val limiteGastoId: Int = 0,
    val categoriaId: Int,
    val montoLimite: Double,
    val periodo: String, // "Diario", "Mensual", etc.
    val gastadoActual: Double? = null,
    val usuarioId: Int
)

