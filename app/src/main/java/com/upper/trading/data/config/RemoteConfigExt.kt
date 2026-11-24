package com.upper.trading.data.config

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.serialization.json.Json

fun String.unquote(): String {
    var result = trim()
    while ((result.startsWith("\"") && result.endsWith("\"")) ||
        (result.startsWith("'") && result.endsWith("'"))
    ) {
        if (result.length < 2) break
        result = result.substring(1, result.length - 1).trim()
    }
    return result
}

fun String.toStringList(): List<String> {
    val trimmed = unquote()
    if (trimmed.isBlank()) return emptyList()

    return runCatching {
        if (trimmed.startsWith("[")) {
            Json.decodeFromString<List<String>>(trimmed).map { it.unquote() }
        } else {
            trimmed.split(",")
                .map { it.unquote() }
                .filter { it.isNotEmpty() }
        }
    }.getOrElse { emptyList() }
}

fun FirebaseRemoteConfig.getStringList(key: String): List<String> {
    return getString(key).toStringList()
}

fun FirebaseRemoteConfig.getTrimmedString(key: String): String {
    return getString(key).unquote()
}
