package ucne.edu.fintracker.data.local.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ucne.edu.fintracker.presentation.remote.DataSource
import ucne.edu.fintracker.presentation.remote.Resource
import ucne.edu.fintracker.presentation.remote.dto.LimiteGastoDto
import javax.inject.Inject


class LimiteRepository @Inject constructor(
    private val dataSource: DataSource
) {

    // Obtener límites de gasto filtrados por usuarioId
    fun getLimites(usuarioId: Int): Flow<Resource<List<LimiteGastoDto>>> = flow {
        emit(Resource.Loading())
        try {
            val limites = dataSource.getLimiteGastosPorUsuario(usuarioId)
            emit(Resource.Success(limites))
        } catch (e: Exception) {
            emit(Resource.Error("Error al obtener límites: ${e.message ?: "Error desconocido"}"))
        }
    }

    // Crear un nuevo límite de gasto
    fun createLimite(limiteDto: LimiteGastoDto): Flow<Resource<LimiteGastoDto>> = flow {
        emit(Resource.Loading())
        try {
            val result = dataSource.createLimiteGasto(limiteDto)
            emit(Resource.Success(result))
        } catch (e: Exception) {
            emit(Resource.Error("Error al crear límite: ${e.message ?: "Error desconocido"}"))
        }
    }

    // Actualizar un límite de gasto existente
    fun updateLimite(id: Int, limiteDto: LimiteGastoDto): Flow<Resource<LimiteGastoDto>> = flow {
        emit(Resource.Loading())
        try {
            val result = dataSource.updateLimiteGasto(id, limiteDto)
            emit(Resource.Success(result))
        } catch (e: Exception) {
            emit(Resource.Error("Error al actualizar límite: ${e.message ?: "Error desconocido"}"))
        }
    }

    // Eliminar un límite de gasto
    fun deleteLimite(id: Int): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            dataSource.deleteLimiteGasto(id)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error("Error al eliminar límite: ${e.message ?: "Error desconocido"}"))
        }
    }
}