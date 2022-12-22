package com.bobbyesp.library

import android.content.Context
import android.os.Build
import android.util.Log
import com.bobbyesp.commonutilities.SharedPrefsHelper
import com.bobbyesp.commonutilities.utils.ZipUtilities
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.*

open class SpotDL {

    val baseName = "spotdl-android"

    val spotdlDirName = "spotdl"
    val spotdlBin = "spotdl"

    private val packagesRoot = "packages"

    private val pythonBinName = "libpython.bin.so"
    private val pythonLibName = "libpython.zip.so"
    private val pythonDirName = "python"
    private val pythonLibVersion = "pythonLibVersion"

    private val ffmpegDirName = "ffmpeg"
    private val ffmpegBinName = "libffmpeg.bin.so"

    private var initialized: Boolean = false

    private var pythonPath: File? = null
    private var ffmpegPath: File? = null
    private var spotdlPath: File? = null
    private var binDir: File? = null

    private var ENV_LD_LIBRARY_PATH: String? = null
    private var ENV_SSL_CERT_FILE: String? = null
    private var ENV_PYTHONHOME: String? = null


    private val id2Process = Collections.synchronizedMap(HashMap<String, Process>())

    val objectMapper = ObjectMapper()

    //create a function that can be called out of this class to get the instance
    companion object {
        fun getInstance(): SpotDL {
            return SpotDL()
        }
    }

    @Synchronized
    @Throws(SpotDLException::class)
    open fun init(appContext: Context) {
        if (initialized) {
            return
        }

        val baseDir: File = File(appContext.noBackupFilesDir, baseName)
        Log.d("SpotDL", "Base dir: $baseDir")
        if (!baseDir.exists()) baseDir.mkdir()

        val packagesDir: File = File(baseDir, packagesRoot)

        //Setup the files directories to be used
        binDir = File(appContext.getApplicationInfo().nativeLibraryDir)

        Log.d("SpotDL", "Bin dir: $binDir")

        pythonPath = File(binDir, pythonBinName)

        Log.d("SpotDL", "Python path: $pythonPath")

        ffmpegPath = File(binDir, ffmpegBinName)

        Log.d("SpotDL", "FFMPEG path: $ffmpegPath")

        val pythonDir = File(packagesDir, pythonDirName)
        val ffmpegDir = File(packagesDir, ffmpegDirName)

        val spotDLdir = File(baseDir, spotdlDirName)
        spotdlPath = File(spotDLdir, spotdlBin)

        ENV_LD_LIBRARY_PATH =
            pythonDir.absolutePath + "/usr/lib" + ":" + ffmpegDir.absolutePath + "/usr/lib"
        ENV_SSL_CERT_FILE = pythonDir.absolutePath + "/usr/etc/tls/cert.pem"
        ENV_PYTHONHOME = pythonDir.absolutePath + "/usr"

        //Initialize the python and spotdl files
        try {
            initPython(appContext, pythonDir)
            initSpotDL(appContext, spotDLdir)
        } catch (e: Exception) {
            throw SpotDLException("Error initializing python and spotdl", e)
        }

        initialized = true
    }

    @Throws(SpotDLException::class)
    private fun initPython(appContext: Context, pythonDir: File) {

        val pythonLib: File = File(binDir, pythonLibName)

        // using size of lib as version number
        val pythonSize = pythonLib.length().toString()

        if (!pythonDir.exists() || shouldUpdatePython(appContext, pythonSize)) {

            //We delete the python directory to ensure that we have a clean install
            FileUtils.deleteQuietly(pythonDir)

            //We make another directory to put in it Python
            pythonDir.mkdirs()

            //And now we try to extract the python files
            try {
                ZipUtilities.unzip(pythonLib, pythonDir)
            } catch (e: Exception) {
                FileUtils.deleteQuietly(pythonDir)
                throw SpotDLException("Error extracting python files", e)
            }
            updatePython(appContext, pythonSize)
        }

    }

    private fun updatePython(appContext: Context, version: String) {
        SharedPrefsHelper.update(
            appContext,
            pythonLibVersion,
            version
        )
    }

    private fun shouldUpdatePython(appContext: Context, version: String): Boolean {
        return !version.equals(SharedPrefsHelper.get(appContext, pythonLibVersion))
    }

    @Throws(SpotDLException::class)
    fun initSpotDL(appContext: Context, spotDLdir: File) {
        if (!spotDLdir.exists()) spotDLdir.mkdirs()

        val spotDlBinary: File = File(spotDLdir, spotdlBin)

        if (!spotDlBinary.exists()) {
            try {
                val inputStream = appContext.resources.openRawResource(R.raw.spotdl)
                FileUtils.copyInputStreamToFile(inputStream, spotDlBinary)
            } catch (e: Exception) {
                FileUtils.deleteQuietly(spotDLdir)
                throw SpotDLException("Error extracting spotdl files", e)
            }
        }
    }

    @Throws(SpotDLException::class, InterruptedException::class)
    fun execute(
        request: SpotDLRequest,
        processId: String,
        callback: DownloadProgressCallback?
    ): SpotDLResponse {
        assertInit()
        if (id2Process.containsKey(processId)) throw SpotDLException("Process ID already exists")
        // disable caching unless explicitly requested
        if (!request.hasOption("--cache-path") || request.getOption("--cache-path") == null) {
            request.addOption("--no-cache")
        }

        var spotDLResponse: SpotDLResponse
        val process: Process

        var exitCode: Int = 0
        val outBuffer = StringBuffer() //stdout

        var errBuffer = StringBuffer() //stderr

        var startTime = System.currentTimeMillis()

        var args = request.buildCommand()

        var command: List<String> = listOf(
            pythonPath!!.absolutePath,
            spotdlPath!!.absolutePath
        )

        val argsCollection: Collection<String> = args?.filterNotNull() ?: emptyList()

        command.toMutableList().addAll(argsCollection)

        val processBuilder = ProcessBuilder(command)
        val env = processBuilder.environment()
        env["LD_LIBRARY_PATH"] = ENV_LD_LIBRARY_PATH!!
        env["SSL_CERT_FILE"] = ENV_SSL_CERT_FILE!!
        env["PATH"] = System.getenv("PATH")!! + ":" + binDir!!.absolutePath
        env["PYTHONHOME"] = ENV_PYTHONHOME!!

        try {
            process = processBuilder.start()
            Log.d("SpotDL", "Process started. Process: $process")
        } catch (e: IOException) {
            throw SpotDLException("Error starting process", e)
        }
        Log.d("SpotDL", "Process started")

        if (processId != null) {
            id2Process[processId] = process
        }

        val outStream: InputStream = process.inputStream
        Log.d("SpotDL", "Out stream: $outStream")
        val errStream: InputStream = process.errorStream
        Log.d("SpotDL", "Err stream: $errStream")

        val stdOutProcessor: StreamProcessExtractor = StreamProcessExtractor(
            outBuffer, outStream,
            null
        )

        val stdErrProcessor: StreamProcessExtractor = StreamProcessExtractor(errBuffer, errStream)

        try {
            stdOutProcessor.join()
            stdErrProcessor.join()
            var exitCode = process.waitFor()
        } catch (e: InterruptedException) {
            try {
                process.destroy()
            } catch (e: Exception) {
                Log.w("SpotDL", "Error destroying process or it was ignored", e)
            }
            if (processId != null) id2Process.remove(processId)
            throw e
        }

        if (processId != null) id2Process.remove(processId)

        val out = outBuffer.toString()
        Log.d("SpotDL", "Stdout: $out")
        val err = errBuffer.toString()

        if(exitCode > 0 && !command.contains("--print-errors")) {
            throw SpotDLException("Error executing command: $command, exit code: $exitCode, stderr: $err")
        }

        val elapsedTime = System.currentTimeMillis() - startTime

        spotDLResponse = SpotDLResponse(command, exitCode, elapsedTime, out, err)

        Log.d("SpotDL", "Process: $processId finished with exit code: $exitCode")
        Log.d("SpotDL", "Process: $spotDLResponse")

        return spotDLResponse
    }


    open fun destroyProcessById(id: String): Boolean {
        if (id2Process.containsKey(id)) {
            val p = id2Process[id]
            var alive = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!p!!.isAlive) {
                    alive = false
                }
            }
            if (alive) {
                try {
                    p!!.destroy()
                    return true
                } catch (ignored: Exception) {
                }
            }
        }
        return false
    }

    open fun version(appContext: Context): String? {
        return SpotDLUpdater.getInstance().version(appContext)
    }

    @Throws(SpotDLException::class)
    private fun assertInit() {
        check(initialized) { "instance not initialized" }
    }

    enum class UpdateStatus {
        DONE, ALREADY_UP_TO_DATE
    }
}