package com.bobbyesp.spotdl_android.ui.pages.home

import android.content.ContentResolver
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bobbyesp.library.DownloadProgressCallback
import com.bobbyesp.library.SpotDL
import com.bobbyesp.library.SpotDLRequest
import com.bobbyesp.spotdl_android.App.Companion.applicationScope
import com.bobbyesp.spotdl_android.App.Companion.context
import com.bobbyesp.spotdl_android.BuildConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalMaterial3Api::class)
class HomeViewModel @Inject constructor() : ViewModel() {
    private var currentJob: Job? = null

    //TAG
    private val TAG = "SpotDL"

    fun downloadSong(link: String) {
        currentJob?.cancel()
        currentJob = applicationScope.launch {
            kotlin.runCatching {
                try {

                    givePermsWithChmod("/data/user/0/com.bobbyesp.spotdl_android/files/spotdl/.spotdl", "777")

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

                    val request = SpotDLRequest()
                    request.addOption("download", link)
                    val processId = UUID.randomUUID().toString()

                    //Print every command
                    for (s in request.buildCommand()) Log.d(TAG, s)

                    SpotDL.getInstance()
                        .execute(request, processId, callback = Callback())
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d("MainActivity", "Error downloading song. ${e.message}")
                }
            }
        }
    }

    fun downloadFFmpeg() {
        currentJob?.cancel()
        currentJob = applicationScope.launch {
            kotlin.runCatching {
                try {
                    val request = SpotDLRequest()
                    request.addOption("--download-ffmpeg")
                    val processId = UUID.randomUUID().toString()
                    SpotDL.getInstance()
                        .execute(request, processId, callback = Callback())
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d("MainActivity", "Error downloading song. ${e.message}")
                }
            }
        }
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