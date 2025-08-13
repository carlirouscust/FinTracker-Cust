package ucne.edu.fintracker.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import ucne.edu.fintracker.remote.FinTrackerApi
import ucne.edu.fintracker.remote.Resource
import ucne.edu.fintracker.remote.dto.UsuarioDto
import java.io.File
import javax.inject.Inject

class UsuarioRepository @Inject constructor(
    private val api: FinTrackerApi
) {
    companion object {
        private const val USUARIO_NO_ENCONTRADO = "Usuario no encontrado"
        private const val NO_AUTORIZADO = "No autorizado"
        private const val ERROR_DEL_SERVIDOR = "Error del servidor"
        private const val ERROR_CONEXION_PREFIX = "Error de conexión: "
        private const val MAX_FILE_SIZE_MB = 5
        private const val MAX_FILE_SIZE_BYTES = MAX_FILE_SIZE_MB * 1024 * 1024
        private val ALLOWED_EXTENSIONS = listOf("jpg", "jpeg", "png", "gif", "webp")
    }

    fun getUsuario(usuarioId: Int): Flow<Resource<UsuarioDto>> {
        return flow {
            try {
                emit(Resource.Loading())
                val usuario = api.getUsuario(usuarioId)
                emit(Resource.Success(usuario))
            } catch (e: HttpException) {
                emit(Resource.Error(getHttpErrorMessage(e.code(), "Error al obtener el usuario")))
            } catch (e: Exception) {
                emit(Resource.Error(ERROR_CONEXION_PREFIX + e.message))
            }
        }
    }

    fun actualizarUsuario(usuarioId: Int, usuarioDto: UsuarioDto): Flow<Resource<UsuarioDto>> {
        return flow {
            try {
                emit(Resource.Loading())
                val usuarioActualizado = api.updateUsuario(usuarioId, usuarioDto)
                emit(Resource.Success(usuarioActualizado))
            } catch (e: HttpException) {
                val errorMessage = when (e.code()) {
                    400 -> "Datos de usuario no válidos"
                    else -> getHttpErrorMessage(e.code(), "Error al actualizar el usuario")
                }
                emit(Resource.Error(errorMessage))
            } catch (e: Exception) {
                emit(Resource.Error(ERROR_CONEXION_PREFIX + e.message))
            }
        }
    }

    fun actualizarFotoPerfil(usuarioId: Int, fotoPath: String): Flow<Resource<UsuarioDto>> {
        return flow {
            try {
                emit(Resource.Loading())

                val validationError = validateImageFile(fotoPath)
                if (validationError != null) {
                    emit(Resource.Error(validationError))
                    return@flow
                }

                val usuarioActualizado = prepareUserWithNewPhoto(usuarioId, fotoPath)
                val resultado = updateUserPhoto(usuarioId, usuarioActualizado, fotoPath)
                emit(Resource.Success(resultado))

            } catch (e: HttpException) {
                val errorMessage = when (e.code()) {
                    400 -> "Datos no válidos"
                    else -> getHttpErrorMessage(e.code(), "Error al guardar la foto: ${e.message()}")
                }
                emit(Resource.Error(errorMessage))
            } catch (e: Exception) {
                emit(Resource.Error(ERROR_CONEXION_PREFIX + e.message))
            }
        }
    }

    fun eliminarUsuario(usuarioId: Int): Flow<Resource<Unit>> {
        return flow {
            try {
                emit(Resource.Loading())
                api.deleteUsuario(usuarioId)
                emit(Resource.Success(Unit))
            } catch (e: HttpException) {
                emit(Resource.Error(getHttpErrorMessage(e.code(), "Error al eliminar el usuario")))
            } catch (e: Exception) {
                emit(Resource.Error(ERROR_CONEXION_PREFIX + e.message))
            }
        }
    }

    private fun getHttpErrorMessage(code: Int, defaultMessage: String): String {
        return when (code) {
            404 -> USUARIO_NO_ENCONTRADO
            401 -> NO_AUTORIZADO
            500 -> ERROR_DEL_SERVIDOR
            else -> defaultMessage
        }
    }

    private fun validateImageFile(fotoPath: String): String? {
        val file = File(fotoPath)

        if (!file.exists()) {
            return "El archivo de imagen no existe"
        }

        val fileExtension = file.extension.lowercase()
        if (!ALLOWED_EXTENSIONS.contains(fileExtension)) {
            return "Formato de imagen no válido. Use: ${ALLOWED_EXTENSIONS.joinToString(", ")}"
        }

        if (file.length() > MAX_FILE_SIZE_BYTES) {
            return "La imagen es demasiado grande. Máximo ${MAX_FILE_SIZE_MB}MB permitido"
        }

        return null
    }

    private suspend fun prepareUserWithNewPhoto(usuarioId: Int, fotoPath: String): UsuarioDto {
        val usuarioActual = api.getUsuario(usuarioId)
        return usuarioActual.copy(fotoPerfil = fotoPath)
    }

    private suspend fun updateUserPhoto(usuarioId: Int, usuarioActualizado: UsuarioDto, fotoPath: String
    ): UsuarioDto {
        return try {
            api.updateUsuario(usuarioId, usuarioActualizado)
        } catch (e: Exception) {
            verifyPhotoUpdate(usuarioId, fotoPath) ?: throw e
        }
    }


    private suspend fun verifyPhotoUpdate(usuarioId: Int, expectedPath: String): UsuarioDto? {
        return try {
            val usuarioVerificado = api.getUsuario(usuarioId)
            if (usuarioVerificado.fotoPerfil == expectedPath) usuarioVerificado else null
        } catch (e: Exception) {
            null
        }
    }
}