// VoteRequest.kt
package com.gamez24.backend.springboot_gamez24.dto

import com.gamez24.backend.springboot_gamez24.entity.VoteType
import jakarta.validation.constraints.NotNull

data class VoteRequest(
    @field:NotNull(message = "Vote type is required")
    val voteType: VoteType
)