package dev.vladleesi.braindanceapp.utils

import io.ktor.util.date.GMTDate
import kotlinx.datetime.Instant
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char

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
