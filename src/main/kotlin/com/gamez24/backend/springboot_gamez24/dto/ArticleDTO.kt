// ArticleDTO.kt
package com.gamez24.backend.springboot_gamez24.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class ArticleDTO(
    val id: Long,
    val title: String,
    val content: String,
    val slug: String,
    @JsonProperty("author_id")
    val authorId: Long,
    @JsonProperty("author_username")
    val authorUsername: String?,
    @JsonProperty("created_at")
    val createdAt: LocalDateTime,
    @JsonProperty("updated_at")
    val updatedAt: LocalDateTime,
    @JsonProperty("upvote_count")
    val upvoteCount: Long = 0,
    @JsonProperty("downvote_count")
    val downvoteCount: Long = 0,
    @JsonProperty("net_score")
    val netScore: Long = 0,
    @JsonProperty("user_vote")
    val userVote: String? = null // "UP", "DOWN", or null if user hasn't voted
)