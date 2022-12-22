package com.bobbyesp.library

import android.util.Log

open class SpotDLRequest(url: String? = null, urls: List<String>? = null) {

    companion object {
        fun getInstance(): SpotDLUpdater {
            return SpotDLUpdater()
        }
    }

    private lateinit var urls: List<String>
    private var options = SpotDLOptions()
    private var commandList = ArrayList<String>()

    constructor(url: String) : this() {
        this.urls = listOf(url)
    }

    constructor(urls: List<String>) : this() {
        this.urls = urls
    }


    open fun addOption(option: String, argument: String): SpotDLRequest {
        options.addOption(option, argument)
        return this
    }

    open fun addOption(option: String, argument: Number): SpotDLRequest {
        options.addOption(option, argument)
        return this
    }


    fun addOption(option: String): SpotDLRequest {
        options.addOption(option)
        return this
    }

    fun addCommands(commands: List<String>): SpotDLRequest {
        commandList.addAll(commands)
        return this
    }


    fun getOption(option: String): String {
        return options.getArgument(option) ?: ""
    }

    fun getArguments(option: String): List<String> {
        return options.getArguments(option) ?: listOf()
    }

    fun hasOption(option: String): Boolean {
        return options.hasOption(option)
    }

    fun buildCommand(): List<String> {
        commandList.addAll(options.buildOptions())
        commandList.addAll(urls)
        Log.d("SpotDLRequest", "Commands: $commandList")
        return commandList
    }

}