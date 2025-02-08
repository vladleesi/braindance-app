package dev.vladleesi.braindanceapp.data.api.clients

import dev.vladleesi.braindanceapp.data.api.defaultConfig
import io.ktor.client.HttpClient

object TwitchClient {
    fun build() =
        HttpClient {
            defaultConfig()
        }
}
