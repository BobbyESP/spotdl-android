package com.bobbyesp.ffmpeg

import android.content.Context
import android.util.Log
import com.bobbyesp.commonutilities.SharedPrefsHelper
import com.bobbyesp.commonutilities.utils.ZipUtilities
import com.bobbyesp.library.SpotDLException
import java.io.File
import org.apache.commons.io.FileUtils

open class FFmpeg {

    val baseName = "spotdl_android"
    private val packagesRoot = "packages"

    private val ffmpegDirName = "ffmpeg"
    private val ffmpegVersion = "ffmpegLibVersion"
    private val ffmpegLibName = "libffmpeg.zip.so"
    private var binDir: File? = null

    private var initialized = false

    companion object {
        private val ffmpeg: FFmpeg = FFmpeg()

        fun getInstance(): FFmpeg {
            return ffmpeg
        }
    }

    @Synchronized
    @Throws(SpotDLException::class)
    open fun init(appContext: Context) {
        if (initialized) return

        val baseDir = File(appContext.noBackupFilesDir, baseName)
        if (!baseDir.exists()) {
            baseDir.mkdir()
        }

        binDir = File(appContext.applicationInfo.nativeLibraryDir)

        val packagesDir = File(baseDir, packagesRoot)
        val ffmpegDir = File(packagesDir, ffmpegDirName)

        initFFmpeg(appContext, ffmpegDir)

        initialized = true
    }

    @Throws(SpotDLException::class)
    private fun initFFmpeg(appContext: Context, ffmpegDir: File){

        val ffmpegLib = File(binDir, ffmpegLibName)

        //Use size of library as version
        val ffmpegSize = ffmpegLib.length().toString()

        if(!ffmpegDir.exists() || shouldUpdateFFmpeg(appContext, ffmpegSize)) {

            //We delete the python directory to ensure that we have a clean install
            FileUtils.deleteQuietly(ffmpegDir)

            //We make another directory to put in it ffmpeg
            ffmpegDir.mkdirs()

            try {
                Log.i("FFmpeg", "Copying ffmpeg to $ffmpegDir")
                //We extract the ffmpeg library from the assets
                ZipUtilities.unzip(ffmpegLib, ffmpegDir)
            } catch (e: Exception) {
                FileUtils.deleteQuietly(ffmpegDir)
                throw SpotDLException("Failed to unzip FFmpeg library", e)
            }
            updateFFmpeg(appContext, ffmpegSize)
        }

    }
    //    @Throws(SpotDLException::class)
    //    private fun initPython(appContext: Context, pythonDir: File) {
    //
    //        val pythonLib: File = File(binDir, pythonLibName)
    //
    //        // using size of lib as version number
    //        val pythonSize = pythonLib.length().toString()
    //
    //        if (!pythonDir.exists() || shouldUpdatePython(appContext, pythonSize)) {
    //
    //            //We delete the python directory to ensure that we have a clean install
    //            FileUtils.deleteQuietly(pythonDir)
    //
    //            //We make another directory to put in it Python
    //            pythonDir.mkdirs()
    //
    //            //And now we try to extract the python files
    //            try {
    //                ZipUtilities.unzip(pythonLib, pythonDir)
    //            } catch (e: Exception) {
    //                FileUtils.deleteQuietly(pythonDir)
    //                throw SpotDLException("Error extracting python files", e)
    //            }
    //            updatePython(appContext, pythonSize)
    //        }
    //
    //    }

    private fun updateFFmpeg(appContext: Context, version: String) {
        SharedPrefsHelper.update(
            appContext,
            ffmpegVersion,
            version
        )
    }

    private fun shouldUpdateFFmpeg(appContext: Context, version: String): Boolean {
        return version != SharedPrefsHelper[appContext, ffmpegVersion]
    }

}