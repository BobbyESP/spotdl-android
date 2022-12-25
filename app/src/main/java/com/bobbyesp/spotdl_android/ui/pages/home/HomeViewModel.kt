package com.bobbyesp.spotdl_android.ui.pages.home

import android.os.Environment
import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bobbyesp.library.DownloadProgressCallback
import com.bobbyesp.library.SpotDL
import com.bobbyesp.library.SpotDLRequest
import com.bobbyesp.spotdl_android.App.Companion.applicationScope
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

    //tag
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
                    //val request = SpotDLRequest("download $link")
                    val request = SpotDLRequest(link)
                    request.addOption("--format", "mp3")
                    var progressCallback: ((Float, Long, String) -> Unit)?
                    val processId = UUID.randomUUID().toString()

                    for (s in request.buildCommand()) Log.d(TAG, s)

                    SpotDL.getInstance()
                        .execute(request, processId, callback = object : DownloadProgressCallback {
                            override fun onProgressUpdate(
                                progress: Float,
                                eta: Long,
                                line: String
                            ) {
                                Log.d("MainActivity", "progress: $progress")
                                Log.d("MainActivity", "eta: $eta")
                                Log.d("MainActivity", "line: $line")
                            }
                        })
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d("MainActivity", "Error downloading song. ${e.message}")
                }
            }
        }
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