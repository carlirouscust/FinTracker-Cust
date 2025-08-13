package ucne.edu.fintracker.remote.dto

import com.google.gson.annotations.SerializedName

data class CambiarContrasenaRequest(
    @SerializedName("contraseñaActual")
    val contraseñaActual: String,

    @SerializedName("contraseñaNueva")
    val contraseñaNueva: String
)
