package com.bobbyesp.spotdl_android.ui

import com.bobbyesp.library.dto.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object StateHolder {

    val mutableTaskState = MutableStateFlow(DownloadTaskItem())
    val taskState = mutableTaskState.asStateFlow()


    data class DownloadTaskItem(
        val title: String = "",
        val progress: Float = 0f,
        val progressText: String = "Working...",
        val songInfo: List<Song> = emptyList(),
        val isDownloading: Boolean = false,
        val url : String = "",
    )
}