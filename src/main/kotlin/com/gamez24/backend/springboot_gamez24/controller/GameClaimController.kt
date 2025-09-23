package com.gamez24.backend.springboot_gamez24.controller

import com.gamez24.backend.springboot_gamez24.dto.ClaimGameRequest
import com.gamez24.backend.springboot_gamez24.dto.ClaimGameResponse
import com.gamez24.backend.springboot_gamez24.dto.UserClaimedGamesResponse
import com.gamez24.backend.springboot_gamez24.dto.UserGameStatsDTO
import com.gamez24.backend.springboot_gamez24.entity.GameStore
import com.gamez24.backend.springboot_gamez24.service.GameClaimService
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/users/me")
class GameClaimController(
    private val gameClaimService: GameClaimService
) {

    private val logger = LoggerFactory.getLogger(GameClaimController::class.java)

    @PostMapping("/claims")
    fun claimGame(@Valid @RequestBody request: ClaimGameRequest): ResponseEntity<ClaimGameResponse> {
        val userId = getCurrentUserId()
        logger.info("🎮 Claim request for user $userId: ${request.gameName}")

        return try {
            val response = gameClaimService.claimGame(userId, request)
            ResponseEntity.ok(response)
        } catch (e: RuntimeException) {
            logger.error("❌ Claim failed: ${e.message}")
            when (e.message) {
                "Game already claimed from this store" -> throw ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "You have already claimed this game from ${request.store}"
                )
                else -> throw ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    e.message ?: "Failed to claim game"
                )
            }
        }
    }

    @GetMapping("/games")
    fun getUserClaimedGames(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") limit: Int,
        @RequestParam(required = false) store: GameStore?
    ): ResponseEntity<UserClaimedGamesResponse> {
        val userId = getCurrentUserId()
        logger.debug("📋 Getting claimed games for user $userId (page: $page, store: $store)")

        val response = gameClaimService.getUserClaimedGames(userId, page, limit, store)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/claims/stats")
    fun getUserStats(): ResponseEntity<UserGameStatsDTO> {
        val userId = getCurrentUserId()
        logger.debug("📊 Getting stats for user $userId")

        val stats = gameClaimService.getUserStatistics(userId)
        return ResponseEntity.ok(stats)
    }

    private fun getCurrentUserId(): Long {
        val authentication: Authentication = SecurityContextHolder.getContext().authentication
        val userDetails = authentication.principal as org.springframework.security.core.userdetails.UserDetails

        // Assuming your User entity implements UserDetails and has getId() method
        // You might need to adjust this based on your User entity implementation
        return when (userDetails) {
            is com.gamez24.backend.springboot_gamez24.entity.User -> userDetails.id
            else -> throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user")
        }
    }
}