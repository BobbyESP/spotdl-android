package com.bobbyesp.spotdl_common.domain.model

import com.bobbyesp.spotdl_common.domain.Dependency

data class DownloadedDependencies(
    val python: Boolean = false,
    val ffmpeg: Boolean = false
)
fun DownloadedDependencies.getMissingDependencies(): List<Dependency> {
    val missingDependencies = mutableListOf<Dependency>()
    if (!python) missingDependencies.add(Dependency.PYTHON)
    if (!ffmpeg) missingDependencies.add(Dependency.FFMPEG)
    return missingDependencies
}
