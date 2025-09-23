package com.gamez24.backend.springboot_gamez24.dto

// UserGameStatsDTO.kt - Complete user statistics

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.time.LocalDateTime
import com.gamez24.backend.springboot_gamez24.entity.GameStore

data class UserGameStatsDTO(
    @JsonProperty("user_id")
    val userId: Long,
    @JsonProperty("total_games_claimed")
    val totalGamesClaimed: Int,
    @JsonProperty("store_breakdown")
    val storeBreakdown: StoreStatsDTO,
    @JsonProperty("total_money_saved")
    val totalMoneySaved: BigDecimal,
    @JsonProperty("last_claimed_game")
    val lastClaimedGame: LastClaimedGameDTO?,
    @JsonProperty("last_updated")
    val lastUpdated: LocalDateTime
)

data class StoreStatsDTO(
    val steam: StoreDetailDTO,
    @JsonProperty("epic_games")
    val epicGames: StoreDetailDTO,
    val gog: StoreDetailDTO
)

data class StoreDetailDTO(
    val games: Int,
    @JsonProperty("money_saved")
    val moneySaved: BigDecimal
)

data class LastClaimedGameDTO(
    val name: String,
    val store: GameStore,
    @JsonProperty("claimed_at")
    val claimedAt: LocalDateTime,
    @JsonProperty("money_saved")
    val moneySaved: BigDecimal
)