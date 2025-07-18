package ucne.edu.fintracker.presentation.remote
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter

class LocalDateTimeAdapter {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

    @ToJson
    fun toJson(dateTime: OffsetDateTime): String {
        // Lo forzamos a UTC y lo formateamos
        return dateTime.withOffsetSameInstant(ZoneOffset.UTC).format(formatter)
    }

    @FromJson
    fun fromJson(json: String): OffsetDateTime {
        return OffsetDateTime.parse(json, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }
}
