package com.bobbyesp.library.dto

import kotlinx.serialization.Serializable

@Serializable
data class Song(
    val name: String,
    val artists: List<String>,
    val artist: String,
    val album_name: String,
    val album_artist: String,
    val genres: List<String>,
    val disc_number: Int,
    val disc_count: Int,
    val duration: Double,
    val year: Int,
    val date: String,
    val track_number: Int,
    val tracks_count: Int,
    val song_id: String,
    val explicit: Boolean,
    val publisher: String,
    val url: String,
    val isrc: String,
    val cover_url: String,
    val copyright_text: String,
    val download_url: String?,
    val song_list: SongList?,
    val list_position: Int?,
    val lyrics: String?
)

@Serializable
data class SongList(
    val name: String,
    val url: String,
    val urls: List<String>,
    val songs: List<Song>,
    val description: String,
    val author_url: String,
    val author_name: String,
    val cover_url: String
)
