package com.bobbyesp.spotdl_common.utils.files

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream

object FilesUtil {
    /**
     * Asserts that a directory exists and creates it if it doesn't
     * @param file the folder (File in Java) to check if it exists or create in case it doesn't exist
     */
    fun createDirectoryIfNotExists(file: File) {
        if(!file.exists()) {
            Log.i("FilesUtil", "Creating folder: ${file.absolutePath}")
            file.mkdir()
        }
    }

    fun File.ensure(): File {
        if (!exists()) {
            mkdir()
        }
        return this
    }

    fun copyRawResourceToFile(context: Context, resourceId: Int, file: File) {
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
}