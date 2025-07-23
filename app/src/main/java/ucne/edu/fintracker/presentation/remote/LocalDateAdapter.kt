package ucne.edu.fintracker.presentation.remote

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.LocalDateTime

class LocalDateTimeAdapter {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

    @ToJson
    fun toJson(dateTime: OffsetDateTime): String {
        return dateTime.withOffsetSameInstant(ZoneOffset.UTC).format(formatter)
    }

    @FromJson
    fun fromJson(json: String): OffsetDateTime {
        return try {
            // Intenta parsear con offset
            OffsetDateTime.parse(json, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        } catch (e: Exception) {
            // Si no tiene offset, lo asumimos UTC
            val localFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            val localDateTime = LocalDateTime.parse(json, localFormatter)
            localDateTime.atOffset(ZoneOffset.UTC)
        }
    }
}

