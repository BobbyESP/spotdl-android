package com.bobbyesp.library

import android.util.Log
import java.io.*
import java.nio.charset.StandardCharsets


/*class StreamGobbler2 : Thread {
    private val inputStream: InputStream
    private val type: String
    private val output: StringBuilder

    constructor(inputStream: InputStream, type: String) {
        this.inputStream = inputStream
        this.type = type
        output = StringBuilder()
    }

    constructor(inputStream: InputStream, type: String, output: StringBuilder) {
        this.inputStream = inputStream
        this.type = type
        this.output = output
    }

    override fun run() {
        try {
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun getOutput(): String {
        return output.toString()
    }
}*/

class StreamGobbler(buffer: StringBuffer, stream: InputStream) : Thread() {
    private var stream: InputStream? = stream
    private var buffer: StringBuffer? = buffer

    private val TAG = StreamGobbler::class.java.simpleName

    init {
        start()
    }

    override fun run() {
        try {
            val inputReader: Reader = InputStreamReader(stream, StandardCharsets.UTF_8)
            var nextChar: Int
            while (inputReader.read().also { nextChar = it } != -1) {
                buffer!!.append(nextChar.toChar())
            }
        } catch (e: IOException){
            if(BuildConfig.BUILD_TYPE == "debug"){
                Log.e(TAG, "Error reading stream", e)
            }
        }
    }

}