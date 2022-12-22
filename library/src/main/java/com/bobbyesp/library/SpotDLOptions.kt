package com.bobbyesp.library

import android.util.Log

open class SpotDLOptions {

    private var options: Map<String, List<String>> = LinkedHashMap()

    open fun addOption(
        option: String,
        argument: String
    ): SpotDLOptions {
        if (!options.containsKey(option)) {
            val arguments: MutableList<String> = mutableListOf()
            arguments.add(argument)
            options.toMutableMap()[option] = arguments
        } else {
           options[option]!!.toMutableList().apply { add(argument) }
        }
        return this
    }

    open fun addOption(
        option: String,
        argument: Number
    ): SpotDLOptions {
        if (!options.containsKey(option)) {
            val arguments: MutableList<String> = ArrayList()
            arguments.add(argument.toString())
            options.toMutableMap()[option] = arguments
        } else {
            options[option]!!.toMutableList().apply { add(argument.toString()) }
        }
        return this
    }

    open fun addOption(option: String): SpotDLOptions {
        if (!options.containsKey(option)) {
            val arguments: MutableList<String> = java.util.ArrayList()
            arguments.add("")
            options.toMutableMap()[option] = arguments  //options.put(option, arguments)
        } else {
            options[option]!!.toMutableList().apply { add("") }
        }
        return this
    }

    open fun getArgument(option: String?): String? {
        if (!options.containsKey(option)) return null
        val argument = options[option]!![0]
        return if (argument.isEmpty()) null else argument
    }

    open fun getArguments(option: String): List<String?>? {
        return if (!options.containsKey(option)) null else options[option]
    }

    open fun hasOption(option: String?): Boolean {
        return options.containsKey(option)
    }

    open fun buildOptions(): List<String> {
        val commandList = mutableListOf<String>()
        for ((option, arguments) in options.entries) {
            commandList.add(option)
            for (argument in arguments) {
                if (argument.isNotEmpty()) {
                    commandList.add(argument)
                }
            }
        }
        return commandList
    }

    /*open fun buildOptions() : List<String>{
        var commandList: List<String> = mutableListOf()
        for (entry: Map.Entry<String, List<String>>)
    }*/

}