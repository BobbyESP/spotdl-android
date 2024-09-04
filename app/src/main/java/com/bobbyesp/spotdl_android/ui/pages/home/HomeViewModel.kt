package com.bobbyesp.spotdl_android.ui.pages.home

import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import com.bobbyesp.library.SpotDL
import com.bobbyesp.library.SpotDLRequest
import com.bobbyesp.library.domain.model.SpotifySong
import com.bobbyesp.spotdl_android.App.Companion.applicationScope
import com.bobbyesp.spotdl_android.App.Companion.context
import com.bobbyesp.spotdl_android.BuildConfig
import com.bobbyesp.spotdl_android.ui.StateHolder.mutableTaskState
import com.bobbyesp.spotdl_android.utils.StorageUtil.canAccessDirectory
import com.bobbyesp.spotdl_android.utils.StorageUtil.canReadAndWriteFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    private var currentJob: Job? = null

    //TAG
    private val TAG = "HomeViewModel"

    //package manager
    private val packageManager = context.packageManager

    fun downloadSong(link: String, progressCallback: ((Float, Long, String) -> Unit)?) {
        currentJob?.cancel()
        currentJob = applicationScope.launch {
            mutableTaskState.update {
                it.copy(progress = 0f)
            }
            kotlin.runCatching {
                try {
                    mutableTaskState.update {
                        it.copy(isDownloading = true, progress = 0f)
                    }
                    val spotDLDir = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        "spotdl"
                    )
                    if (!spotDLDir.exists()) {
                        spotDLDir.mkdir()
                    }
                    val fullPath = context.filesDir.absolutePath + "/spotdl/.spotdl"
                    val pathToFfmpeg: Uri = FileProvider.getUriForFile(
                        context, BuildConfig.APPLICATION_ID + ".provider", File(
                            "$fullPath/ffmpeg"
                        )
                    )
                    Log.d(
                        TAG,
                        "--------------------------------------------------------------------"
                    )
                    Log.i(TAG, canAccessDirectory(fullPath).toString())
                    Log.i(TAG, canReadAndWriteFile("$fullPath/ffmpeg").toString())
                    Log.i(TAG, pathToFfmpeg.toString())
                    Log.d(
                        TAG,
                        "--------------------------------------------------------------------"
                    )

                    //Directory of downloads
                    val downloadDir = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        "spotdl"
                    )

                    //Request song info
                    val songInfo = SpotDL.getInstance().getSongInfo(link)

                    mutableTaskState.update {
                        it.copy(spotifySongInfo = songInfo)
                    }

                    val request = SpotDLRequest()
                    request.addOption("download", link)
                    request.addOption("--log-level", "DEBUG")
                    //request.addOption("--simple-tui")
                    request.addOption("--output", downloadDir.absolutePath)

                    val processId = "${link}_${link.reversed()}"

                    //Print every command
                    for (s in request.buildCommand()) Log.d(TAG, s)

                    SpotDL.getInstance()
                        .execute(request, processId, progressCallback)

                    cleanUpDownload()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d("MainActivity", "Error downloading song. ${e.message}")
                    cleanUpDownload()
                }
            }
        }
    }

    fun cancelDownload(id: String){
        currentJob?.cancel()
        try {
            SpotDL.getInstance().destroyProcessById(id)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("MainActivity", "Error cancelling download. ${e.message}")
        }
    }
    fun requestSongInfo(url: String): List<SpotifySong> {
        var info: List<SpotifySong> = emptyList()
        currentJob?.cancel()
        currentJob = applicationScope.launch {
            kotlin.runCatching {
                try {
                    mutableTaskState.update {
                        it.copy(isDownloading = true)
                    }
                    val songInfo = SpotDL.getInstance().getSongInfo(url)
                    Log.i(TAG, songInfo.toString())
                    info = songInfo
                    mutableTaskState.update {
                        it.copy(spotifySongInfo = info, isDownloading = false)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d("MainActivity", "Error requesting song info. ${e.message}")
                    cleanUpDownload()
                }
            }
        }
        return info
    }

    //open the url in the browser
    fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    private fun cleanUpDownload(){
        mutableTaskState.update {
            it.copy(progress = 0f, isDownloading = false, progressText = "")
        }
    }

    fun openDownloadsFolder() {
        //open the downloads folder in the system file manager
        val intent = Intent(Intent.ACTION_VIEW)
        val downloadDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "spotdl"
        )
        Log.d(TAG, downloadDir.absolutePath)
        val uri: Uri = Uri.parse(downloadDir.absolutePath)
        intent.setDataAndType(uri, "*/*")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val activities = packageManager.queryIntentActivities(intent, 0)
        if(activities.isNotEmpty()){
            context.startActivity(intent)
        }else{
            Toast.makeText(context, "No file manager found", Toast.LENGTH_SHORT).show()
        }
    }

    fun updateSpotDLLibrary() {
        applicationScope.launch {
            SpotDL.getInstance().updateSpotDL(context)
        }
    }
}