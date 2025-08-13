package ucne.edu.fintracker.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ucne.edu.fintracker.remote.DataSource
import ucne.edu.fintracker.remote.Resource
import ucne.edu.fintracker.remote.dto.MetaAhorroDto
import javax.inject.Inject


class MetaRepository @Inject constructor(
    private val dataSource: DataSource
) {
    companion object {
        private const val ERROR_DESCONOCIDO = "Error desconocido"
    }

    fun getMetas(usuarioId: Int, metaId: Int): Flow<Resource<MetaAhorroDto?>> = flow {
        emit(Resource.Loading())
        try {
            val metas = dataSource.getMetaAhorrosPorUsuario(usuarioId)
            val meta = metas.find { it.metaAhorroId == metaId }
            emit(Resource.Success(meta))
        } catch (e: Exception) {
            emit(Resource.Error("Error al obtener la meta: ${e.message ?: ERROR_DESCONOCIDO}"))
        }
    }

    fun createMeta(metaDto: MetaAhorroDto): Flow<Resource<MetaAhorroDto>> = flow {
        emit(Resource.Loading())
        try {
            val result = dataSource.createMetaAhorro(metaDto)
            emit(Resource.Success(result))
        } catch (e: Exception) {
            emit(Resource.Error("Error al crear meta: ${e.message ?: ERROR_DESCONOCIDO}"))
        }
    }

    fun updateMeta(id: Int, metaDto: MetaAhorroDto): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            Log.d("RepoMeta", "Intentando actualizar meta ID=$id con datos: $metaDto")
            val result = dataSource.updateMetaAhorro(id, metaDto)
            Log.d("RepoMeta", "Respuesta de updateMetaAhorro: $result")
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            Log.e("RepoMeta", "Error en updateMeta: ${e.message}", e)
            emit(Resource.Error("Error al actualizar meta: ${e.message ?: ERROR_DESCONOCIDO}"))
        }
    }

    fun deleteMeta(id: Int): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            dataSource.deleteMetaAhorro(id)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error("Error al eliminar meta: ${e.message ?: ERROR_DESCONOCIDO}"))
        }
    }
}