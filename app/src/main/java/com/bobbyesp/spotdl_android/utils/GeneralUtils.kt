package com.bobbyesp.spotdl_android.utils

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

    fun convertDuration(duration: Double): String {
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
