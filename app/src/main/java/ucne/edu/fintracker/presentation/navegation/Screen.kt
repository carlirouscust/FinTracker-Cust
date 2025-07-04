package ucne.edu.fintracker.presentation.navegation

import kotlinx.serialization.Serializable

sealed class Screen {
    @Serializable
    data object LoginList : Screen()
    @Serializable
    data class Login (val name: String) : Screen()

}