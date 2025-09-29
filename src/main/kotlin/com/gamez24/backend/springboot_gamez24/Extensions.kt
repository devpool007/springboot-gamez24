package com.gamez24.backend.springboot_gamez24

import java.util.*

fun String.toSlug(): String = lowercase(Locale.getDefault())
    .replace(oldValue = "\n", newValue = " ")
    .replace("[^a-z\\d\\s]".toRegex(), " ")
    .split(" ")
    .joinToString("-")
    .replace("-+".toRegex(), "-")

fun String.toUniqueSlug(): String {
    val slug = this.trim()
        .replace(Regex("[^a-zA-Z0-9\\s]"), "")
        .split(Regex("\\s+"))
        .joinToString("-")
        .lowercase()

    // Append a UUID to ensure uniqueness
    return "$slug-${UUID.randomUUID()}"
}