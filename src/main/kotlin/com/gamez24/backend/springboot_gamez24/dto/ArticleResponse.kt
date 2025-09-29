// ArticleResponse.kt
package com.gamez24.backend.springboot_gamez24.dto

data class ArticleResponse(
    val success: Boolean,
    val message: String,
    val article: ArticleDTO
)

