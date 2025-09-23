package com.gamez24.backend.springboot_gamez24.dto

// ClaimGameRequest.kt - What frontend sends to backend

import com.gamez24.backend.springboot_gamez24.entity.GameStore
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PositiveOrZero
import java.math.BigDecimal

data class ClaimGameRequest(
    @field:NotBlank(message = "Game name is required")
    @JsonProperty("game_name")
    val gameName: String,

    val store: GameStore,

    @field:PositiveOrZero(message = "Original price must be positive or zero")
    @JsonProperty("original_price")
    val originalPrice: BigDecimal,

    @field:PositiveOrZero(message = "Discounted price must be positive or zero")
    @JsonProperty("discounted_price")
    val discountedPrice: BigDecimal = BigDecimal.ZERO,

    @JsonProperty("game_image_url")
    val gameImageUrl: String? = null,

    @JsonProperty("external_game_id")
    val externalGameId: String? = null
)