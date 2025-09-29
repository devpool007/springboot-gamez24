// VoteResponse.kt
package com.gamez24.backend.springboot_gamez24.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class VoteResponse(
    val success: Boolean,
    val message: String,
    @JsonProperty("vote_type")
    val voteType: String,
    @JsonProperty("upvote_count")
    val upvoteCount: Long,
    @JsonProperty("downvote_count")
    val downvoteCount: Long,
    @JsonProperty("net_score")
    val netScore: Long
)

