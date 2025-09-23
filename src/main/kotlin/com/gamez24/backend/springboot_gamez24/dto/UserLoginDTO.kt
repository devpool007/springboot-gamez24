package com.gamez24.backend.springboot_gamez24.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class UserLoginDTO(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email should be valid")
    val email: String,

    @field:NotBlank(message = "Password is required")
    val password: String
)
