package dev.vladleesi.braindanceapp.utils

import io.ktor.util.date.GMTDate
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime

private const val DAYS_OF_MONTH = 30

val currentYear: Int
    get() = GMTDate().year

val lastYear: Int
    get() = GMTDate().year.dec()

val now: Long
    get() = GMTDate().timestamp

val nowUnix: Long
    get() = now / 1000

// TODO: Add reusable formats
fun Long.formatDate(): String? {
    val format =
        DateTimeComponents.Format {
            monthName(MonthNames.ENGLISH_ABBREVIATED)
            char(' ')
            dayOfMonth()
            char(',')
            char(' ')
            year()
        }
    return runCatching { Instant.fromEpochMilliseconds(this).format(format) }.getOrNull()
}

fun String.formatEndDate(): String? {
    val formattedEndDate = this.replace(" ", "T")
    val endDateTime = LocalDateTime.parseOrNull(formattedEndDate) ?: return null
    val daysUntilEnd =
        Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date.daysUntil(endDateTime.date)

    return when {
        daysUntilEnd > DAYS_OF_MONTH -> null
        daysUntilEnd == DAYS_OF_MONTH -> "1 month"
        daysUntilEnd > 0 -> "$daysUntilEnd days"
        else -> "Ends soon"
    }
}

private fun LocalDateTime.Companion.parseOrNull(value: String): LocalDateTime? =
    runCatching { parse(value) }.getOrNull()
