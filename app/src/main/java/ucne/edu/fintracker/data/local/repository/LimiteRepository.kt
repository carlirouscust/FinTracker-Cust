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

    // Obtener todos los límites de gasto
    fun getLimites(): Flow<Resource<List<LimiteGastoDto>>> = flow {
        try {
            emit(Resource.Loading())
            val limites = dataSource.getLimiteGasto()
            emit(Resource.Success(limites))
        } catch (e: Exception) {
            emit(Resource.Error("Error al obtener límites: ${e.message}"))
        }
    }

    // Crear un nuevo límite de gasto
    fun createLimite(limiteDto: LimiteGastoDto): Flow<Resource<LimiteGastoDto>> = flow {
        try {
            emit(Resource.Loading())
            val result = dataSource.createLimiteGasto(limiteDto)
            emit(Resource.Success(result))
        } catch (e: Exception) {
            emit(Resource.Error("Error al crear límite: ${e.message}"))
        }
    }

    // Actualizar un límite de gasto existente
    fun updateLimite(id: Int, limiteDto: LimiteGastoDto): Flow<Resource<LimiteGastoDto>> = flow {
        try {
            emit(Resource.Loading())
            val result = dataSource.updateLimiteGasto(id, limiteDto)
            emit(Resource.Success(result))
        } catch (e: Exception) {
            emit(Resource.Error("Error al actualizar límite: ${e.message}"))
        }
    }

    // Eliminar un límite de gasto
    fun deleteLimite(id: Int): Flow<Resource<Unit>> = flow {
        try {
            emit(Resource.Loading())
            dataSource.deleteLimiteGasto(id)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error("Error al eliminar límite: ${e.message}"))
        }
    }
}
