package com.bobbyesp.spotdl_android

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Environment
import android.util.Log
import com.bobbyesp.library.SpotDL
import com.bobbyesp.spotdl_android.utils.PreferencesUtil
import com.bobbyesp.spotdl_android.utils.PreferencesUtil.AUDIO_DIRECTORY
import com.tencent.mmkv.MMKV
import com.yausername.ffmpeg.FFmpeg
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
        context = applicationContext
        applicationScope = CoroutineScope(SupervisorJob())
        val SpotDLInstance = SpotDL.getInstance()
        applicationScope.launch((Dispatchers.IO)) {
            try {
                SpotDLInstance.init(this@App)
                FFmpeg.getInstance().init(this@App)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("SpotDL", "Error initializing SpotDL. ${e.message}")
            }
        }


        with(PreferencesUtil.getString(AUDIO_DIRECTORY)) {
            audioDownloadDir = if (isNullOrEmpty()) File(audioDownloadDir, "Audio").absolutePath
            else this
        }
    }

    companion object {
        var audioDownloadDir: String = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "spotdl").absolutePath
        lateinit var applicationScope: CoroutineScope

        fun updateDownloadDir(path: String) {
            audioDownloadDir = path
            PreferencesUtil.updateString(AUDIO_DIRECTORY, path)
        }

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }
}
