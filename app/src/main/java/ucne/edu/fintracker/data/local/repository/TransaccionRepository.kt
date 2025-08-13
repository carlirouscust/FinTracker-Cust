package ucne.edu.fintracker.data.local.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import ucne.edu.fintracker.presentation.remote.DataSource
import ucne.edu.fintracker.presentation.remote.Resource
import ucne.edu.fintracker.presentation.remote.dto.TransaccionDto
import kotlinx.coroutines.flow.flow
import ucne.edu.fintracker.data.local.dao.TransaccionDao
import ucne.edu.fintracker.data.local.toDto
import ucne.edu.fintracker.data.local.toEntity
import ucne.edu.fintracker.presentation.remote.dto.TotalAnual
import ucne.edu.fintracker.presentation.remote.dto.TotalMes
import javax.inject.Inject



class TransaccionRepository @Inject constructor(
    private val dataSource: DataSource,
    private val transaccionDao: TransaccionDao
) {

    fun getTransacciones(usuarioId: Int): Flow<Resource<List<TransaccionDto>>> = flow {
        emit(Resource.Loading())

        val datosLocales = transaccionDao.getByUsuario(usuarioId)
            .firstOrNull()
            ?.map { it.toDto() }
            ?: emptyList()
        if (datosLocales.isNotEmpty()) {
            emit(Resource.Success(datosLocales))
        }

        try {
            val remotas = dataSource.getTransaccionesPorUsuario(usuarioId)
            transaccionDao.insertOrUpdateAll(remotas.map { it.toEntity() })

            emit(Resource.Success(remotas))
        } catch (e: Exception) {
            emit(Resource.Error("Error al obtener transacciones: ${e.message ?: "Error desconocido"}"))
        }
    }

    suspend fun syncTransacciones(usuarioId: Int) {
        try {
            val remotas = dataSource.getTransacciones()
            val filtradas = remotas.filter { it.usuarioId == usuarioId }
            transaccionDao.insertOrUpdateAll(filtradas.map { it.toEntity() })
        } catch (e: Exception) {

        }
    }

    fun createTransaccion(transaccionDto: TransaccionDto): Flow<Resource<TransaccionDto>> = flow {
        val entity = transaccionDto.toEntity().copy(syncPending = true)
        transaccionDao.insert(entity)
        emit(Resource.Loading())
        try {
            val created = dataSource.createTransaccion(transaccionDto)
            transaccionDao.save(created.toEntity().copy(syncPending = false))
            emit(Resource.Success(created))
        } catch (e: Exception) {
            emit(Resource.Error("Error al crear transacción: ${e.message ?: "Error desconocido"}"))
        }
    }

    fun updateTransaccion(id: Int, transaccionDto: TransaccionDto): Flow<Resource<TransaccionDto>> = flow {
        emit(Resource.Loading())
        try {
            val result = dataSource.updateTransaccion(id, transaccionDto)
            emit(Resource.Success(result))
        } catch (e: Exception) {
            emit(Resource.Error("Error al actualizar transacción: ${e.message ?: "Error desconocido"}"))
        }
    }

    fun deleteTransaccion(id: Int): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            dataSource.deleteTransaccion(id)
            transaccionDao.deleteById(id)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error("Error al eliminar transacción: ${e.message ?: "Error desconocido"}"))
        }
    }

    suspend fun obtenerTotalesPorMes(usuarioId: Int): List<TotalMes> {
        return try {
            dataSource.obtenerTotalesPorMes(usuarioId)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun obtenerTotalesPorAno(usuarioId: Int): List<TotalAnual> {
        return try {
            dataSource.obtenerTotalesPorAno(usuarioId)
        } catch (e: Exception) {
            emptyList()
        }
    }
}

