package com.bobbyesp.spotdl_common.utils

import kotlinx.serialization.json.Json

val json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
    coerceInputValues = true
}