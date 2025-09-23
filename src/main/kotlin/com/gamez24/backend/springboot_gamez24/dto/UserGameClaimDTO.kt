package com.gamez24.backend.springboot_gamez24.dto

// UserGameClaimDTO.kt - For returning claimed games list

import com.gamez24.backend.springboot_gamez24.entity.GameStore
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.time.LocalDateTime

data class UserGameClaimDTO(
    val id: Long,
    @JsonProperty("game_name")
    val gameName: String,
    val store: GameStore,
    @JsonProperty("claimed_at")
    val claimedAt: LocalDateTime,
    @JsonProperty("money_saved")
    val moneySaved: BigDecimal,
    @JsonProperty("original_price")
    val originalPrice: BigDecimal,
    @JsonProperty("discounted_price")
    val discountedPrice: BigDecimal,
    @JsonProperty("game_image_url")
    val gameImageUrl: String?,
    @JsonProperty("external_game_id")
    val externalGameId: String?
)