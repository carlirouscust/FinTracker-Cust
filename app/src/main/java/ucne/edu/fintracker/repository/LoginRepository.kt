package ucne.edu.fintracker.repository

import android.content.Context
import ucne.edu.fintracker.remote.DataSource
import ucne.edu.fintracker.remote.dto.UsuarioDto
import ucne.edu.fintracker.presentation.login.DataLogin
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val dataSource: DataSource
) {
    suspend fun login(email: String, contraseña: String, context: Context): UsuarioDto? {
        val usuarios = dataSource.getUsuarios()
        val usuario = usuarios.find { it.email == email && it.contraseña == contraseña }

        if (usuario != null) {
            DataLogin.guardarUsuarioId(context, usuario.usuarioId ?: 0)
        }

        return usuario
    }

    suspend fun register(usuario: UsuarioDto): UsuarioDto {
        return dataSource.createUsuario(usuario)
    }

}
