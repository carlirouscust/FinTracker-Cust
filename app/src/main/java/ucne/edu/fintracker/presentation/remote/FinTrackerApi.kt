package ucne.edu.fintracker.presentation.remote

import retrofit2.Response
import retrofit2.http.*
import ucne.edu.fintracker.presentation.remote.dto.ResetPasswordRequest
import ucne.edu.fintracker.presentation.remote.dto.UsuarioDto

interface FinTrackerApi {

        @GET("api/Usuarios")
        suspend fun getUsuario(): List<UsuarioDto>

        @POST("api/Usuarios")
        suspend fun createUsuario(@Body usuarioDto: UsuarioDto): UsuarioDto

        @GET("api/Usuarios/{id}")
        suspend fun getUsuario(@Path("id") id: Int): UsuarioDto

        @PUT("api/Usuarios/{id}")
        suspend fun updateUsuario(@Path("id") id: Int, @Body usuarioDto: UsuarioDto): UsuarioDto

        @DELETE("api/Usuarios/{id}")
        suspend fun deleteUsuario(@Path("id") id: Int)

        @POST("usuarios/resetpassword")
        suspend fun enviarLinkResetPassword(@Body request: ResetPasswordRequest): Response<Unit>

}


