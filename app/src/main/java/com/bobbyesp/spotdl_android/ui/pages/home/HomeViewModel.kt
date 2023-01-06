package com.bobbyesp.spotdl_android.ui.pages.home

import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import com.bobbyesp.library.DownloadProgressCallback
import com.bobbyesp.library.SpotDL
import com.bobbyesp.library.SpotDLRequest
import com.bobbyesp.library.dto.Song
import com.bobbyesp.spotdl_android.App.Companion.applicationScope
import com.bobbyesp.spotdl_android.App.Companion.context
import com.bobbyesp.spotdl_android.BuildConfig
import com.bobbyesp.spotdl_android.ui.StateHolder.mutableTaskState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalMaterial3Api::class)
class HomeViewModel @Inject constructor() : ViewModel() {
    private var currentJob: Job? = null

    //TAG
    private val TAG = "HomeViewModel"

    fun downloadSong(link: String, progressCallback: ((Float, Long, String) -> Unit)?) {
        currentJob?.cancel()
        currentJob = applicationScope.launch {
            //if a looper is not present, the app will crash so we need to create one and if it is present, we need to use it
            val looper = if (Looper.myLooper() == null) {
                Looper.prepare()
                Looper.myLooper()
            } else {
                Looper.myLooper()
            }
            kotlin.runCatching {
                try {
                    Toast.makeText(context, "Downloading...", Toast.LENGTH_SHORT).show()
                    val spotDLDir = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        "spotdl"
                    )
                    if (!spotDLDir.exists()) {
                        spotDLDir.mkdir()
                    }
                    val fullPath = context.filesDir.absolutePath + "/spotdl/.spotdl"
                    val pathUri: Uri = FileProvider.getUriForFile(
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
                    Log.i(TAG, pathUri.toString())
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
                        it.copy(songInfo = songInfo)
                    }

                    val request = SpotDLRequest()
                    request.addOption("download", link)
                    request.addOption("--log-level", "DEBUG")
                    request.addOption("--output", downloadDir.absolutePath)
                    val processId = UUID.randomUUID().toString()

                    //Print every command
                    for (s in request.buildCommand()) Log.d(TAG, s)

                    SpotDL.getInstance()
                        .execute(request, processId, progressCallback)
                    Toast.makeText(context, "Downloaded!", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d("MainActivity", "Error downloading song. ${e.message}")
                }
            }
        }
    }
    fun requestSongInfo(url: String): List<Song> {
        var info: List<Song> = emptyList()
        currentJob?.cancel()
        currentJob = applicationScope.launch {
            kotlin.runCatching {
                try {
                    val songInfo = SpotDL.getInstance().getSongInfo(url)
                    Log.i(TAG, songInfo.toString())
                    info = songInfo
                    mutableTaskState.update {
                        it.copy(songInfo = info)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d("MainActivity", "Error downloading song. ${e.message}")
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


    //Give permissions to a directory with chmod 777
    private fun givePermsWithChmod(path: String, perms: String = "777") {
        val command = "chmod -R $perms $path"
        val process = Runtime.getRuntime().exec(command)
        process.waitFor()

    }

    //Check if the app can access to a directory
    fun canAccessDirectory(path: String): Boolean {
        Log.d("Can Access Directory", "Checking if the app can access to $path")
        val file = File(path)
        return file.exists() && file.canRead() && file.canWrite() && file.isDirectory
    }

    //can read and write file
    fun canReadAndWriteFile(path: String): Boolean {
        Log.d("Can Read and Write File", "Checking if the app can read and write to $path")
        val file = File(path)
        return file.exists() && file.canRead() && file.canWrite() && file.isFile
    }

}

class Callback : DownloadProgressCallback {
    override fun onProgressUpdate(progress: Float, eta: Long, line: String) {
        Log.d("MainActivity", "Progress: $progress, ETA: $eta, Line: $line")
    }
}