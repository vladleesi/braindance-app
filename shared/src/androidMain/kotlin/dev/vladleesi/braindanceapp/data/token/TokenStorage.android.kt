package dev.vladleesi.braindanceapp.data.token

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

actual class TokenStorage : KoinComponent {
    private val settings by lazy {
        val context = get<Context>()
        EncryptedSharedPreferences.create(
            context,
            TokenStorageConstants.SECURE_PREFS_KEY,
            MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
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
