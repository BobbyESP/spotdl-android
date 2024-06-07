package com.bobbyesp.spotdl_common.data.remote.dependencies

import android.content.Context
import com.bobbyesp.spotdl_common.Constants.LibrariesName.TemporalFilesName.TEMPORAL_FFMPEG
import com.bobbyesp.spotdl_common.Constants.LibrariesName.TemporalFilesName.TEMPORAL_PYTHON
import com.bobbyesp.spotdl_common.domain.DependenciesDownloader
import com.bobbyesp.spotdl_common.domain.Dependency
import com.bobbyesp.spotdl_common.utils.dependencies.DependenciesUtil.getDownloadLinkForDependency
import com.bobbyesp.spotdl_common.utils.dependencies.DependenciesUtil.unzipToDependencyDirectory
import com.bobbyesp.spotdl_common.utils.device.CpuUtils
import com.bobbyesp.spotdl_common.data.remote.FileDownloader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.File

class DependenciesDownloaderImpl : DependenciesDownloader {
    private val fileDownloader by lazy { FileDownloader }
    override suspend fun downloadPython(
        context: Context,
        progressCallback: (progress: Int) -> Unit
    ) {
        val tempFile = withContext(Dispatchers.IO) {
            File.createTempFile(TEMPORAL_PYTHON, null)
        }

        val urlDeferred = withContext(Dispatchers.IO) {
            async { getDownloadLinkForDependency(CpuUtils.getPreferredAbi(), Dependency.PYTHON) }
        }

        val downloadUrl = urlDeferred.await()

        fileDownloader.downloadFileWithProgress(
            fileUrl = downloadUrl,
            localFile = tempFile,
            progressCallback = progressCallback,
            overwrite = true
        )

        unzipToDependencyDirectory(context, tempFile, Dependency.PYTHON)
    }

    override suspend fun downloadFFmpeg(
        context: Context,
        progressCallback: (progress: Int) -> Unit
    ) {
        val tempFile = withContext(Dispatchers.IO) {
            File.createTempFile(TEMPORAL_FFMPEG, null)
        }

        val urlDeferred = withContext(Dispatchers.IO) {
            async { getDownloadLinkForDependency(CpuUtils.getPreferredAbi(), Dependency.FFMPEG) }
        }

        val downloadUrl = urlDeferred.await()

        fileDownloader.downloadFileWithProgress(
            fileUrl = downloadUrl,
            localFile = tempFile,
            progressCallback = progressCallback,
            overwrite = true
        )

        unzipToDependencyDirectory(context, tempFile, Dependency.FFMPEG)
    }
}