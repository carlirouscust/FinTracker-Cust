package ucne.edu.fintracker.presentation.remote

import retrofit2.http.*
import ucne.edu.fintracker.presentation.remote.dto.UsuarioDto

interface FinTrackerApi {

        @GET("api/Usuarios")
        suspend fun getUsuario(): List<UsuarioDto>

        @GET("api/Usuarios/{id}")
        suspend fun getUsuario(@Path("id") id: Int): UsuarioDto

        @POST("api/Usuarios")
        suspend fun createUsuario(@Body usuarioDto: UsuarioDto): UsuarioDto

        @PUT("api/Usuarios/{id}")
        suspend fun updateUsuario(@Path("id") id: Int, @Body usuarioDto: UsuarioDto): UsuarioDto

        @DELETE("api/Usuarios/{id}")
        suspend fun deleteUsuario(@Path("id") id: Int)
}
