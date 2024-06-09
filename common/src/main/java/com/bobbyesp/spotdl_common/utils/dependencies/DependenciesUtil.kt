package com.bobbyesp.spotdl_common.utils.dependencies

import android.content.Context
import android.util.Log
import com.bobbyesp.spotdl_common.BuildConfig
import com.bobbyesp.spotdl_common.Constants
import com.bobbyesp.spotdl_common.Constants.LIBRARY_NAME
import com.bobbyesp.spotdl_common.Constants.PACKAGES_ROOT_NAME
import com.bobbyesp.spotdl_common.data.remote.dependencies.DependenciesDownloaderImpl
import com.bobbyesp.spotdl_common.domain.CpuArchitecture
import com.bobbyesp.spotdl_common.domain.DependenciesDownloader
import com.bobbyesp.spotdl_common.domain.Dependency
import com.bobbyesp.spotdl_common.domain.Dependency.Companion.toDirectoryName
import com.bobbyesp.spotdl_common.domain.Dependency.Companion.toLibraryName
import com.bobbyesp.spotdl_common.domain.model.DownloadedDependencies
import com.bobbyesp.spotdl_common.domain.model.getMissingDependencies
import com.bobbyesp.spotdl_common.domain.model.updates.Release
import com.bobbyesp.spotdl_common.utils.ZipUtils.unzip
import com.bobbyesp.spotdl_common.utils.network.Ktor.client
import com.bobbyesp.spotdl_common.utils.network.Ktor.makeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.apache.commons.io.FileUtils
import java.io.File

/**
 * Callback for dependency download
 * @param dependency the dependency
 * @param progress the progress of the download (0-100)
 */
typealias dependencyDownloadCallback = (dependency: Dependency, progress: Int) -> Unit

object DependenciesUtil {
    private val dependenciesDownloader: DependenciesDownloader = DependenciesDownloaderImpl()

    /**
     * Deletes a dependency (his files and if it exists)
     * @param context the context
     * @param dependency the dependency to delete
     */
    fun deleteDependency(context: Context, dependency: Dependency): Boolean {
        val binariesDirectory =
            File(context.applicationInfo.nativeLibraryDir) //Here are all the binaries provided by the jniLibs folder
        val dependencyFile = File(binariesDirectory, dependency.toLibraryName())
        return FileUtils.deleteQuietly(dependencyFile)
    }

    /**
     * Unzips a file to the dependency directory
     * @param context the context
     * @param tempFile the file to unzip
     * @param dependency the dependency
     */
    fun unzipToDependencyDirectory(context: Context, tempFile: File, dependency: Dependency) {
        val baseDir = File(context.noBackupFilesDir, LIBRARY_NAME)
        val packagesDir = File(baseDir, PACKAGES_ROOT_NAME)

        val dependencyFile = File(packagesDir, dependency.toDirectoryName())
        unzip(tempFile, dependencyFile)
    }

    /**
     * Checks if the dependencies are installed
     * @return the installed dependencies
     */
    fun checkInstalledDependencies(appContext: Context): DownloadedDependencies {
        val libraryBaseDir = File(appContext.noBackupFilesDir, LIBRARY_NAME)
        val packagesDir = File(libraryBaseDir, PACKAGES_ROOT_NAME)

        val pythonDir = File(
            packagesDir, Constants.DirectoriesName.PYTHON
        )
        val ffmpegDir = File(
            packagesDir, Constants.DirectoriesName.FFMPEG
        )

        val installedDependencies = DownloadedDependencies(
            pythonDir.exists(), ffmpegDir.exists()
        )

        return installedDependencies
    }


    /**
     * Ensures that the necessary dependencies are installed and downloads them if they are missing.
     * This method should be called before initialization.
     *
     * @param appContext The application context.
     * If set to false, Aria2c will not be installed, being marked during the process as installed.
     * @param callback An optional callback function that will be invoked with the dependency and the progress of the download.
     * @return [DownloadedDependencies] The installed dependencies. If Aria2c was skipped, here will
     * be marked as downloaded if it was or as missing if it is really missing.
     * @throws IllegalStateException If the dependencies are still missing after the installation.
     */
    @Throws(IllegalStateException::class)
    fun ensureDependencies(
        appContext: Context,
        skipDependencies: List<Dependency> = emptyList(),
        callback: dependencyDownloadCallback? = null
    ): DownloadedDependencies {
        // Check the currently installed dependencies
        var installedDependencies = checkInstalledDependencies(appContext)

        // If a dependency is not installed and it's in the list of dependencies to skip, mark it as installed
        if (!installedDependencies.python && skipDependencies.contains(Dependency.PYTHON)) {
            installedDependencies = installedDependencies.copy(python = true)
        }
        if (!installedDependencies.ffmpeg && skipDependencies.contains(Dependency.FFMPEG)) {
            installedDependencies = installedDependencies.copy(ffmpeg = true)
        }

        // Install the dependencies. The callback is invoked with the dependency and the progress of the download. Returns the installed dependencies.
        return installDependencies(appContext, installedDependencies) { dependency, progress ->
            callback?.invoke(dependency, progress)
        }
    }

    @Throws(IllegalStateException::class)
    private fun installDependencies(
        appContext: Context,
        downloadedDependencies: DownloadedDependencies,
        callback: dependencyDownloadCallback?
    ): DownloadedDependencies {
        val missingDependencies = downloadedDependencies.getMissingDependencies()
        if (BuildConfig.DEBUG) Log.i("SpotDL", "Missing dependencies: $missingDependencies")

        if (missingDependencies.isNotEmpty()) {
            runBlocking(Dispatchers.IO) {
                missingDependencies.forEach { dependency ->
                    downloadDependency(appContext, dependency, callback)
                }
            }
        }

        val postInstallDependencies = checkInstalledDependencies(appContext)
        if (BuildConfig.DEBUG) {
            val postInstallMissingDependencies = postInstallDependencies.getMissingDependencies()
            if (postInstallMissingDependencies.isNotEmpty()) {
                Log.d(
                    "DependenciesUtil",
                    "Dependencies are still missing after installation: $postInstallMissingDependencies"
                )
            }
        }
        return postInstallDependencies
    }

    private suspend fun downloadDependency(
        appContext: Context,
        dependency: Dependency,
        callback: dependencyDownloadCallback?
    ) {
        if (BuildConfig.DEBUG) Log.i("SpotDL", "Downloading $dependency")
        var lastProgress = 0

        val downloadFunction = when (dependency) {
            Dependency.PYTHON -> dependenciesDownloader::downloadPython
            Dependency.FFMPEG -> dependenciesDownloader::downloadFFmpeg
        }

        downloadFunction(appContext) { progress ->
            if (progress != lastProgress) {
                callback?.invoke(dependency, progress)
                lastProgress = progress
            }
        }
    }

    //The dependency scheme is: [arch]_lib[dependencyName].zip.so
    private const val LATEST_RELEASE =
        "https://api.github.com/repos/bobbyesp/spotdl-android/releases/latest"

    @Throws(Exception::class)
    suspend fun getRelease(releaseUrl: String = LATEST_RELEASE): Release? {
        return makeApiCall<Release?>(client, releaseUrl, null)
    }

    @Throws(Exception::class)
    fun getDownloadLinkForDependency(
        architecture: CpuArchitecture,
        dependency: Dependency,
        release: Release
    ): String {
        val dependencyName = dependency.toString()
        val desiredArch = architecture.name.lowercase()
        val dependencyFileName = "${desiredArch}_lib$dependencyName.zip.so"
        val dependencyAsset = release.assets.find { it.name.lowercase() == dependencyFileName }

        return dependencyAsset?.browser_download_url
            ?: throw Exception("dependency $dependencyName not found for architecture ${architecture.name}")
    }

    @Throws(Exception::class)
    suspend fun getDownloadLinkForDependency(
        architecture: CpuArchitecture,
        dependency: Dependency
    ): String {
        val release = getRelease() ?: throw Exception("No release found")
        return getDownloadLinkForDependency(architecture, dependency, release)
    }
}