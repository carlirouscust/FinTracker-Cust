package ucne.edu.fintracker.data.local.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ucne.edu.fintracker.presentation.remote.DataSource
import ucne.edu.fintracker.presentation.remote.Resource
import ucne.edu.fintracker.presentation.remote.dto.MetaAhorroDto
import javax.inject.Inject


class MetaRepository @Inject constructor(
    private val dataSource: DataSource
) {

    //  Obtener todas las metas filtradas por usuarioId
    fun getMetas(usuarioId: Int): Flow<Resource<List<MetaAhorroDto>>> = flow {
        emit(Resource.Loading())
        try {
            val metas = dataSource.getMetaAhorrosPorUsuario(usuarioId)
            emit(Resource.Success(metas))
        } catch (e: Exception) {
            emit(Resource.Error("Error al obtener metas: ${e.message ?: "Error desconocido"}"))
        }
    }

    // Crear una nueva meta
    fun createMeta(metaDto: MetaAhorroDto): Flow<Resource<MetaAhorroDto>> = flow {
        emit(Resource.Loading())
        try {
            val result = dataSource.createMetaAhorro(metaDto)
            emit(Resource.Success(result))
        } catch (e: Exception) {
            emit(Resource.Error("Error al crear meta: ${e.message ?: "Error desconocido"}"))
        }
    }

    // Actualizar una meta existente
    fun updateMeta(id: Int, metaDto: MetaAhorroDto): Flow<Resource<MetaAhorroDto>> = flow {
        emit(Resource.Loading())
        try {
            val result = dataSource.updateMetaAhorro(id, metaDto)
            emit(Resource.Success(result))
        } catch (e: Exception) {
            emit(Resource.Error("Error al actualizar meta: ${e.message ?: "Error desconocido"}"))
        }
    }

    // Eliminar una meta
    fun deleteMeta(id: Int): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            dataSource.deleteMetaAhorro(id)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error("Error al eliminar meta: ${e.message ?: "Error desconocido"}"))
        }
    }
}