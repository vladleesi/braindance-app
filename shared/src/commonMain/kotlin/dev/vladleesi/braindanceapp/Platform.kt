package dev.vladleesi.braindanceapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
