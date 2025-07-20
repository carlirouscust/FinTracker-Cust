package ucne.edu.fintracker.data.local.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ucne.edu.fintracker.presentation.remote.DataSource
import ucne.edu.fintracker.presentation.remote.Resource
import ucne.edu.fintracker.presentation.remote.dto.PagoRecurrenteDto
import javax.inject.Inject

class PagoRepository@Inject constructor(
    private val dataSource: DataSource
) {

        fun getPagosRecurrentes(): Flow<Resource<List<PagoRecurrenteDto>>> = flow {
            try {
                emit(Resource.Loading())
                val pagos = dataSource.getPagoRecurrente()
                emit(Resource.Success(pagos))
            } catch (e: Exception) {
                emit(Resource.Error("Error al obtener pagos recurrentes: ${e.message}"))
            }
        }

        fun createPagoRecurrente(pagoRecurrenteDto: PagoRecurrenteDto): Flow<Resource<PagoRecurrenteDto>> = flow {
            try {
                emit(Resource.Loading())
                val result = dataSource.createPagoRecurrente(pagoRecurrenteDto)
                emit(Resource.Success(result))
            } catch (e: Exception) {
                emit(Resource.Error("Error al crear pago recurrente: ${e.message}"))
            }
        }

    fun updatePagoRecurrente(id: Int, pagoRecurrenteDto: PagoRecurrenteDto): Flow<Resource<PagoRecurrenteDto>> = flow {
        try {
            emit(Resource.Loading())
            val response = dataSource.updatePagoRecurrente(id, pagoRecurrenteDto)
            if (response.isSuccessful) {
                emit(Resource.Success(pagoRecurrenteDto))
            } else {
                emit(Resource.Error("Error en la respuesta: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error al actualizar pago recurrente: ${e.message}"))
        }
    }




    fun deletePagoRecurrente(id: Int): Flow<Resource<Unit>> = flow {
            try {
                emit(Resource.Loading())
                dataSource.deletePagoRecurrente(id)
                emit(Resource.Success(Unit))
            } catch (e: Exception) {
                emit(Resource.Error("Error al eliminar pago recurrente: ${e.message}"))
            }
        }

}