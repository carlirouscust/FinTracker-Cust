package ucne.edu.fintracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ucne.edu.fintracker.data.local.entity.UsuarioEntity

@Dao
interface UsuarioDao {

    @Upsert
    suspend fun save(usuario: UsuarioEntity)

    @Query("SELECT * FROM Usuarios WHERE usuarioId = :id LIMIT 1")
    suspend fun find(id: Int): UsuarioEntity?

    @Query("" +
            "SELECT * FROM Usuarios " +
            "WHERE email = :email" +
            " LIMIT 1")
    suspend fun findByEmail(email: String): UsuarioEntity?

    @Query("SELECT * FROM Usuarios")
    fun getAll(): Flow<List<UsuarioEntity>>

    @Update
    suspend fun update(usuario: UsuarioEntity)

    @Delete
    suspend fun delete(usuario: UsuarioEntity)
}
