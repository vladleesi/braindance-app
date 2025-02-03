package dev.vladleesi.braindanceapp.data.token

expect class TokenStorage() {
    fun saveToken(token: String)

    fun getToken(): String?

    fun clearToken()
}

object TokenStorageConstants {
    const val SECURE_PREFS_KEY = "secure_token_prefs"
    const val AUTH_TOKEN_KEY = "auth_token"
}
