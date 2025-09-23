package com.gamez24.backend.springboot_gamez24.dto

// ErrorResponse.kt - For error handling

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class ErrorResponse(
    val success: Boolean = false,
    val message: String,
    @JsonProperty("error_code")
    val errorCode: String? = null,
    val timestamp: LocalDateTime = LocalDateTime.now()
)