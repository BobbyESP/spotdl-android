package com.bobbyesp.library.domain.auth

import kotlinx.serialization.Serializable

@Serializable
data class SpotifyCredentials(
    val clientId: String,
    val accessToken: String,
    val accessTokenExpirationTimestampMs: Long,
    val isAnonymous: Boolean
) {
    fun isExpired(): Boolean {
        return System.currentTimeMillis() > accessTokenExpirationTimestampMs
    }

    companion object {
        val EMPTY = SpotifyCredentials(
            clientId = "",
            accessToken = "",
            accessTokenExpirationTimestampMs = -1,
            isAnonymous = true
        )
    }
}
