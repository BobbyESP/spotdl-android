package com.bobbyesp.library

class SpotDLRequest {
    private var urls: List<String> = emptyList()
    private var options = SpotDLOptions()
    private var customCommandList: MutableList<String> = ArrayList()

    fun addOption(option: String, argument: String): SpotDLRequest {
        options.addOption(option, argument)
        return this
    }

    fun addOption(option: String, argument: Number): SpotDLRequest {
        options.addOption(option, argument)
        return this
    }

    fun addOption(option: String): SpotDLRequest {
        options.addOption(option)
        return this
    }

    fun addCommands(commands: List<String>): SpotDLRequest {
        customCommandList.addAll(commands)
        return this
    }

    fun getOption(option: String): String? {
        return options.getArgument(option)
    }

    fun getArguments(option: String): List<String?>? {
        return options.getArguments(option)
    }

    fun hasOption(option: String): Boolean {
        return options.hasOption(option)
    }

    fun buildCommand(): List<String> {
        val commandList: MutableList<String> = ArrayList()
        commandList.addAll(options.buildOptions())
        commandList.addAll(customCommandList)
        commandList.addAll(urls)
        return commandList
    }

}