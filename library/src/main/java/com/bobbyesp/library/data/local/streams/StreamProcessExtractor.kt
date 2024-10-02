package com.bobbyesp.library.data.local.streams

import android.util.Log
import com.bobbyesp.library.BuildConfig
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.regex.Pattern

internal class StreamProcessExtractor(
    private val buffer: StringBuilder,
    private val stream: InputStream,
    private val callback: ((Float, Long, String) -> Unit)? = null
) : Thread() {

    private val cleanOutRegex: Pattern = Pattern.compile(
        "(\\x1B[@-Z\\\\-_]|[\\x80-\\x9A\\x9C-\\x9F]|(?:\\x1B\\[|\\x9B)[0-?]*[ -/]*[@-~])"
    )
    private val uniqueLines = HashSet<String>()

    init { 
        start()
    }

    override fun run() {
        try {
            BufferedReader(InputStreamReader(stream, StandardCharsets.UTF_8)).use { reader ->
                val arrayOfLines = mutableListOf<String>()
                reader.forEachLine { line ->
                    val cleanLine = cleanOutRegex.matcher(line).replaceAll("")
                    if (cleanLine.isNotEmpty() && uniqueLines.add(cleanLine)) {
                        processOutputLine(cleanLine)
                        arrayOfLines.add(cleanLine)
                    }
                }
                buffer.append(arrayOfLines.joinToString("\n"))
            }
        } catch (e: IOException) {
            if (BuildConfig.DEBUG) Log.e(TAG, "Failed to read stream", e)
        }
    }

    private fun processOutputLine(line: String) {
        if (BuildConfig.DEBUG) Log.d(TAG, line)
        callback?.invoke(getProgress(line), getEta(line), line)
    }

    private fun getProgress(line: String): Float {
        val regex = Regex("(\\d+)%")
        val matchResult = regex.find(line)
        val progress = matchResult?.groupValues?.get(1)?.toFloatOrNull() ?: 0f
        if (BuildConfig.DEBUG) Log.d(TAG, "Progress: $progress")
        return progress / 100f
    }

    private fun getEta(line: String): Long {
        val regex = Regex("(\\d+:\\d+:\\d+)")
        val matchResult = regex.find(line)
        val timeParts = matchResult?.groupValues?.get(1)?.split(":")?.map {
            it.toIntOrNull() ?: 0
        } ?: listOf(0, 0, 0)
        val (hours, minutes, seconds) = timeParts
        val eta = (hours * 3600 + minutes * 60 + seconds).toLong()
        if (BuildConfig.DEBUG) Log.d(TAG, "ETA: $hours:$minutes:$seconds")
        return eta
    }

    companion object {
        private val TAG = StreamProcessExtractor::class.java.simpleName
    }
}