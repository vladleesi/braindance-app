package dev.vladleesi.braindanceapp.storage

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.vladleesi.braindanceapp.storage.token.AndroidContextProvider
import dev.vladleesi.braindanceapp.storage.token.TokenStorage
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.random.Random

@RunWith(AndroidJUnit4::class)
class TokenStorageTest {

    private val tokenStorage = TokenStorage()
    private val token = Random.nextBytes(Int.SIZE_BYTES).toString()

    init {
        AndroidContextProvider.getContext = { ApplicationProvider.getApplicationContext() }
    }

    @Test
    fun test_saveToken() {
        tokenStorage.saveToken(token)
        val savedToken = tokenStorage.getToken()
        assert(savedToken != null)
        assert(savedToken == token)
    }
}
