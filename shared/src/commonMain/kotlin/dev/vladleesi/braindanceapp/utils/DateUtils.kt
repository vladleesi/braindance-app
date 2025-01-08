package dev.vladleesi.braindanceapp.utils

import io.ktor.util.date.GMTDate

val currentYear: Int
    get() = GMTDate().year

val lastYear: Int
    get() = GMTDate().year - 1
