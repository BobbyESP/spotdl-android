package com.bobbyesp.library

import android.util.Log

open class SpotDLRequest(url: String? = null, urls: List<String>? = null) {

    companion object {
        fun getInstance(): SpotDLUpdater {
            return SpotDLUpdater()
        }
    }

    private var urls: List<String>? = null
    private val options: SpotDLOptions = SpotDLOptions()

    private val commandList: List<String> = mutableListOf()

    init {
        if (url != null) {
            this.urls = listOf(url)
        } else if (urls != null) {
            this.urls = urls
        }
    }

    open fun addOption(
        option: String,
        argument: String
    ): SpotDLRequest {
        options.addOption(option, argument)
        return this
    }

    open fun addOption(
        option: String,
        argument: Number
    ): SpotDLRequest {
        options.addOption(option, argument)
        return this
    }

    open fun addOption(option: String?): SpotDLRequest {
        options.addOption(option!!)
        return this
    }

    open fun addCommands(commands: List<String?>?): SpotDLRequest {
        val collectionOfCommands: Collection<String> = commands?.filterNotNull() ?: emptyList()
        commandList.toMutableList().addAll(collectionOfCommands)
        return this
    }

    open fun getOption(option: String?): String? {
        return options.getArgument(option)
    }

    open fun getArguments(option: String?): List<String?>? {
        return options.getArguments(option!!)
    }

    open fun hasOption(option: String?): Boolean {
        return options.hasOption(option)
    }

    open fun buildCommand(): List<String?>? {
        commandList + options.buildOptions()
        val collectionOfUrls: Collection<String> = urls?.filterNotNull() ?: emptyList()
        commandList.toMutableList().addAll(collectionOfUrls)

        Log.d("SpotDLRequest", "buildCommand: $commandList")
        return commandList
    }
}