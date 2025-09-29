package com.gamez24.backend.springboot_gamez24.dto

// UserClaimedGamesResponse.kt - Paginated response
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class UserClaimedGamesResponse(
    val games: List<UserGameClaimDTO>,
    val stats: UserGameStatsSummaryDTO,
    val pagination: PaginationDTO
)

data class UserGameStatsSummaryDTO(
    @JsonProperty("total_games")
    val totalGames: Int,
    @JsonProperty("total_saved")
    val totalSaved: BigDecimal,
    @JsonProperty("store_counts")
    val storeCounts: Map<String, Int>
)

data class PaginationDTO(
    val page: Int,
    val limit: Int,
    val total: Long,
    @JsonProperty("has_next")
    val hasNext: Boolean,
    @JsonProperty("has_previous")
    val hasPrevious: Boolean
)

data class PaginationArticleDTO(
    val page: Int,
    val limit: Int,
    val total: Long,
    @JsonProperty("total_pages")
    val totalPages: Int,
    @JsonProperty("has_next")
    val hasNext: Boolean,
    @JsonProperty("has_previous")
    val hasPrevious: Boolean
)