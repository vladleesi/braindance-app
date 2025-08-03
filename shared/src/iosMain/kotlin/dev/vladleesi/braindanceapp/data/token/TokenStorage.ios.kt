package dev.vladleesi.braindanceapp.data.token

import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.KeychainSettings

@OptIn(ExperimentalSettingsImplementation::class)
actual class TokenStorage {
    private val settings by lazy {
        KeychainSettings(service = TokenStorageConstants.SECURE_PREFS_KEY)
    }

    actual fun saveToken(token: String) {
        settings.putString(TokenStorageConstants.AUTH_TOKEN_KEY, token)
    }

    actual fun getToken(): String? = settings.getStringOrNull(TokenStorageConstants.AUTH_TOKEN_KEY)

    actual fun clearToken() {
        settings.remove(TokenStorageConstants.AUTH_TOKEN_KEY)
    }
}
