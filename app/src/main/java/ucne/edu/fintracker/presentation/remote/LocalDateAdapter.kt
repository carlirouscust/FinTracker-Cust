package ucne.edu.fintracker.presentation.remote

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import org.threeten.bp.LocalDate

class LocalDateAdapter {
    @ToJson
    fun toJson(date: LocalDate): String = date.toString()

    @FromJson
    fun fromJson(json: String): LocalDate = LocalDate.parse(json)
}