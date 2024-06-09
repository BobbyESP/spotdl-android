package com.bobbyesp.library

import android.content.Context
import com.bobbyesp.spotdl_common.domain.Dependency
import com.bobbyesp.spotdl_common.domain.model.DownloadedDependencies
import com.bobbyesp.spotdl_common.utils.dependencies.DependenciesUtil
import com.bobbyesp.spotdl_common.utils.dependencies.dependencyDownloadCallback
import java.io.File

object SpotDL: SpotDLCore() {
    override fun ensureDependencies(
        appContext: Context,
        skipDependencies: List<Dependency>,
        callback: dependencyDownloadCallback?
    ): DownloadedDependencies = DependenciesUtil.ensureDependencies(appContext, skipDependencies, callback)

    override fun initPython(appContext: Context, pythonDir: File) {
        TODO("Not yet implemented")
    }
}