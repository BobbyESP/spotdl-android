package com.bobbyesp.spotdl_common.domain

import com.bobbyesp.spotdl_common.Constants

enum class Dependency {
    PYTHON,
    FFMPEG;

    override fun toString(): String {
        return when (this) {
            PYTHON -> "python"
            FFMPEG -> "ffmpeg"
        }
    }

    fun fromString(string: String): Dependency {
        return when (string) {
            "python" -> PYTHON
            "ffmpeg" -> FFMPEG
            else -> throw IllegalArgumentException("Unknown plugin: $string")
        }
    }
    companion object {
        fun Dependency.toLibraryName(): String {
            return when (this) {
                PYTHON -> Constants.LibrariesName.PYTHON
                FFMPEG -> Constants.LibrariesName.FFMPEG
            }
        }

        fun Dependency.toDirectoryName(): String {
            return when (this) {
                PYTHON -> Constants.DirectoriesName.PYTHON
                FFMPEG -> Constants.DirectoriesName.FFMPEG
            }
        }
    }
}