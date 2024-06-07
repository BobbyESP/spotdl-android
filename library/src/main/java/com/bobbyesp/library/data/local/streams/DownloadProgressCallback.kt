package com.bobbyesp.library.data.local.streams

interface DownloadProgressCallback {
    fun onProgressUpdate(progress: Float, etaInSeconds: Long, line: String)
}