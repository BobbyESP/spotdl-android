package com.bobbyesp.spotdl_android.utils

import android.util.Log
import java.io.File

object StorageUtil {

    //Give permissions to a directory with chmod 777
    private fun givePermsWithChmod(path: String, perms: String = "777") {
        val command = "chmod -R $perms $path"
        val process = Runtime.getRuntime().exec(command)
        process.waitFor()

    }

    //Check if the app can access to a directory
    fun canAccessDirectory(path: String): Boolean {
        Log.d("Can Access Directory", "Checking if the app can access to $path")
        val file = File(path)
        return file.exists() && file.canRead() && file.canWrite() && file.isDirectory
    }

    //can read and write file
    fun canReadAndWriteFile(path: String): Boolean {
        Log.d("Can Read and Write File", "Checking if the app can read and write to $path")
        val file = File(path)
        return file.exists() && file.canRead() && file.canWrite() && file.isFile
    }
}