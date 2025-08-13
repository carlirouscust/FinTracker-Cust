package ucne.edu.fintracker.data.local.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import ucne.edu.fintracker.data.local.dao.CategoriaDao
import ucne.edu.fintracker.data.local.toEntity
import ucne.edu.fintracker.presentation.remote.DataSource
import ucne.edu.fintracker.presentation.remote.Resource
import ucne.edu.fintracker.presentation.remote.dto.CategoriaDto
import javax.inject.Inject


class CategoriaRepository @Inject constructor(
    private val dataSource: DataSource,
    private val categoriaDao: CategoriaDao
) {
    fun getCategorias(usuarioId: Int): Flow<Resource<List<CategoriaDto>>> = flow {
        emit(Resource.Loading())
        Log.d("CategoriaRepository", "Llamando api.getCategoriasPorUsuario con usuarioId=$usuarioId")
        val categorias = dataSource.getCategoriasPorUsuario(usuarioId)
        Log.d("CategoriaRepository", "Categorias recibidas: $categorias")
        emit(Resource.Success(categorias))
    }.catch { e ->
        Log.e("CategoriaRepository", "Error en api.getCategoriasPorUsuario", e)
        emit(Resource.Error("Error al obtener categorías: ${e.message ?: "Error desconocido"}"))
    }



    fun createCategoria(categoriaDto: CategoriaDto): Flow<Resource<CategoriaDto>> = flow {
        emit(Resource.Loading())
        try {
            Log.d("CategoriaRepository", "Creando categoría: $categoriaDto")
            val result = dataSource.createCategoria(categoriaDto)
            categoriaDao.insert(result.toEntity())
            Log.d("CategoriaRepository", "Categoría creada: $result")
            emit(Resource.Success(result))
        } catch (e: Exception) {
            Log.e("CategoriaRepository", "Error al crear categoría", e)
            emit(Resource.Error("Error al crear categoría: ${e.message ?: "Error desconocido"}"))
        }
    }

}
