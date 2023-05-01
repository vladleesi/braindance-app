package io.github.vladleesi.braindanceapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform