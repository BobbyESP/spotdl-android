package com.bobbyesp.library.util

object Hash {
    /**
     * Hashes a string using SHA-1.
     * @param input the string to hash
     * @return the hashed string
     */
    fun sha1(input: String): String {
        val bytes = input.toByteArray()
        val md = java.security.MessageDigest.getInstance("SHA-1")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}