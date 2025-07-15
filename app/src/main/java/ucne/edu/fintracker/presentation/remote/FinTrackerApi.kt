package ucne.edu.fintracker.presentation.remote

import retrofit2.Response
import retrofit2.http.*
import ucne.edu.fintracker.presentation.remote.dto.CategoriaDto
import ucne.edu.fintracker.presentation.remote.dto.LimiteGastoDto
import ucne.edu.fintracker.presentation.remote.dto.MetaAhorroDto
import ucne.edu.fintracker.presentation.remote.dto.PagoRecurrenteDto
import ucne.edu.fintracker.presentation.remote.dto.ResetPasswordRequest
import ucne.edu.fintracker.presentation.remote.dto.TransaccionDto
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

        @GET("api/Transacciones")
        suspend fun getTransaccion(): List<TransaccionDto>

        @POST("api/Transacciones")
        suspend fun createTransaccion(@Body usuarioDto: TransaccionDto): TransaccionDto

        @GET("api/Transacciones/{id}")
        suspend fun getTransaccion(@Path("id") id: Int): TransaccionDto

        @PUT("api/Transacciones/{id}")
        suspend fun updateTransaccion(@Path("id") id: Int, @Body transaccionDto: TransaccionDto): TransaccionDto

        @DELETE("api/Transacciones/{id}")
        suspend fun deleteTransaccion(@Path("id") id: Int)

        @GET("api/PagoRecurrentes")
        suspend fun getPagoRecurrenteo(): List<PagoRecurrenteDto>

        @POST("api/PagoRecurrentes")
        suspend fun createPagoRecurrente(@Body pagoRecurrenteDto: PagoRecurrenteDto): PagoRecurrenteDto

        @GET("api/PagoRecurrentes/{id}")
        suspend fun getPagoRecurrente(@Path("id") id: Int): PagoRecurrenteDto

        @PUT("api/PagoRecurrentes/{id}")
        suspend fun updatePagoRecurrente(@Path("id") id: Int, @Body pagoRecurrenteDto: PagoRecurrenteDto): PagoRecurrenteDto

        @DELETE("api/PagoRecurrentes/{id}")
        suspend fun deletePagoRecurrente(@Path("id") id: Int)

        @GET("api/MetaAhorros")
        suspend fun getMetaAhorro(): List<MetaAhorroDto>

        @POST("api/MetaAhorros")
        suspend fun createMetaAhorro(@Body metaAhorroDto: MetaAhorroDto): MetaAhorroDto

        @GET("api/MetaAhorros/{id}")
        suspend fun getMetaAhorro(@Path("id") id: Int): MetaAhorroDto

        @PUT("api/MetaAhorros/{id}")
        suspend fun updateMetaAhorro(@Path("id") id: Int, @Body metaAhorroDto: MetaAhorroDto): MetaAhorroDto

        @DELETE("api/MetaAhorros/{id}")
        suspend fun deleteMetaAhorro(@Path("id") id: Int)


        @GET("api/LimiteGastos")
        suspend fun getLimiteGasto(): List<LimiteGastoDto>

        @POST("api/LimiteGastos")
        suspend fun createLimiteGasto(@Body limiteGastoDto: LimiteGastoDto): LimiteGastoDto

        @GET("api/LimiteGastos/{id}")
        suspend fun getLimiteGasto(@Path("id") id: Int): LimiteGastoDto

        @PUT("api/LimiteGastos/{id}")
        suspend fun updateLimiteGasto(@Path("id") id: Int, @Body limiteGastoDto: LimiteGastoDto): LimiteGastoDto

        @DELETE("api/LimiteGastos/{id}")
        suspend fun deleteLimiteGasto(@Path("id") id: Int)

        @GET("api/Categorias")
        suspend fun getCategoria(): List<CategoriaDto>

        @POST("api/Categorias")
        suspend fun createCategoria(@Body categoriaDto: CategoriaDto): CategoriaDto

        @GET("api/Categorias/{id}")
        suspend fun getCategoria(@Path("id") id: Int): CategoriaDto

        @PUT("api/Categorias/{id}")
        suspend fun updateCategoria(@Path("id") id: Int, @Body categoriaDto: CategoriaDto): CategoriaDto

        @DELETE("api/Categorias/{id}")
        suspend fun deleteCategoria(@Path("id") id: Int)
}


