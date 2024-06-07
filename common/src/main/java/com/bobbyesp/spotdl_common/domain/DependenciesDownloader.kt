package com.bobbyesp.spotdl_common.domain

import android.content.Context

interface DependenciesDownloader {
    suspend fun downloadPython(context: Context, progressCallback: (progress: Int) -> Unit)
    suspend fun downloadFFmpeg(context: Context, progressCallback: (progress: Int) -> Unit)
}