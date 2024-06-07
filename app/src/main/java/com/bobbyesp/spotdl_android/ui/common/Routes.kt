package com.bobbyesp.spotdl_android.ui.common

import kotlinx.serialization.Serializable

class Routes {
    companion object {
        const val DOWNLOADER = "downloader"
        const val SETTINGS = "settings"
        const val DOWNLOADS_HISTORY = "downloads_history"
    }
}

@Serializable
sealed interface Route {
    @Serializable
    data object Home : Route
}