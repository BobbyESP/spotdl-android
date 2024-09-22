package com.bobbyesp.library

import android.content.Context
import android.util.Log
import com.bobbyesp.library.util.exceptions.SpotDLException
import com.bobbyesp.spotdl_common.Constants
import com.bobbyesp.spotdl_common.domain.Dependency
import com.bobbyesp.spotdl_common.domain.model.DownloadedDependencies
import com.bobbyesp.spotdl_common.utils.ZipUtils.unzip
import com.bobbyesp.spotdl_common.utils.dependencies.dependencyDownloadCallback
import org.apache.commons.io.FileUtils
import java.io.File

object SpotDL : SpotDLCore() {
    override fun ensureDependencies(
        appContext: Context,
        skipDependencies: List<Dependency>,
        callback: dependencyDownloadCallback?
    ): DownloadedDependencies? = null

    /**
     * Initializes Python.
     * @param appContext the application context
     * @param pythonDir the directory where Python is located
     */
    @Throws(SpotDLException::class)
    override fun initPython(appContext: Context, pythonDir: File) {
        val pythonLibrary = File(binariesDirectory, Constants.LibrariesName.PYTHON)
        val pythonZipSize = pythonLibrary.length().toString()

        if (!pythonDir.exists() || shouldUpdatePython(appContext, pythonZipSize)) {
            FileUtils.deleteQuietly(pythonDir)
            pythonDir.mkdirs()
            unzipPythonLibrary(pythonLibrary, pythonDir)
        } else {
            logDebug("Python library already exists or doesn't need to be updated")
        }
    }

    private fun unzipPythonLibrary(pythonLibrary: File, pythonDir: File) {
        try {
            logDebug("Unzipping Python library")
            unzip(pythonLibrary, pythonDir)
            logDebug("Unzipped finished for the Python library")
        } catch (e: Exception) {
            FileUtils.deleteQuietly(pythonDir)
            throw SpotDLException("Failed to initialize Python", e)
        }
    }

    private fun logDebug(message: String) {
        if (isDebug) Log.i("SpotDL", message)
    }

    @JvmStatic
    fun getInstance() = this
}