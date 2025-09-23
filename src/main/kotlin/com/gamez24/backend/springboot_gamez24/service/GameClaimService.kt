package com.gamez24.backend.springboot_gamez24.service

// GameClaimService.kt

import com.gamez24.backend.springboot_gamez24.dto.ClaimGameRequest
import com.gamez24.backend.springboot_gamez24.dto.ClaimGameResponse
import com.gamez24.backend.springboot_gamez24.dto.LastClaimedGameDTO
import com.gamez24.backend.springboot_gamez24.dto.PaginationDTO
import com.gamez24.backend.springboot_gamez24.dto.StoreDetailDTO
import com.gamez24.backend.springboot_gamez24.dto.StoreStatsDTO
import com.gamez24.backend.springboot_gamez24.dto.UserClaimedGamesResponse
import com.gamez24.backend.springboot_gamez24.dto.UserGameClaimDTO
import com.gamez24.backend.springboot_gamez24.dto.UserGameStatsDTO
import com.gamez24.backend.springboot_gamez24.dto.UserGameStatsSummaryDTO
import com.gamez24.backend.springboot_gamez24.dto.UserStatsUpdateDTO
import com.gamez24.backend.springboot_gamez24.entity.GameStore
import com.gamez24.backend.springboot_gamez24.entity.UserGameClaim
import com.gamez24.backend.springboot_gamez24.entity.UserGameStatistics
import com.gamez24.backend.springboot_gamez24.repository.UserGameClaimRepository
import com.gamez24.backend.springboot_gamez24.repository.UserGameStatisticsRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GameClaimService(
    private val userGameClaimRepository: UserGameClaimRepository,
    private val userGameStatisticsRepository: UserGameStatisticsRepository
) {

    private val logger = LoggerFactory.getLogger(GameClaimService::class.java)

    @Transactional
    fun claimGame(userId: Long, request: ClaimGameRequest): ClaimGameResponse {
        logger.info("🎮 User $userId attempting to claim game: ${request.gameName} from ${request.store}")

        // Check if already claimed
        if (userGameClaimRepository.existsByUserIdAndGameNameAndStore(userId, request.gameName, request.store)) {
            throw RuntimeException("Game already claimed from this store")
        }

        // Create the claim
        val claim = UserGameClaim(
            userId = userId,
            gameName = request.gameName,
            store = request.store,
            originalPrice = request.originalPrice,
            discountedPrice = request.discountedPrice,
            gameImageUrl = request.gameImageUrl,
            externalGameId = request.externalGameId
        )

        val savedClaim = userGameClaimRepository.save(claim)
        logger.info("✅ Game claim saved with ID: ${savedClaim.id}")

        // Update or create user statistics
        updateUserStatistics(userId, request.store, claim.moneySaved)

        // Get updated stats for response
        val updatedStats = getUserStats(userId)

        logger.info("🎉 Game claimed successfully! Money saved: ${claim.moneySaved}")

        return ClaimGameResponse(
            success = true,
            message = "Game claimed successfully!",
            claimId = savedClaim.id,
            moneySaved = claim.moneySaved,
            updatedStats = UserStatsUpdateDTO(
                totalGames = updatedStats.totalGamesClaimed,
                steamGames = updatedStats.steamGamesClaimed,
                epicGames = updatedStats.epicGamesClaimed,
                gogGames = updatedStats.gogGamesClaimed,
                totalSaved = updatedStats.totalMoneySaved
            )
        )
    }

    @Transactional
    private fun updateUserStatistics(userId: Long, store: GameStore, moneySaved: java.math.BigDecimal) {
        val stats = userGameStatisticsRepository.findByUserId(userId)
            ?: UserGameStatistics(userId)

        stats.addClaim(store, moneySaved)
        userGameStatisticsRepository.save(stats)

        logger.debug("📊 Updated statistics for user $userId")
    }

    fun getUserClaimedGames(userId: Long, page: Int = 0, limit: Int = 20, store: GameStore? = null): UserClaimedGamesResponse {
        val pageable: Pageable = PageRequest.of(page, limit)

        val claimsPage = if (store != null) {
            userGameClaimRepository.findByUserIdAndStoreOrderByClaimedAtDesc(userId, store, pageable)
        } else {
            userGameClaimRepository.findByUserIdOrderByClaimedAtDesc(userId, pageable)
        }

        val claimDTOs = claimsPage.content.map { claim ->
            UserGameClaimDTO(
                id = claim.id,
                gameName = claim.gameName,
                store = claim.store,
                claimedAt = claim.claimedAt,
                moneySaved = claim.moneySaved,
                originalPrice = claim.originalPrice,
                discountedPrice = claim.discountedPrice,
                gameImageUrl = claim.gameImageUrl,
                externalGameId = claim.externalGameId
            )
        }

        // Get user stats summary
        val stats = getUserStats(userId)
        val statsSummary = UserGameStatsSummaryDTO(
            totalGames = stats.totalGamesClaimed,
            totalSaved = stats.totalMoneySaved,
            storeCounts = mapOf(
                "steam" to stats.steamGamesClaimed,
                "epic_games" to stats.epicGamesClaimed,
                "gog" to stats.gogGamesClaimed
            )
        )

        val pagination = PaginationDTO(
            page = page,
            limit = limit,
            total = claimsPage.totalElements,
            hasNext = claimsPage.hasNext(),
            hasPrevious = claimsPage.hasPrevious()
        )

        return UserClaimedGamesResponse(claimDTOs, statsSummary, pagination)
    }

    fun getUserStatistics(userId: Long): UserGameStatsDTO {
        val stats = getUserStats(userId)
        val lastClaimedGame = userGameClaimRepository.findFirstByUserIdOrderByClaimedAtDesc(userId)

        return UserGameStatsDTO(
            userId = userId,
            totalGamesClaimed = stats.totalGamesClaimed,
            storeBreakdown = StoreStatsDTO(
                steam = StoreDetailDTO(stats.steamGamesClaimed, stats.steamMoneySaved),
                epicGames = StoreDetailDTO(stats.epicGamesClaimed, stats.epicMoneySaved),
                gog = StoreDetailDTO(stats.gogGamesClaimed, stats.gogMoneySaved)
            ),
            totalMoneySaved = stats.totalMoneySaved,
            lastClaimedGame = lastClaimedGame?.let {
                LastClaimedGameDTO(
                    name = it.gameName,
                    store = it.store,
                    claimedAt = it.claimedAt,
                    moneySaved = it.moneySaved
                )
            },
            lastUpdated = stats.lastUpdated
        )
    }

    private fun getUserStats(userId: Long): UserGameStatistics {
        return userGameStatisticsRepository.findByUserId(userId)
            ?: UserGameStatistics(userId)
    }
}