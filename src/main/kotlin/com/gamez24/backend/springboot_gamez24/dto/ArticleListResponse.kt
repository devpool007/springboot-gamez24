// ArticleListResponse.kt
package com.gamez24.backend.springboot_gamez24.dto

data class ArticleListResponse(
    val articles: List<ArticleListDTO>,
    val pagination: PaginationArticleDTO
)