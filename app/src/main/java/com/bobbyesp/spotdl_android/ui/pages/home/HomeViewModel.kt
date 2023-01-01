package com.bobbyesp.spotdl_android.ui.pages.home

import android.content.ContentResolver
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bobbyesp.library.DownloadProgressCallback
import com.bobbyesp.library.SpotDL
import com.bobbyesp.library.SpotDLRequest
import com.bobbyesp.spotdl_android.App.Companion.applicationScope
import com.bobbyesp.spotdl_android.App.Companion.context
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
                    val spotDLDir = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        "spotdl"
                    )
                    if (!spotDLDir.exists()) {
                        spotDLDir.mkdir()
                    }
                    val fullPath = context.filesDir.absolutePath + "/spotdl/.spotdl"
                    Log.d(TAG, "--------------------------------------------------------------------")
                    Log.i(TAG, canAccessDirectory(fullPath).toString())
                    Log.i(TAG, canReadAndWriteFile("$fullPath/ffmpeg").toString())
                    Log.d(TAG, "--------------------------------------------------------------------")

                    val request = SpotDLRequest(link)
                    request.addOption("--output", "/storage/emulated/0/Download/spotdl/")
                    request.addOption("--format", "mp3")
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

class Callback: DownloadProgressCallback {
    override fun onProgressUpdate(progress: Float, eta: Long, line: String) {
        Log.d("MainActivity", "Progress: $progress, ETA: $eta, Line: $line")
    }
}


/*
        // see this: progressCallback: ((Float, Long, String) -> Unit)?
         try {
            val spotDLDir: File = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "spotdl")
            if(!spotDLDir.exists()) {
                spotDLDir.mkdir()
            }
            Log.d("MainActivity", "spotDLDir: $spotDLDir")
            val request = SpotDLRequest("https://open.spotify.com/track/17vXZTcVsCJF1NBoaBQjm7?si=ff8b2f8d6e354416")
            request.addOption("--output", spotDLDir.absolutePath)
            request.addOption("--format", "mp3")
            Log.d("MainActivity", "request: $request")
            var progressCallback: ((Float, Long, String) -> Unit)?
            //get a random id
            val id = UUID.randomUUID().toString()
            SpotDLInstance.execute(request, id, callback = object : DownloadProgressCallback {
                override fun onProgressUpdate(progress: Float, eta: Long, line: String) {
                    Log.d("MainActivity", "progress: $progress")
                    Log.d("MainActivity", "eta: $eta")
                    Log.d("MainActivity", "line: $line")
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("MainActivity", e.message.toString())
        }   */