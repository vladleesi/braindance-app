package dev.vladleesi.braindanceapp.utils

import io.ktor.util.date.GMTDate
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char

val currentYear: Int
    get() = GMTDate().year

val lastYear: Int
    get() = GMTDate().year.dec()

// TODO: Add reusable formats
fun String.formatDate(): String? {
    val format =
        LocalDate.Format {
            monthName(MonthNames.ENGLISH_ABBREVIATED)
            char(' ')
            dayOfMonth()
            char(',')
            char(' ')
            year()
        }
    return runCatching { LocalDate.parse(this).format(format) }.getOrNull()
}
