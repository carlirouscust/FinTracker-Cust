package ucne.edu.fintracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ucne.edu.fintracker.data.local.entity.LimiteGastoEntity

@Dao
interface LimiteGastoDao {

    @Upsert
    suspend fun save(limiteGasto: LimiteGastoEntity)

    @Query("SELECT * FROM LimitesGasto WHERE limiteGastoId = :id LIMIT 1")
    suspend fun find(id: Int): LimiteGastoEntity?
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(limiteGasto: LimiteGastoEntity)
    @Query("SELECT * FROM LimitesGasto WHERE usuarioId = :usuarioId")
    fun getByUsuario(usuarioId: Int): Flow<List<LimiteGastoEntity>>

    @Query("SELECT * FROM LimitesGasto")
    fun getAll(): Flow<List<LimiteGastoEntity>>

    @Update
    suspend fun update(limiteGasto: LimiteGastoEntity)

    @Delete
    suspend fun delete(limiteGasto: LimiteGastoEntity)
    @Query("DELETE FROM LimitesGasto WHERE limiteGastoId = :id")
    suspend fun deleteById(id: Int)
}
