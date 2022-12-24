package com.bobbyesp.spotdl_android.utils

import com.tencent.mmkv.MMKV

object PreferencesUtil {
    private val kv = MMKV.defaultMMKV()

    fun updateValue(key: String, b: Boolean) = kv.encode(key, b)
    fun updateInt(key: String, int: Int) = kv.encode(key, int)
    fun getInt(key: String, int: Int) = kv.decodeInt(key, int)
    fun getValue(key: String): Boolean = kv.decodeBool(key, false)
    fun getValue(key: String, b: Boolean): Boolean = kv.decodeBool(key, b)
    fun getString(key: String): String? = kv.decodeString(key)

    fun getString(key: String, default: String): String = kv.decodeString(key, default).toString()
    fun updateString(key: String, string: String) = kv.encode(key, string)

    fun containsKey(key: String) = kv.containsKey(key)


    const val VIDEO_DIRECTORY = "download_dir"
    const val AUDIO_DIRECTORY = "audio_dir"

}