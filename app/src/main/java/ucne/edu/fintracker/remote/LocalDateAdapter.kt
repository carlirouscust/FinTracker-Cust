package ucne.edu.fintracker.remote

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.LocalDateTime

class LocalDateTimeAdapter {

    private val formatters = listOf(
        DateTimeFormatter.ISO_OFFSET_DATE_TIME,
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    )

    @ToJson
    fun toJson(dateTime: OffsetDateTime): String {
        return dateTime.withOffsetSameInstant(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }

    @FromJson
    fun fromJson(json: String): OffsetDateTime {
        for (formatter in formatters) {
            try {
                return when (formatter) {
                    DateTimeFormatter.ISO_OFFSET_DATE_TIME -> OffsetDateTime.parse(json, formatter)
                    else -> {
                        val localDateTime = LocalDateTime.parse(json, formatter)
                        localDateTime.atOffset(ZoneOffset.UTC)
                    }
                }
            } catch (_: Exception) {
            }
        }
        throw IllegalArgumentException("Formato de fecha no reconocido: $json")
    }
}


