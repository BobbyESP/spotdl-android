package com.bobbyesp.library

open class SpotDLResponse(commands: List<String>, exitCode: Int, elapsedTime: Long, output: String, error: String) {
    private var command: List<String>? = null
    private var exitCode = 0
    private var out: String? = null
    private var err: String? = null
    private var elapsedTime: Long = 0

    init {
        this.command = commands
        this.exitCode = exitCode
        this.out = output
        this.err = error
        this.elapsedTime = elapsedTime
    }

    fun getCommand(): List<String>? {
        return command
    }

    fun getExitCode(): Int {
        return exitCode
    }

    fun getOutput(): String? {
        return out
    }

    fun getErr(): String? {
        return err
    }

    fun getElapsedTime(): Long {
        return elapsedTime
    }
}