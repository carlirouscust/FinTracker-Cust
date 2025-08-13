package ucne.edu.fintracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ucne.edu.fintracker.data.local.entity.PagoRecurrenteEntity

@Dao
interface PagoRecurrenteDao {

    @Upsert
    suspend fun save(pagoRecurrente: PagoRecurrenteEntity)

    @Query("SELECT * FROM PagosRecurrentes WHERE pagoRecurrenteId = :id LIMIT 1")
    suspend fun find(id: Int): PagoRecurrenteEntity?
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pagoRecurrente: PagoRecurrenteEntity)
    @Upsert
    suspend fun insertOrUpdateAll(pagosRecurrentes: List<PagoRecurrenteEntity>)

    @Query("SELECT * FROM PagosRecurrentes WHERE usuarioId = :usuarioId")
    fun getByUsuario(usuarioId: Int): Flow<List<PagoRecurrenteEntity>>

    @Query("SELECT * FROM PagosRecurrentes WHERE activo = 1")
    fun getActivos(): Flow<List<PagoRecurrenteEntity>>

    @Query("SELECT * FROM PagosRecurrentes")
    fun getAll(): Flow<List<PagoRecurrenteEntity>>

    @Update
    suspend fun update(pagoRecurrente: PagoRecurrenteEntity)

    @Delete
    suspend fun delete(pagoRecurrente: PagoRecurrenteEntity)
    @Query("DELETE FROM PagosRecurrentes WHERE pagoRecurrenteId = :id")
    suspend fun deleteById(id: Int)
}
