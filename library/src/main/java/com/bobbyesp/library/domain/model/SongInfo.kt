package com.bobbyesp.library.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SpotifySong(
    val name: String = "",
    val artists: List<String> = listOf(),
    val artist: String = "",
    val album_name: String = "",
    val album_artist: String = "",
    val genres: List<String>? = listOf(),
    val disc_number: Int? = 0,
    val disc_count: Int? = 0,
    val duration: Double = 0.0,
    val year: Int = 0,
    val date: String = "",
    val track_number: Int? = 0,
    val tracks_count: Int? = 0,
    val song_id: String = "",
    val explicit: Boolean = false,
    val publisher: String? = "",
    val url: String = "",
    val isrc: String? = "",
    val cover_url: String = "",
    val copyright_text: String? = "",
    val download_url: String? = null,
    val song_list: SpotifyPlaylist? = null,
    val list_position: Int? = null,
    val lyrics: String? = null,
    val album_id: String? = null,
)

@Serializable
data class SpotifyPlaylist(
    val name: String = "",
    val url: String = "",
    val urls: List<String> = listOf(),
    val spotifySongs: List<SpotifySong> = listOf(),
    val genres: List<String>? = listOf(),
    val albums: List<String>? = listOf(),
    val artist: SpotifyArtist = SpotifyArtist(),
    val description: String = "",
    val author_url: String = "",
    val author_name: String = "",
    val cover_url: String = "",
)

@Serializable
data class SpotifyArtist(
    val external_urls: Map<String, String> = emptyMap(),
    val href: String = "",
    val id: String = "",
    val name: String = "",
    val type: String = "",
    val uri: String = ""
)