package dev.vladleesi.braindanceapp.data.token

import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dev.vladleesi.braindanceapp.system.ContextProvider

actual class TokenStorage {
    private val settings by lazy {
        ContextProvider.androidContext().let { context ->
            EncryptedSharedPreferences.create(
                context,
                TokenStorageConstants.SECURE_PREFS_KEY,
                MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
            )
        }
    }

    actual fun saveToken(token: String) {
        settings.edit().putString(TokenStorageConstants.AUTH_TOKEN_KEY, token).apply()
    }

    actual fun getToken(): String? {
        return settings.getString(TokenStorageConstants.AUTH_TOKEN_KEY, null)
    }

    actual fun clearToken() {
        settings.edit().remove(TokenStorageConstants.AUTH_TOKEN_KEY).apply()
    }
}
