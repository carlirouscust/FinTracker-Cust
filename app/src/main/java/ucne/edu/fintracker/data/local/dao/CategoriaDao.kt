package ucne.edu.fintracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ucne.edu.fintracker.data.local.entity.CategoriaEntity

@Dao
interface CategoriaDao {

    @Upsert
    suspend fun save(categoria: CategoriaEntity)

    @Query("SELECT * FROM Categorias WHERE categoriaId = :id LIMIT 1")
    suspend fun find(id: Int): CategoriaEntity?
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(categoria: CategoriaEntity)
    @Query("SELECT * FROM Categorias WHERE usuarioId = :usuarioId")
    fun getByUsuario(usuarioId: Int): Flow<List<CategoriaEntity>>

    @Query("SELECT * FROM Categorias")
    fun getAll(): Flow<List<CategoriaEntity>>

    @Update
    suspend fun update(categoria: CategoriaEntity)

    @Delete
    suspend fun delete(categoria: CategoriaEntity)
    @Query("DELETE FROM Categorias WHERE categoriaId = :id")
    suspend fun deleteById(id: Int)
}
