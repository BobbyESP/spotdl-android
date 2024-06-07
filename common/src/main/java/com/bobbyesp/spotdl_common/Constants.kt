package com.bobbyesp.spotdl_common

object Constants {
    const val LIBRARY_NAME = "spotdl-android"
    const val PACKAGES_ROOT_NAME = "packages"

    object BinariesName {
        const val PYTHON = "libpython.so"
        const val FFMPEG = "libffmpeg.so"
        const val SPOTDL = "spotdl"
    }

    object LibrariesName {
        const val PYTHON = "libpython.zip.so"
        const val FFMPEG = "libffmpeg.zip.so"

        object TemporalFilesName {
            const val TEMPORAL_PYTHON = "temp_libpython.zip.so"
            const val TEMPORAL_FFMPEG = "temp_libffmpeg.zip.so"
        }
    }

    object DirectoriesName {
        const val PYTHON = "python"
        const val FFMPEG = "ffmpeg"

        const val SPOTDL = "spotdl"
    }
}