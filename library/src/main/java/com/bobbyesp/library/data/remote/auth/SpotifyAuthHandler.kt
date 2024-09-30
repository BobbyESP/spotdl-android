package com.bobbyesp.library.data.remote.auth

import com.bobbyesp.library.domain.auth.SpotifyCredentials
import com.bobbyesp.library.domain.auth.SpotifyTokenExpired
import com.bobbyesp.spotdl_common.utils.network.Ktor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SpotifyAuthHandler {
    private lateinit var credentials: SpotifyCredentials
    private val tokenGeneratorUrl =
        "https://open.spotify.com/get_access_token?reason=transport&productType=web_player"

    init {
        coroutineScope.launch {
            initializeCredentials()
        }
    }

    private suspend fun createCredentials(): SpotifyCredentials = withContext(coroutineScope.coroutineContext) {
        Ktor.get<SpotifyCredentials>(
            url = tokenGeneratorUrl,
            params = null
        )
    }

    @Throws(IllegalStateException::class, SpotifyTokenExpired::class)
    fun getCredentials(): SpotifyCredentials {
        if (!::credentials.isInitialized) {
            throw IllegalStateException("Spotify Credentials are not initialized")
        }

        if (credentials.isExpired()) {
            throw SpotifyTokenExpired("The Spotify Web Player token has expired. Request a new one and try again.")
        }

        return credentials
    }

    suspend fun refreshCredentials() {
        credentials = createCredentials()
    }

    fun initializeCredentials() {
        if (!::credentials.isInitialized) {
            coroutineScope.launch {
                credentials = createCredentials()
            }
        }
    }

    fun clearCredentials() {
        if (::credentials.isInitialized) {
            credentials = SpotifyCredentials.EMPTY
        }
    }

    companion object {
        val coroutineScope = CoroutineScope(Dispatchers.IO)
    }
}