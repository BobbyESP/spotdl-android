package com.bobbyesp.library

import android.content.Context
import com.bobbyesp.commonutilities.SharedPrefsHelper
import com.bobbyesp.library.dto.Release
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
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
    fun update(appContext: Context, apiUrl: String? = null): SpotDL.UpdateStatus {
        val json = checkForUpdate(appContext, apiUrl) ?: return SpotDL.UpdateStatus.ALREADY_UP_TO_DATE
        val downloadUrl = getDownloadUrl(json)
        val file = download(appContext, downloadUrl)
        val spotdlDir = getSpotDLDir(appContext)
        val binary = File(spotdlDir, "spotdl")
        try {
            /* purge older version */
            if (spotdlDir.exists()) FileUtils.deleteDirectory(spotdlDir)
            /* install newer version */
            spotdlDir.mkdirs()
            FileUtils.copyFile(file, binary)
        } catch (e: Exception) {
            /* if something went wrong restore default version */
            FileUtils.deleteQuietly(spotdlDir)
            SpotDL.getInstance().initSpotDL(appContext, spotdlDir)
            throw SpotDLException(e)
        } finally {
            file.delete()
        }
        updateSharedPrefs(appContext, getTag(json))
        return SpotDL.UpdateStatus.DONE
    }

    private fun updateSharedPrefs(appContext: Context, tag: String) {
        //change the spotDLVersionKey to the new version tag
        SharedPrefsHelper.update(appContext, spotDLVersionKey, tag)
    }

    //check for updates+
    @Throws(IOException::class, SpotDLException::class)
    private fun checkForUpdate(appContext: Context, apiUrl: String? = null): JsonElement? {
        val url = apiUrl ?: releasesUrl
        val json = Json.parseToJsonElement(URL(url).readText())
        val tag = getTag(json)
        val currentVersion = SharedPrefsHelper[appContext, spotDLVersionKey] ?: ""
        return if (tag != currentVersion) json else null
    }

    private fun getTag(json: JsonElement): String {
        return json.jsonObject["tag_name"]?.jsonPrimitive?.content ?: ""
    }

    @Throws(SpotDLException::class)
    private fun getDownloadUrl(json: JsonElement): String {
        val assets = json.jsonObject["assets"]?.jsonArray
        var downloadUrl = ""
        assets?.forEach { asset ->
            if (SpotDL.getInstance().spotdlBin == asset.jsonObject["name"]?.jsonPrimitive?.content) {
                downloadUrl = asset.jsonObject["browser_download_url"]?.jsonPrimitive?.content ?: ""
                return@forEach
            }
        }
        if (downloadUrl.isEmpty()) throw SpotDLException("unable to get download url")
        return downloadUrl
    }


    @Throws(IOException::class)
    private fun download(appContext: Context, url: String): File {
        val downloadUrl = URL(url)
        val file = File.createTempFile("spotdl", null, appContext.cacheDir)
        FileUtils.copyURLToFile(downloadUrl, file, 5000, 10000)
        //change the file name to spotdl
        file.renameTo(File(file.parent, SpotDL.getInstance().spotdlBin))
        return file
    }

    private fun getSpotDLDir(appContext: Context): File {
        val baseDir = File(appContext.noBackupFilesDir, SpotDL.getInstance().baseName)
        return File(baseDir, SpotDL.getInstance().spotdlDirName)
    }

    open fun version(appContext: Context): String? {
        return SharedPrefsHelper[appContext, spotDLVersionKey]
    }
}