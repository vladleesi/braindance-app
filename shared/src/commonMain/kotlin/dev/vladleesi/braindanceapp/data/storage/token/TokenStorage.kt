package dev.vladleesi.braindanceapp.data.storage.token

expect class TokenStorage constructor() {
    fun getToken(): String?

    fun saveToken(accessToken: String)
}
