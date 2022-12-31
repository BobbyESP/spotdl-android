package com.bobbyesp.library

import android.content.Context
import android.os.Build
import android.util.Log
import com.bobbyesp.commonutilities.SharedPrefsHelper
import com.bobbyesp.commonutilities.utils.ZipUtilities
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.*

open class SpotDL {

    /*
    * INFO: Python tries to run a binary file that is standalone and does not require any type of python installation or dependencies.
    * The thing is that the binary file is not compatible with all the android devices (or it's kinda hard). So, we need to run the python file instead.
    * We should take the source code and run it directly here. The problem, the dependencies. We need to install them first. So, we need to run the libraries installation python file first.
    * Then, we can run the main python file and run the library.
    * */

    //lib.so.6: https://www.golinuxcloud.com/how-do-i-install-the-linux-library-libc-so-6/
    //Because: ImportError: dlopen failed: library "libc.so.6" not found: needed by /data/data/com.bobbyesp.spotdl_android/no_backup/spotdl_android/packages/python/usr/lib/python3.8/site-packages/pydantic/__init__.cpython-38.so in namespace (default)

    val baseName = "spotdl_android"

    val spotdlDirName = "spotdl"
    val spotdlBin = "spotdl_bin"

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
    private var HOME: String? = null
    private var LDFLAGS: String? = null


    private val id2Process = Collections.synchronizedMap(HashMap<String, Process>())

    val objectMapper = ObjectMapper()

    //create a function that can be called out of this class to get the instance
    companion object {
        private val spotDl: SpotDL = SpotDL()

        fun getInstance(): SpotDL {
            return spotDl
        }
    }

    @Synchronized
    @Throws(SpotDLException::class)
    open fun init(appContext: Context) {
        if (initialized) {
            return
        }

        val termuxSpotDLPath_Text = File("/data/data/com.termux/files/home/.spotdl")
        if(!termuxSpotDLPath_Text.exists()) {
            termuxSpotDLPath_Text.mkdirs()
        }

        val baseDir = File(appContext.noBackupFilesDir, baseName)
        Log.d("SpotDL", "Base dir: $baseDir")
        if (!baseDir.exists()) baseDir.mkdir()

        val packagesDir = File(baseDir, packagesRoot)

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

        val appPath = File(appContext.filesDir, "spotdl")

        ENV_LD_LIBRARY_PATH =
            pythonDir.absolutePath + "/usr/lib" + ":" + ffmpegDir.absolutePath + "/usr/lib"
        ENV_SSL_CERT_FILE = pythonDir.absolutePath + "/usr/etc/tls/cert.pem"
        ENV_PYTHONHOME = pythonDir.absolutePath + "/usr"
        HOME = appPath.absolutePath
        LDFLAGS = "-rdynamic"

        //Initialize the python and spotdl files
        try {
            if(HOME != null) {
                val homeDir = File(HOME)
                if (!homeDir.exists()) {
                    homeDir.mkdirs()
                }
            }
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
        return version != SharedPrefsHelper[appContext, pythonLibVersion]
    }

    @Throws(SpotDLException::class)
    fun initSpotDL(appContext: Context, spotDLdir: File) {
        if (!spotDLdir.exists()) spotDLdir.mkdirs()

        val spotDlBinary = File(spotDLdir, spotdlBin)

        if (!spotDlBinary.exists()) {
            try {
                //See https://github.com/containerd/containerd/blob/269548fa27e0089a8b8278fc4fc781d7f65a939b/platforms/platforms.go#L88
                //Also https://www.digitalocean.com/community/tutorials/building-go-applications-for-different-operating-systems-and-architectures
                val binaryFileId = R.raw.spotdl_bin
                val outpuFile = File(spotDlBinary.absolutePath)
                copyRawResourceToFile(appContext, binaryFileId, outpuFile)
            } catch (e: Exception) {
                FileUtils.deleteQuietly(spotDLdir)
                throw SpotDLException("Error extracting spotdl files", e)
            }
        }
    }

    private fun copyRawResourceToFile(context: Context, resourceId: Int, file: File) {
        val inputStream = context.resources.openRawResource(resourceId)
        val outputStream = FileOutputStream(file)
        val buffer = ByteArray(1024)
        var read = inputStream.read(buffer)
        while (read != -1) {
            outputStream.write(buffer, 0, read)
            read = inputStream.read(buffer)
        }
        outputStream.close()
        inputStream.close()
    }

    @Throws(SpotDLException::class, InterruptedException::class)
    fun execute(
        request: SpotDLRequest,
        processId: String,
        callback: DownloadProgressCallback?
    ): SpotDLResponse {
        assertInit()
        if (id2Process.containsKey(processId)) throw SpotDLException("Process ID already exists")
        // disable caching unless it is explicitly requested
        if (!request.hasOption("--cache-path") || request.getOption("--cache-path") == null) {
            request.addOption("--no-cache")
        }

        var spotDLResponse: SpotDLResponse
        val process: Process

        var exitCode: Int = 0

        val outBuffer = StringBuffer() //stdout
        var errBuffer = StringBuffer() //stderr

        var startTime = System.currentTimeMillis()

        val args = request.buildCommand()

        //Full command
        val command = mutableListOf<String>()
        command.addAll(listOf(pythonPath!!.absolutePath, spotdlPath!!.absolutePath))
        command.addAll(args)

        val processBuilder = ProcessBuilder(command)
        val env = processBuilder.environment()
        env["LD_LIBRARY_PATH"] = ENV_LD_LIBRARY_PATH!!
        env["SSL_CERT_FILE"] = ENV_SSL_CERT_FILE!!
        env["PATH"] = System.getenv("PATH")!! + ":" + binDir!!.absolutePath + ":" + ffmpegPath!!.absolutePath
        env["PYTHONHOME"] = ENV_PYTHONHOME!!
        env["HOME"] = HOME!!

        //Testing ffmpeg
        //env["ffmpeg"] = ffmpegPath!!.absolutePath
        //env["global_ffmpeg"] = ffmpegPath!!.absolutePath

        try {
            process = processBuilder.start()
            Log.d("SpotDL", "Process started. Process: $process")
        } catch (e: IOException) {
            throw SpotDLException("Error starting process", e)
        }

        if (processId != null) {
            id2Process[processId] = process
        }

        val outStream: InputStream = process.inputStream
        Log.d("SpotDL", "Out stream: $outStream")
        val errStream: InputStream = process.errorStream
        Log.d("SpotDL", "Err stream: $errStream")

        val stdOutProcessor = StreamProcessExtractor(
            outBuffer, outStream,
            callback
        )

        val stdErrProcessor = StreamProcessExtractor(errBuffer, errStream)

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

        //Delete ANSI (cleaner output)
        val outClean = out.replace("(?:\\x1B[@-Z\\\\-_]|[\\x80-\\x9A\\x9C-\\x9F]|(?:\\x1B\\[|\\x9B)[0-?]*[ -/]*[@-~])".toRegex(), "")

        val err = errBuffer.toString()
        //Cleaner output
        val errClean = err.replace("(?:\\x1B[@-Z\\\\-_]|[\\x80-\\x9A\\x9C-\\x9F]|(?:\\x1B\\[|\\x9B)[0-?]*[ -/]*[@-~])".toRegex(), "")

        if(exitCode > 0 && !command.contains("--print-errors")) {
            throw SpotDLException("Error executing command: $command, exit code: $exitCode, stderr: $err")
        }

        val elapsedTime = System.currentTimeMillis() - startTime

        spotDLResponse = SpotDLResponse(command, exitCode, elapsedTime, out, err)

        Log.d("SpotDL", "Stdout: $outClean")
        Log.e("SpotDL", "Stderr: $errClean")
        Log.d("SpotDL", "------------------------------------------------------------------------------")
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