package com.bobbyesp.spotdl_android.utils

import android.util.Log
import android.widget.Toast
import com.bobbyesp.spotdl_android.App.Companion.applicationScope
import com.bobbyesp.spotdl_android.App.Companion.context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object GeneralUtils {

    fun makeToast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }
    fun makeToastSuspend(text: String) {
        applicationScope.launch(Dispatchers.Main) {
            makeToast(text)
        }
    }

    fun convertDuration(durationOfSong: Double): String {
        //First of all the duration comes with this format "146052" but it has to be "146.052"
        var duration = 0.0
        if (durationOfSong > 100000.0){
            duration = durationOfSong / 1000
        } else {
            duration = durationOfSong
        }

        Log.d("GeneralUtils", "convertDuration: $duration")
        val hours = (duration / 3600).toInt()
        val minutes = ((duration % 3600) / 60).toInt()
        val seconds = (duration % 60).toInt()
        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }
}
