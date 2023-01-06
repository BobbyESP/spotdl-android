package com.bobbyesp.library

import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.nio.charset.StandardCharsets
import java.util.regex.Matcher
import java.util.regex.Pattern

internal class StreamProcessExtractor(
    private val buffer: StringBuffer,
    private val stream: InputStream,
    private val callback: ((Float, Long, String) -> Unit)? = null
) : Thread() {

    private val cleanOutRegex: Pattern =
        Pattern.compile("(?:\\x1B[@-Z\\\\-_]|[\\x80-\\x9A\\x9C-\\x9F]|(?:\\x1B\\[|\\x9B)[0-?]*[ -/]*[@-~])")
    init {
        start()
    }

    //Based on this output that changes every second: Alan Walker - Shut Up                    Converting         ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━╺━━━━━━━━━━━━━━━  66% 0:00:02
    //NF - The Search                          Converting         ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━╸━━━━━━━━━━  78% 0:00:01
    //do the next:
    //1. Get the percentage
    //2. Get the ETA
    //3. Get the song name
    //4. Get the current status

    override fun run() {
        try {
            //Read the stream
            val `in`: Reader = InputStreamReader(stream)
            var nextChar: Int

            while (`in`.read().also { nextChar = it } != -1) {
                //appending the char to the buffer
                buffer.append(nextChar.toChar())
                Log.d("StreamProcessExtractor", buffer.toString())
                //Clean the output
                val cleanOutMatcher: Matcher = cleanOutRegex.matcher(buffer)
                val cleanOut = cleanOutMatcher.replaceAll("")
                //Log.d("StreamProcessExtractor", cleanOut)
                //Call the callback with random values except the clean output
                callback?.invoke(1f, 170, cleanOut)
            }

        } catch (e: IOException) {
            if (BuildConfig.DEBUG) Log.e(TAG, "failed to read stream", e)
        }
    }

    private fun processOutputLine(line: String) {
        callback?.let { it(getProgress(line), getEta(line), line) }
    }

    private fun getProgress(line: String): Float{
        return 1f
    }

    private fun getEta(line: String): Long{
        return 1
    }


    //TESTS FIELD
    /*if(isDebug){
        //show the ouput until the process is finished
        val outReader = BufferedReader(InputStreamReader(outStream))
        val errReader = BufferedReader(InputStreamReader(errStream))
        var line: String?
        while (true) {
            Thread.sleep(100)
            line = outReader.readLine()
            if(line == null) break
            outBuffer.append(line)
            outBuffer.append("\n")
            Log.d("SpotDL", "Out: $line")
        }
    }*/

    companion object{
        private val TAG = StreamProcessExtractor::class.java.simpleName

        private var ETA: Long = -1
        private var PERCENT = -1.0f
        private var GROUP_PERCENT = 1
        private var GROUP_MINUTES = 2
        private var GROUP_SECONDS = 3
    }

}