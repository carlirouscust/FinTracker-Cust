package ucne.edu.fintracker.data.local.repository

import kotlinx.coroutines.flow.Flow
import ucne.edu.fintracker.presentation.remote.DataSource
import ucne.edu.fintracker.presentation.remote.Resource
import ucne.edu.fintracker.presentation.remote.dto.TransaccionDto
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class TransaccionRepository @Inject constructor(
    private val dataSource: DataSource
) {

    fun getTransacciones(): Flow<Resource<List<TransaccionDto>>> = flow {
        try {
            emit(Resource.Loading())
            val transacciones = dataSource.getTransacciones()
            emit(Resource.Success(transacciones))
        } catch (e: Exception) {
            emit(Resource.Error("Error al obtener transacciones: ${e.message}"))
        }
    }

    fun createTransaccion(transaccionDto: TransaccionDto): Flow<Resource<TransaccionDto>> = flow {
        try {
            emit(Resource.Loading())
            val result = dataSource.createTransaccion(transaccionDto)
            emit(Resource.Success(result))
        } catch (e: Exception) {
            emit(Resource.Error("Error al crear transacción: ${e.message}"))
        }
    }

    fun updateTransaccion(id: Int, transaccionDto: TransaccionDto): Flow<Resource<TransaccionDto>> = flow {
        try {
            emit(Resource.Loading())
            val result = dataSource.updateTransaccion(id, transaccionDto)
            emit(Resource.Success(result))
        } catch (e: Exception) {
            emit(Resource.Error("Error al actualizar transacción: ${e.message}"))
        }
    }

    fun deleteTransaccion(id: Int): Flow<Resource<Unit>> = flow {
        try {
            emit(Resource.Loading())
            dataSource.deleteTransaccion(id)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error("Error al eliminar transacción: ${e.message}"))
        }
    }
}

