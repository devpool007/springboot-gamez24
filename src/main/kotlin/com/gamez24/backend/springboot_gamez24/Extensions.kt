package com.gamez24.backend.springboot_gamez24

import java.util.*

fun String.toSlug(): String = lowercase(Locale.getDefault())
    .replace(oldValue = "\n", newValue = " ")
    .replace("[^a-z\\d\\s]".toRegex(), " ")
    .split(" ")
    .joinToString("-")
    .replace("-+".toRegex(), "-")