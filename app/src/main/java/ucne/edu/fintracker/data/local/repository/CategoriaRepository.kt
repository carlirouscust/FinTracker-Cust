package ucne.edu.fintracker.data.local.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ucne.edu.fintracker.presentation.remote.DataSource
import ucne.edu.fintracker.presentation.remote.Resource
import ucne.edu.fintracker.presentation.remote.dto.CategoriaDto
import javax.inject.Inject

class CategoriaRepository @Inject constructor(
    private val dataSource: DataSource
) {


    fun getCategorias(): Flow<Resource<List<CategoriaDto>>> = flow {
        try {
            emit(Resource.Loading())
            val categorias = dataSource.getCategoria()
            emit(Resource.Success(categorias))
        } catch (e: Exception) {
            emit(Resource.Error("Error al obtener categorías: ${e.message}"))
        }
    }

    // Crear una categoría
    fun createCategoria(categoriaDto: CategoriaDto): Flow<Resource<CategoriaDto>> = flow {
        try {
            emit(Resource.Loading())
            val result = dataSource.createCategoria(categoriaDto)
            emit(Resource.Success(result))
        } catch (e: Exception) {
            emit(Resource.Error("Error al crear categoría: ${e.message}"))
        }
    }



}