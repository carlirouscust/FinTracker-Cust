package ucne.edu.fintracker.data.local.repository

import ucne.edu.fintracker.presentation.remote.DataSource
import ucne.edu.fintracker.presentation.remote.dto.UsuarioDto
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val dataSource: DataSource
) {
    suspend fun login(email: String, password: String): UsuarioDto? {
        val usuarios = dataSource.getUsuarios()
        return usuarios.find { it.email == email && it.contraseña == password }
    }

    suspend fun register(usuario: UsuarioDto): UsuarioDto {
        return dataSource.createUsuario(usuario)
    }

    suspend fun enviarResetPassword(email: String): Boolean {
        return dataSource.enviarResetPassword(email)
    }
}
