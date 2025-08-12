package ucne.edu.fintracker.data.local.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import ucne.edu.fintracker.presentation.remote.FinTrackerApi
import ucne.edu.fintracker.presentation.remote.Resource
import ucne.edu.fintracker.presentation.remote.dto.UsuarioDto
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
                val usuarioActual = api.getUsuario(usuarioId)
                val file = File(fotoPath)
                if (!file.exists()) {
                    emit(Resource.Error("El archivo de imagen no existe"))
                    return@flow
                }
                val allowedExtensions = listOf("jpg", "jpeg", "png", "gif", "webp")
                val fileExtension = file.extension.lowercase()
                if (!allowedExtensions.contains(fileExtension)) {
                    emit(Resource.Error("Formato de imagen no válido. Use: ${allowedExtensions.joinToString(", ")}"))
                    return@flow
                }
                val maxSizeInBytes = 5 * 1024 * 1024
                if (file.length() > maxSizeInBytes) {
                    emit(Resource.Error("La imagen es demasiado grande. Máximo 5MB permitido"))
                    return@flow
                }
                val usuarioActualizado = usuarioActual.copy(fotoPerfil = fotoPath)
                val resultado = api.updateUsuario(usuarioId, usuarioActualizado)
                emit(Resource.Success(resultado))

            } catch (e: HttpException) {
                emit(Resource.Error(
                    when (e.code()) {
                        400 -> "Datos no válidos"
                        401 -> "No autorizado"
                        404 -> "Usuario no encontrado"
                        500 -> "Error del servidor"
                        else -> "Error al guardar la foto"
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