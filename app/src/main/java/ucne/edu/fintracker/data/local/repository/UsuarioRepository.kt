package ucne.edu.fintracker.data.local.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import ucne.edu.fintracker.presentation.remote.FinTrackerApi
import ucne.edu.fintracker.presentation.remote.Resource
import ucne.edu.fintracker.presentation.remote.dto.UsuarioDto
import ucne.edu.fintracker.presentation.remote.dto.CambiarContrasenaRequest
import java.io.File
import javax.inject.Inject

class UsuarioRepository @Inject constructor(
    private val api: FinTrackerApi
) {

    suspend fun getUsuario(usuarioId: Int): Flow<Resource<UsuarioDto>> {
        return flow {
            try {
                emit(Resource.Loading())
                val usuario = api.getUsuario(usuarioId)
                emit(Resource.Success(usuario))
            } catch (e: HttpException) {
                emit(Resource.Error(
                    when (e.code()) {
                        404 -> "Usuario no encontrado"
                        401 -> "No autorizado"
                        500 -> "Error del servidor"
                        else -> "Error al obtener el usuario"
                    }
                ))
            } catch (e: Exception) {
                emit(Resource.Error("Error de conexión: ${e.message}"))
            }
        }
    }

    suspend fun actualizarUsuario(usuarioId: Int, usuarioDto: UsuarioDto): Flow<Resource<UsuarioDto>> {
        return flow {
            try {
                emit(Resource.Loading())
                val usuarioActualizado = api.updateUsuario(usuarioId, usuarioDto)
                emit(Resource.Success(usuarioActualizado))
            } catch (e: HttpException) {
                emit(Resource.Error(
                    when (e.code()) {
                        400 -> "Datos de usuario no válidos"
                        404 -> "Usuario no encontrado"
                        401 -> "No autorizado"
                        500 -> "Error del servidor"
                        else -> "Error al actualizar el usuario"
                    }
                ))
            } catch (e: Exception) {
                emit(Resource.Error("Error de conexión: ${e.message}"))
            }
        }
    }

    suspend fun actualizarFotoPerfil(usuarioId: Int, fotoPath: String): Flow<Resource<UsuarioDto>> {
        return flow {
            try {
                emit(Resource.Loading())

                // Obtener el usuario actual primero
                val usuarioActual = api.getUsuario(usuarioId)

                // Crear el archivo desde el path
                val file = File(fotoPath)
                if (!file.exists()) {
                    emit(Resource.Error("El archivo de imagen no existe"))
                    return@flow
                }

                // Validar que el archivo sea una imagen
                val allowedExtensions = listOf("jpg", "jpeg", "png", "gif", "webp")
                val fileExtension = file.extension.lowercase()
                if (!allowedExtensions.contains(fileExtension)) {
                    emit(Resource.Error("Formato de imagen no válido. Use: ${allowedExtensions.joinToString(", ")}"))
                    return@flow
                }

                // Validar el tamaño del archivo (máximo 5MB)
                val maxSizeInBytes = 5 * 1024 * 1024 // 5MB
                if (file.length() > maxSizeInBytes) {
                    emit(Resource.Error("La imagen es demasiado grande. Máximo 5MB permitido"))
                    return@flow
                }

                // Actualizar el usuario con la nueva ruta de foto
                // IMPORTANTE: No incluir la contraseña en la actualización
                val usuarioActualizado = usuarioActual.copy(
                    fotoPerfil = fotoPath,
                    contraseña = "" // Enviar contraseña vacía o null para evitar problemas
                )

                try {
                    val resultado = api.updateUsuario(usuarioId, usuarioActualizado)
                    // Si el resultado es null, obtener el usuario actualizado
                    val usuarioFinal = resultado ?: api.getUsuario(usuarioId)
                    emit(Resource.Success(usuarioFinal))
                } catch (e: Exception) {
                    // Si hay error en updateUsuario, verificar si la actualización se hizo
                    try {
                        val usuarioVerificado = api.getUsuario(usuarioId)
                        if (usuarioVerificado.fotoPerfil == fotoPath) {
                            // La actualización fue exitosa aunque el endpoint retornó null
                            emit(Resource.Success(usuarioVerificado))
                        } else {
                            throw e
                        }
                    } catch (verifyException: Exception) {
                        throw e // Lanzar el error original
                    }
                }

            } catch (e: HttpException) {
                emit(Resource.Error(
                    when (e.code()) {
                        400 -> "Datos no válidos"
                        401 -> "No autorizado"
                        404 -> "Usuario no encontrado"
                        500 -> "Error del servidor"
                        else -> "Error al guardar la foto: ${e.message()}"
                    }
                ))
            } catch (e: Exception) {
                emit(Resource.Error("Error de conexión: ${e.message}"))
            }
        }
    }

    suspend fun eliminarUsuario(usuarioId: Int): Flow<Resource<Unit>> {
        return flow {
            try {
                emit(Resource.Loading())
                api.deleteUsuario(usuarioId)
                emit(Resource.Success(Unit))
            } catch (e: HttpException) {
                emit(Resource.Error(
                    when (e.code()) {
                        404 -> "Usuario no encontrado"
                        401 -> "No autorizado"
                        500 -> "Error del servidor"
                        else -> "Error al eliminar el usuario"
                    }
                ))
            } catch (e: Exception) {
                emit(Resource.Error("Error de conexión: ${e.message}"))
            }
        }
    }
}