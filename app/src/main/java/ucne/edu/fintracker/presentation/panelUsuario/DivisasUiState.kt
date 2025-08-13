package ucne.edu.fintracker.presentation.panelUsuario

data class DivisasUiState(
    val divisas: List<Divisa> = listOf(
        Divisa("Peso dominicano", "DOP"),
        Divisa("Dólar estadounidense", "USD"),
        Divisa("Euro", "EUR"),
        Divisa("Libra esterlina", "GBP"),
        Divisa("Yen japonés", "JPY"),
        Divisa("Dólar canadiense", "CAD"),
        Divisa("Dólar australiano", "AUD"),
        Divisa("Franco suizo", "CHF"),
        Divisa("Yuan chino", "CNY"),
        Divisa("Real brasileño", "BRL")
    ),
    val divisaSeleccionada: String = "DOP",
    val isLoading: Boolean = false,
    val error: String? = null
)

data class Divisa(
    val nombre: String,
    val codigo: String
)