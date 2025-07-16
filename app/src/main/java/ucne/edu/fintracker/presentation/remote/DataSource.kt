package ucne.edu.fintracker.presentation.remote

import ucne.edu.fintracker.presentation.remote.dto.ResetPasswordRequest
import ucne.edu.fintracker.presentation.remote.dto.UsuarioDto
import ucne.edu.fintracker.presentation.remote.dto.CategoriaDto
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
}
