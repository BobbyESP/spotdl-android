package com.bobbyesp.library.domain.auth

class SpotifyTokenExpired(
    message: String = "Spotify token has expired.",
    cause: Throwable? = null
) : Exception(message, cause)