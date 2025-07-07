package ucne.edu.fintracker.presentation.remote

import ucne.edu.fintracker.presentation.remote.dto.ResetPasswordRequest
import ucne.edu.fintracker.presentation.remote.dto.UsuarioDto
import javax.inject.Inject

class DataSource @Inject constructor(
    private val api: FinTrackerApi
){
    suspend fun getUsuarios(): List<UsuarioDto> = api.getUsuario()

    suspend fun createUsuario(usuario: UsuarioDto): UsuarioDto =
        api.createUsuario(usuario)

    suspend fun getUsuario(id: Int): UsuarioDto =api.getUsuario(id)

    suspend fun updateUsuario(id: Int, usuario: UsuarioDto): UsuarioDto =
        api.updateUsuario(id, usuario)

    suspend fun deleteUsuario(id: Int) = api.deleteUsuario(id)

    suspend fun enviarResetPassword(email: String): Boolean {
        val response = api.enviarLinkResetPassword(ResetPasswordRequest(email))
        return response.isSuccessful
    }

}