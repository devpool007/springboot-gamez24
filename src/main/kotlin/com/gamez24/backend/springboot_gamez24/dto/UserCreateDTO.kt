package com.gamez24.backend.springboot_gamez24.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UserCreateDTO (
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email should be valid")
    val email: String,

    @field:NotBlank(message = "Username is required")
    @field:Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    val username: String, // ← Added username field

    @field:NotBlank(message = "Password is required")
    @field:Size(min = 6, message = "Password must be at least 6 characters")
    val password: String
)