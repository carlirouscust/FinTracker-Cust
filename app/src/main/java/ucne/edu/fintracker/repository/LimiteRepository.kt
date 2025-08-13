package ucne.edu.fintracker.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ucne.edu.fintracker.data.local.dao.LimiteGastoDao
import ucne.edu.fintracker.data.mappers.toEntity
import ucne.edu.fintracker.remote.DataSource
import ucne.edu.fintracker.remote.Resource
import ucne.edu.fintracker.remote.dto.LimiteGastoDto
import javax.inject.Inject


class LimiteRepository @Inject constructor(
    private val dataSource: DataSource,
    private val limiteGastoDao: LimiteGastoDao
) {

    companion object {
        private const val ERROR_DESCONOCIDO = "Error desconocido"
    }

    fun getLimites(usuarioId: Int): Flow<Resource<List<LimiteGastoDto>>> = flow {
        emit(Resource.Loading())
        try {
            val limites = dataSource.getLimiteGastosPorUsuario(usuarioId)
            emit(Resource.Success(limites))
        } catch (e: Exception) {
            emit(Resource.Error("Error al obtener límites: ${e.message ?: ERROR_DESCONOCIDO}"))
        }
    }

    fun createLimite(limiteDto: LimiteGastoDto): Flow<Resource<LimiteGastoDto>> = flow {
        val entity = limiteDto.toEntity(syncPending = true)
        limiteGastoDao.insert(entity)
        emit(Resource.Loading())
        try {
            val created = dataSource.createLimiteGasto(limiteDto)
            limiteGastoDao.insert(created.toEntity(syncPending = false))
            emit(Resource.Success(created))
        } catch (e: Exception) {
            emit(Resource.Error("Error al crear límite: ${e.message ?: ERROR_DESCONOCIDO}"))
        }
    }

    fun updateLimite(id: Int, limiteDto: LimiteGastoDto): Flow<Resource<LimiteGastoDto>> = flow {
        emit(Resource.Loading())
        try {
            val result = dataSource.updateLimiteGasto(id, limiteDto)
            emit(Resource.Success(result))
        } catch (e: Exception) {
            emit(Resource.Error("Error al actualizar límite: ${e.message ?: ERROR_DESCONOCIDO}"))
        }
    }

    fun deleteLimite(id: Int): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            dataSource.deleteLimiteGasto(id)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error("Error al eliminar límite: ${e.message ?: ERROR_DESCONOCIDO}"))
        }
    }
}