package ucne.edu.fintracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ucne.edu.fintracker.data.local.entity.MetaAhorroEntity

@Dao
interface MetaAhorroDao {

    @Upsert
    suspend fun save(metaAhorro: MetaAhorroEntity)

    @Query("SELECT * FROM MetasAhorro WHERE metaAhorroId = :id LIMIT 1")
    suspend fun find(id: Int): MetaAhorroEntity?
    @Query("SELECT * FROM MetasAhorro WHERE usuarioId = :usuarioId")
    suspend fun findAllByUsuario(usuarioId: Int): List<MetaAhorroEntity>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(metaAhorro: MetaAhorroEntity)
    @Upsert
    suspend fun insertOrUpdateAll(metasAhorro: List<MetaAhorroEntity>)
    @Query("SELECT * FROM MetasAhorro WHERE usuarioId = :usuarioId")
    fun getByUsuario(usuarioId: Int): Flow<List<MetaAhorroEntity>>

    @Query("SELECT * FROM MetasAhorro")
    fun getAll(): Flow<List<MetaAhorroEntity>>

    @Update
    suspend fun update(metaAhorro: MetaAhorroEntity)

    @Delete
    suspend fun delete(metaAhorro: MetaAhorroEntity)
    @Query("DELETE FROM MetasAhorro WHERE metaAhorroId = :id")
    suspend fun deleteById(id: Int)
}
