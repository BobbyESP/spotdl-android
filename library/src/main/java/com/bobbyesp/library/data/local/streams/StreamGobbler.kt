package com.bobbyesp.library.data.local.streams

import android.util.Log
import com.bobbyesp.library.BuildConfig
import java.io.*
import java.nio.charset.StandardCharsets


internal class StreamGobbler(
    private val buffer: StringBuilder, private val stream: InputStream
) : Thread() {

    init {
        start()
    }

    override fun run() {
        try {
            InputStreamReader(stream, StandardCharsets.UTF_8).use { reader ->
                var nextChar: Int
                while (reader.read().also { nextChar = it } != -1) {
                    buffer.append(nextChar.toChar())
                }
            }
        } catch (e: IOException) {
            if (BuildConfig.DEBUG) Log.e(TAG, "Failed to read stream", e)
        }
    }

    companion object {
        private val TAG = StreamGobbler::class.java.simpleName
    }
}