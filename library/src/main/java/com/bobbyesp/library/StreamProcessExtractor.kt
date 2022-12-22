package com.bobbyesp.library

import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.nio.charset.StandardCharsets
import java.util.regex.Matcher
import java.util.regex.Pattern

class StreamProcessExtractor(buffer: StringBuffer, stream: InputStream, callback: DownloadProgressCallback? = null): Thread() {

    private val TAG = StreamProcessExtractor::class.java.simpleName

    private var ETA: Long = -1
    private var PERCENT = -1.0f
    private var GROUP_PERCENT = 1
    private var GROUP_MINUTES = 2
    private var GROUP_SECONDS = 3
    private var stream: InputStream? = null
    private var buffer: StringBuffer? = null
    private var callback: DownloadProgressCallback? = null

    private val regexPattern: Pattern =
        Pattern.compile("([+-]?(?=\\.\\d|\\d)(?:\\d+)?(?:\\.?\\d*))(?:[eE]([+-]?\\d+))?")

    private var progress: Float = PERCENT
    private var eta: Long = ETA

    init {
        this.stream = stream
        this.buffer = buffer
        this.callback = callback
        start()
    }

    override fun run() {
        try {
            val reader: Reader = InputStreamReader(stream, StandardCharsets.UTF_8)
            val currentLine = StringBuilder()
            var nextChar: Int
            while (reader.read().also { nextChar = it } != -1) {
                buffer!!.append(nextChar.toChar())
                if (nextChar == '\r'.code || nextChar == '\n'.code && callback != null) {
                    processOutputLine(currentLine.toString())
                    currentLine.setLength(0)
                    continue
                }
                currentLine.append(nextChar.toChar())
            }
        } catch (e: IOException) {
            if (BuildConfig.DEBUG) Log.e(TAG, "failed to read stream", e)
        }
    }

    private fun processOutputLine(line: String) {
        callback!!.onProgressUpdate(getProgress(line), getEta(line), line)
    }

    private fun getProgress(line: String): Float {
        val matcher: Matcher = regexPattern.matcher(line)
        return if (matcher.find()) matcher.group(GROUP_PERCENT)!!.toFloat()
            .also {
                progress = it
            } else progress
    }

    private fun getEta(line: String): Long {
        val matcher: Matcher = regexPattern.matcher(line)
        return if (matcher.find()) convertToSeconds(
            matcher.group(GROUP_MINUTES)!!,
            matcher.group(GROUP_SECONDS)!!
        ).also {
            eta =
                it.toLong()
        }.toLong() else eta
    }

    private fun convertToSeconds(minutes: String, seconds: String): Int {
        return minutes.toInt() * 60 + seconds.toInt()
    }

}