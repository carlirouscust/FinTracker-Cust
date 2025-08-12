package ucne.edu.fintracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
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
    fun getByUsuario(usuarioId: Int): Flow<List<MetaAhorroEntity>>

    @Query("SELECT * FROM MetasAhorro")
    fun getAll(): Flow<List<MetaAhorroEntity>>

    @Update
    suspend fun update(metaAhorro: MetaAhorroEntity)

    @Delete
    suspend fun delete(metaAhorro: MetaAhorroEntity)
}
