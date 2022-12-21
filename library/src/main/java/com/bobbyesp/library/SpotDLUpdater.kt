package com.bobbyesp.library

import android.content.Context
import com.bobbyesp.commonutilities.SharedPrefsHelper
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException
import java.net.URL

open class SpotDLUpdater {

    companion object {
        fun getInstance(): SpotDLUpdater {
            return SpotDLUpdater()
        }
    }

    private val releasesUrl = "https://api.github.com/repos/spotDL/spotify-downloader/releases/latest"
    private val spotDLVersionKey = "spotDLVersion"

    @Throws(IOException::class, SpotDLException::class)
    open fun update(appContext: Context): SpotDL.UpdateStatus {
        val json: JsonNode =
            checkForUpdate(appContext)
                ?: return SpotDL.UpdateStatus.ALREADY_UP_TO_DATE
        val downloadUrl: String =
            getDownloadUrl(json)
        val file: File =
            download(appContext, downloadUrl)
        val spotdlDir: File =
            getSpotDLDir(appContext)
        val binary = File(spotdlDir, "spotdl")
        try {
            /* purge older version */
            if (spotdlDir.exists()) FileUtils.deleteDirectory(spotdlDir)
            /* install newer version */spotdlDir.mkdirs()
            FileUtils.copyFile(file, binary)
        } catch (e: Exception) {
            /* if something went wrong restore default version */
            FileUtils.deleteQuietly(spotdlDir)
            SpotDL.getInstance().initSpotDL(appContext, spotdlDir)
            throw SpotDLException(e)
        } finally {
            file.delete()
        }
        updateSharedPrefs(
            appContext,
            getTag(json)
        )
        return SpotDL.UpdateStatus.DONE
    }

    private  fun updateSharedPrefs(appContext: Context, tag: String) {
        SharedPrefsHelper.update(
            appContext,
            spotDLVersionKey,
            tag
        )
    }

    @Throws(IOException::class)
    private fun checkForUpdate(appContext: Context): JsonNode? {
        val url = URL(releasesUrl)
        val json: JsonNode = SpotDL.getInstance().objectMapper.readTree(url)//YoutubeDL.objectMapper.readTree(url)
        val newVersion: String = getTag(json)
        val oldVersion =
            SharedPrefsHelper[appContext, spotDLVersionKey]
        return if (newVersion == oldVersion) {
            null
        } else json
    }

    private fun getTag(json: JsonNode): String {
        return json["tag_name"].asText()
    }

    @Throws(SpotDLException::class)
    private fun getDownloadUrl(json: JsonNode): String {
        val assets = json["assets"] as ArrayNode
        var downloadUrl = ""
        for (asset in assets) {
            if (SpotDL.getInstance().spotdlBin.equals(asset["name"].asText())) {
                downloadUrl = asset["browser_download_url"].asText()
                break
            }
        }
        if (downloadUrl.isEmpty()) throw SpotDLException("unable to get download url")
        return downloadUrl
    }


    @Throws(IOException::class)
    private fun download(appContext: Context, url: String): File {
        val downloadUrl = URL(url)
        val file = File.createTempFile("spotDL", null, appContext.cacheDir)
        FileUtils.copyURLToFile(downloadUrl, file, 5000, 10000)
        return file
    }

    private fun getSpotDLDir(appContext: Context): File {
        val baseDir = File(appContext.noBackupFilesDir, SpotDL.getInstance().baseName)
        return File(baseDir, SpotDL.getInstance().spotdlDirName)
    }

    open fun version(appContext: Context): String? {
        return SharedPrefsHelper.get(
            appContext,
            spotDLVersionKey
        )
    }
}