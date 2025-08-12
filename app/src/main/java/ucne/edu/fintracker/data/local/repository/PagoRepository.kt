package ucne.edu.fintracker.data.local.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import ucne.edu.fintracker.data.local.dao.PagoRecurrenteDao
import ucne.edu.fintracker.data.local.toDto
import ucne.edu.fintracker.data.local.toEntity
import ucne.edu.fintracker.presentation.remote.DataSource
import ucne.edu.fintracker.presentation.remote.Resource
import ucne.edu.fintracker.presentation.remote.dto.PagoRecurrenteDto
import javax.inject.Inject


class PagoRepository @Inject constructor(
    private val dataSource: DataSource,
    private val pagoRecurrenteDao: PagoRecurrenteDao
) {

    // Obtener pagos recurrentes filtrados por usuarioId
    fun getPagosRecurrentes(usuarioId: Int): Flow<Resource<List<PagoRecurrenteDto>>> = flow {
        emit(Resource.Loading())

        val datosLocalesEntities = pagoRecurrenteDao.getByUsuario(usuarioId).firstOrNull() ?: emptyList()
        val datosLocalesDto = datosLocalesEntities.map { it.toDto() }

        if (datosLocalesDto.isNotEmpty()) {
            emit(Resource.Success(datosLocalesDto))
        }

        try {
            val remotosDto = dataSource.getPagoRecurrentesPorUsuario(usuarioId)
            val remotosEntities = remotosDto.map { it.toEntity() }

            pagoRecurrenteDao.insertOrUpdateAll(remotosEntities)

            emit(Resource.Success(remotosDto))
        } catch (e: Exception) {
            emit(Resource.Error("Error al obtener pagos recurrentes: ${e.message ?: "Error desconocido"}"))
        }
    }

    suspend fun syncPagosRecurrentes(usuarioId: Int) {
        try {
            val remotos = dataSource.getPagoRecurrentes()
            val filtrados = remotos.filter { it.usuarioId == usuarioId }
            pagoRecurrenteDao.insertOrUpdateAll(filtrados.map { it.toEntity() })
        } catch (e: Exception) {
            // Manejar error si quieres
        }
    }

    // Crear un nuevo pago recurrente
    fun createPagoRecurrente(pagoRecurrenteDto: PagoRecurrenteDto): Flow<Resource<PagoRecurrenteDto>> = flow {
        val entity = pagoRecurrenteDto.toEntity(syncPending = true)
        pagoRecurrenteDao.insert(entity)
        emit(Resource.Loading())
        try {
            val created = dataSource.createPagoRecurrente(pagoRecurrenteDto)
            pagoRecurrenteDao.save(created.toEntity(syncPending = false))
            emit(Resource.Success(created))
        } catch (e: Exception) {
            emit(Resource.Error("Error al crear pago recurrente: ${e.message ?: "Error desconocido"}"))
        }
    }

    // Actualizar un pago recurrente existente
    fun updatePagoRecurrente(id: Int, pagoRecurrenteDto: PagoRecurrenteDto): Flow<Resource<PagoRecurrenteDto>> = flow {
        emit(Resource.Loading())
        try {
            val response = dataSource.updatePagoRecurrente(id, pagoRecurrenteDto)
            if(response.isSuccessful) {
                emit(Resource.Success(pagoRecurrenteDto))
            } else {
                emit(Resource.Error("Error en la respuesta: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error al actualizar pago recurrente: ${e.message ?: "Error desconocido"}"))
        }
    }

    // Eliminar un pago recurrente
    fun deletePagoRecurrente(id: Int): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            dataSource.deletePagoRecurrente(id)
            pagoRecurrenteDao.deleteById(id)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error("Error al eliminar pago recurrente: ${e.message ?: "Error desconocido"}"))
        }
    }
}
