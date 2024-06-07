package com.bobbyesp.library

import android.content.Context
import com.bobbyesp.library.util.exceptions.SpotDLException
import com.bobbyesp.spotdl_common.Constants
import com.bobbyesp.spotdl_common.domain.Dependency
import com.bobbyesp.spotdl_common.domain.model.DownloadedDependencies
import com.bobbyesp.spotdl_common.utils.ZipUtils
import com.bobbyesp.spotdl_common.utils.ZipUtils.unzip
import com.bobbyesp.spotdl_common.utils.dependencies.DependenciesUtil
import com.bobbyesp.spotdl_common.utils.dependencies.dependencyDownloadCallback
import org.apache.commons.io.FileUtils
import java.io.File

object SpotDL : SpotDLCore() {
    override fun ensureDependencies(
        appContext: Context,
        skipDependencies: List<Dependency>,
        callback: dependencyDownloadCallback?
    ): DownloadedDependencies =
        DependenciesUtil.ensureDependencies(appContext, skipDependencies, callback)

    /**
     * Initializes Python.
     * @param appContext the application context
     * @param pythonDir the directory where Python is located
     */
    @Throws(SpotDLException::class)
    override fun initPython(appContext: Context, pythonDir: File) {
        val pythonLibrary = File(
            binariesDirectory, Constants.LibrariesName.PYTHON
        )
        if (!pythonDir.exists()) {
            FileUtils.deleteQuietly(pythonDir)
            pythonDir.mkdirs()
            try {
                unzip(pythonLibrary, pythonDir)
            } catch (e: Exception) {
                FileUtils.deleteQuietly(pythonDir)
                throw SpotDLException("Failed to initialize Python", e)
            }
        }
    }

    @JvmStatic
    fun getInstance() = this
}