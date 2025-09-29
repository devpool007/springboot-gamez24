// ArticleListDTO.kt - Simplified for list views
package com.gamez24.backend.springboot_gamez24.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class ArticleListDTO(
    val id: Long,
    val title: String,
    val slug: String,
    @JsonProperty("author_id")
    val authorId: Long,
    @JsonProperty("author_username")
    val authorUsername: String?,
    @JsonProperty("created_at")
    val createdAt: LocalDateTime,
    @JsonProperty("upvote_count")
    val upvoteCount: Long = 0,
    @JsonProperty("downvote_count")
    val downvoteCount: Long = 0,
    @JsonProperty("net_score")
    val netScore: Long = 0,
    @JsonProperty("user_vote")
    val userVote: String? = null,
    @JsonProperty("content_preview")
    val contentPreview: String // First 150 characters of content
)