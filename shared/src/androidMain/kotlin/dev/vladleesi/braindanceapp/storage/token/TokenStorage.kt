package dev.vladleesi.braindanceapp.storage.token

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

actual class TokenStorage {

    private val prefsName = "token_prefs"
    private val tokenKey = "access_token"

    @SuppressWarnings("ForbiddenComment")
    actual fun getToken(): String? {
        // TODO: Get rid of deprecation
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        val preferences: SharedPreferences = EncryptedSharedPreferences.create(
            prefsName,
            masterKeyAlias,
            AndroidContextProvider.getContext(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        return preferences.getString(tokenKey, null)
    }

    @SuppressWarnings("ForbiddenComment")
    actual fun saveToken(accessToken: String) {
        // TODO: Use util-kts library
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        val preferences: SharedPreferences = EncryptedSharedPreferences.create(
            prefsName,
            masterKeyAlias,
            AndroidContextProvider.getContext(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        val editor = preferences.edit()
        editor.putString(tokenKey, accessToken)
        editor.apply()
    }
}

object AndroidContextProvider {
    lateinit var getContext: () -> Context
}
