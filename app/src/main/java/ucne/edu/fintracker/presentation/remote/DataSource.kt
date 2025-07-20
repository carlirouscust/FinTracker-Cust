package ucne.edu.fintracker.presentation.remote

import retrofit2.Response
import ucne.edu.fintracker.presentation.remote.dto.ResetPasswordRequest
import ucne.edu.fintracker.presentation.remote.dto.UsuarioDto
import ucne.edu.fintracker.presentation.remote.dto.CategoriaDto
import ucne.edu.fintracker.presentation.remote.dto.PagoRecurrenteDto
import ucne.edu.fintracker.presentation.remote.dto.TransaccionDto
import javax.inject.Inject

class DataSource @Inject constructor(
    private val api: FinTrackerApi
) {
    // ------------------- USUARIOS -------------------
    suspend fun getUsuarios(): List<UsuarioDto> = api.getUsuario()

    suspend fun createUsuario(usuario: UsuarioDto): UsuarioDto =
        api.createUsuario(usuario)

    suspend fun getUsuario(id: Int): UsuarioDto = api.getUsuario(id)

    suspend fun updateUsuario(id: Int, usuario: UsuarioDto): UsuarioDto =
        api.updateUsuario(id, usuario)

    suspend fun deleteUsuario(id: Int) = api.deleteUsuario(id)

    suspend fun enviarResetPassword(email: String): Boolean {
        val response = api.enviarLinkResetPassword(ResetPasswordRequest(email))
        return response.isSuccessful
    }

    // ------------------- CATEGORÍAS -------------------
    suspend fun getCategoria(): List<CategoriaDto> = api.getCategoria()

    suspend fun createCategoria(categoriaDto: CategoriaDto): CategoriaDto =
        api.createCategoria(categoriaDto)

    suspend fun getCategoria(id: Int): CategoriaDto =
        api.getCategoria(id)

    suspend fun updateCategoria(id: Int, categoriaDto: CategoriaDto): CategoriaDto =
        api.updateCategoria(id, categoriaDto)

    suspend fun deleteCategoria(id: Int) =
        api.deleteCategoria(id)

    // ------------------- TRANSACCIONES -------------------
    suspend fun getTransacciones(): List<TransaccionDto> =
        api.getTransaccion()

    suspend fun createTransaccion(transaccionDto: TransaccionDto): TransaccionDto =
        api.createTransaccion(transaccionDto)

    suspend fun getTransaccion(id: Int): TransaccionDto =
        api.getTransaccion(id)

    suspend fun updateTransaccion(id: Int, transaccionDto: TransaccionDto): TransaccionDto =
        api.updateTransaccion(id, transaccionDto)

    suspend fun deleteTransaccion(id: Int) =
        api.deleteTransaccion(id)

    // ------------------- Pago Recurrente -------------------
    suspend fun getPagoRecurrente(): List<PagoRecurrenteDto> =
        api.getPagoRecurrente()

    suspend fun createPagoRecurrente(pagoRecurrenteDto: PagoRecurrenteDto): PagoRecurrenteDto =
        api.createPagoRecurrente(pagoRecurrenteDto)

    suspend fun getPagoRecurrente(id: Int): PagoRecurrenteDto =
        api.getPagoRecurrente(id)

    suspend fun updatePagoRecurrente(id: Int, pagoRecurrenteDto: PagoRecurrenteDto): Response<Void> {
        return api.updatePagoRecurrente(id, pagoRecurrenteDto)
    }

    suspend fun deletePagoRecurrente(id: Int) =
        api.deletePagoRecurrente(id)

}


