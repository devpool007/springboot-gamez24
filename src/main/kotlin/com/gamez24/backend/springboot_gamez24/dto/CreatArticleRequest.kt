// CreateArticleRequest.kt
package com.gamez24.backend.springboot_gamez24.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateArticleRequest(
    @field:NotBlank(message = "Title is required")
    @field:Size(max = 255, message = "Title must not exceed 255 characters")
    val title: String,

    @field:NotBlank(message = "Content is required")
    @field:Size(min = 10, message = "Content must be at least 10 characters")
    val content: String,

    @field:Size(max = 255, message = "Slug must not exceed 255 characters")
    val slug: String, // Optional - will be auto-generated if not provided
)