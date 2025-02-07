package dev.vladleesi.braindanceapp.data.api.client

import dev.vladleesi.braindanceapp.data.api.engineConfig
import dev.vladleesi.braindanceapp.data.api.errorHandlerConfig
import dev.vladleesi.braindanceapp.data.api.jsonConfig
import dev.vladleesi.braindanceapp.data.api.loggerConfig
import io.ktor.client.HttpClient

val twitchHttpClient =
    HttpClient {
        expectSuccess = true
        engineConfig()
        loggerConfig()
        jsonConfig()
        errorHandlerConfig()
    }
