package com.bobbyesp.spotdl_android

import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bobbyesp.library.DownloadProgressCallback
import com.bobbyesp.library.SpotDL
import com.bobbyesp.library.SpotDLRequest
import com.bobbyesp.spotdl_android.ui.theme.SpotdlandroidTheme
import com.yausername.ffmpeg.FFmpeg
import java.io.File
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val SpotDLInstance = SpotDL.getInstance()
        try {
            SpotDLInstance.init(this)
            FFmpeg.getInstance().init(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

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
        }

        setContent {
            SpotdlandroidTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,

                    ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(modifier = Modifier.align(Alignment.Center)) {
                            Greeting(name = "Android")
                        }

                    }
                }

            }

        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SpotdlandroidTheme {
        Greeting("Android")
    }
}