package ucne.edu.fintracker.data.local.database



import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ucne.edu.fintracker.data.local.dao.CategoriaDao
import ucne.edu.fintracker.data.local.dao.LimiteGastoDao
import ucne.edu.fintracker.data.local.dao.MetaAhorroDao
import ucne.edu.fintracker.data.local.dao.PagoRecurrenteDao
import ucne.edu.fintracker.data.local.dao.TransaccionDao
import ucne.edu.fintracker.data.local.dao.UsuarioDao
import ucne.edu.fintracker.data.local.entity.UsuarioEntity
import ucne.edu.fintracker.data.local.entity.CategoriaEntity
import ucne.edu.fintracker.data.local.entity.LimiteGastoEntity
import ucne.edu.fintracker.data.local.entity.MetaAhorroEntity
import ucne.edu.fintracker.data.local.entity.PagoRecurrenteEntity
import ucne.edu.fintracker.data.local.entity.TransaccionEntity

@Database(
    entities = [
        UsuarioEntity::class,
        TransaccionEntity::class,
        PagoRecurrenteEntity::class,
        MetaAhorroEntity::class,
        LimiteGastoEntity::class,
        CategoriaEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FinTrackerDb : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun transaccionDao(): TransaccionDao
    abstract fun pagoRecurrenteDao(): PagoRecurrenteDao
    abstract fun metaAhorroDao(): MetaAhorroDao
    abstract fun limiteGastoDao(): LimiteGastoDao
    abstract fun categoriaDao(): CategoriaDao
}

