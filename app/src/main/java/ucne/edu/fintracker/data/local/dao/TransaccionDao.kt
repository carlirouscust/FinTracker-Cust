package ucne.edu.fintracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ucne.edu.fintracker.data.local.entity.TransaccionEntity

@Dao
interface TransaccionDao {

    @Upsert
    suspend fun save(transaccion: TransaccionEntity)
    @Upsert
    suspend fun insertOrUpdateAll(transacciones: List<TransaccionEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transacciones: List<TransaccionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaccion: TransaccionEntity)

    @Query("SELECT * FROM Transacciones WHERE transaccionId = :id LIMIT 1")
    suspend fun find(id: Int): TransaccionEntity?

    @Query("SELECT * FROM Transacciones WHERE usuarioId = :usuarioId")
    fun getByUsuario(usuarioId: Int): Flow<List<TransaccionEntity>>

    @Query("SELECT * FROM Transacciones")
    fun getAll(): Flow<List<TransaccionEntity>>

    @Query("SELECT * FROM transacciones WHERE syncPending = 1")
    suspend fun getPendientesSync(): List<TransaccionEntity>
    @Update
    suspend fun update(transaccion: TransaccionEntity)

    @Delete
    suspend fun delete(transaccion: TransaccionEntity)
    @Query("DELETE FROM Transacciones WHERE transaccionId = :id")
    suspend fun deleteById(id: Int)
}
