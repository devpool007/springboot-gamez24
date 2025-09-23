package com.gamez24.backend.springboot_gamez24.dto

// ClaimGameResponse.kt

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class ClaimGameResponse(
    val success: Boolean,
    val message: String,
    @JsonProperty("claim_id")
    val claimId: Long,
    @JsonProperty("money_saved")
    val moneySaved: BigDecimal,
    @JsonProperty("updated_stats")
    val updatedStats: UserStatsUpdateDTO
)

data class UserStatsUpdateDTO(
    @JsonProperty("total_games")
    val totalGames: Int,
    @JsonProperty("steam_games")
    val steamGames: Int,
    @JsonProperty("epic_games")
    val epicGames: Int,
    @JsonProperty("gog_games")
    val gogGames: Int,
    @JsonProperty("total_saved")
    val totalSaved: BigDecimal
)