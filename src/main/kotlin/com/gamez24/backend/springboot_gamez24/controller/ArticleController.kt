package com.gamez24.backend.springboot_gamez24.controller

import com.gamez24.backend.springboot_gamez24.dto.*
import com.gamez24.backend.springboot_gamez24.entity.User
import com.gamez24.backend.springboot_gamez24.service.ArticleService
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/v1/articles")
class ArticleController(
    private val articleService: ArticleService
) {

    private val logger = LoggerFactory.getLogger(ArticleController::class.java)

    @PostMapping
    fun createArticle(@Valid @RequestBody request: CreateArticleRequest): ResponseEntity<ArticleResponse> {
        val userId = getCurrentUserId()
        logger.info("📝 Create article request from user: $userId")

        return try {
            val article = articleService.createArticle(userId, request)
            val response = ArticleResponse(
                success = true,
                message = "Article created successfully",
                article = article
            )
            ResponseEntity.status(HttpStatus.CREATED).body(response)
        } catch (e: RuntimeException) {
            logger.error("❌ Failed to create article: ${e.message}")
            when (e.message) {
                "Article with this slug already exists" -> throw ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Article with this slug already exists"
                )
                else -> throw ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    e.message ?: "Failed to create article"
                )
            }
        }
    }

    @GetMapping
    fun getArticles(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") limit: Int,
        @RequestParam(required = false) search: String?
    ): ResponseEntity<ArticleListResponse> {
        val userId = getCurrentUserIdOrNull() // Optional authentication for public access
        logger.debug("📋 Get articles request (page: $page, limit: $limit, search: $search)")

        return try {
            val response = if (search.isNullOrBlank()) {
                articleService.getArticles(page, limit, userId)
            } else {
                articleService.searchArticles(search, page, limit, userId)
            }
            ResponseEntity.ok(response)
        } catch (e: RuntimeException) {
            logger.error("❌ Failed to get articles: ${e.message}")
            throw ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Failed to retrieve articles"
            )
        }
    }

    @GetMapping("/{slug}")
    fun getArticleBySlug(@PathVariable slug: String): ResponseEntity<ArticleResponse> {
        val userId = getCurrentUserIdOrNull()
        logger.debug("🔍 Get article by slug: $slug")

        return try {
            val article = articleService.getArticleBySlug(slug, userId)
            val response = ArticleResponse(
                success = true,
                message = "Article retrieved successfully",
                article = article
            )
            ResponseEntity.ok(response)
        } catch (e: RuntimeException) {
            logger.error("❌ Failed to get article: ${e.message}")
            when (e.message) {
                "Article not found" -> throw ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Article not found"
                )
                else -> throw ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to retrieve article"
                )
            }
        }
    }

    @PostMapping("/{slug}/vote")
    fun voteOnArticle(
        @PathVariable slug: String,
        @Valid @RequestBody request: VoteRequest
    ): ResponseEntity<VoteResponse> {
        val userId = getCurrentUserId()
        logger.info("🗳️ Vote request from user: $userId on article: $slug (${request.voteType})")

        return try {
            val response = articleService.voteOnArticle(userId, slug, request.voteType)
            ResponseEntity.ok(response)
        } catch (e: RuntimeException) {
            logger.error("❌ Failed to vote on article: ${e.message}")
            when (e.message) {
                "Article not found" -> throw ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Article not found"
                )
                else -> throw ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    e.message ?: "Failed to vote on article"
                )
            }
        }
    }

    @GetMapping("/me")
    fun getUserArticles(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") limit: Int
    ): ResponseEntity<ArticleListResponse> {
        val userId = getCurrentUserId()
        logger.debug("👤 Get user articles request for user: $userId")

        return try {
            val response = articleService.getUserArticles(userId, page, limit)
            ResponseEntity.ok(response)
        } catch (e: RuntimeException) {
            logger.error("❌ Failed to get user articles: ${e.message}")
            throw ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Failed to retrieve user articles"
            )
        }
    }

    // Helper method to get current user ID (required authentication)
    private fun getCurrentUserId(): Long {
        val authentication: Authentication = SecurityContextHolder.getContext().authentication
        val userDetails = authentication.principal as org.springframework.security.core.userdetails.UserDetails

        return when (userDetails) {
            is User -> userDetails.id
            else -> throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user")
        }
    }

    // Helper method to get current user ID (optional authentication)
    private fun getCurrentUserIdOrNull(): Long? {
        return try {
            val authentication: Authentication? = SecurityContextHolder.getContext().authentication
            if (authentication == null || !authentication.isAuthenticated || authentication.principal == "anonymousUser") {
                null
            } else {
                val userDetails = authentication.principal as? org.springframework.security.core.userdetails.UserDetails
                when (userDetails) {
                    is User -> userDetails.id
                    else -> null
                }
            }
        } catch (e: Exception) {
            logger.debug("No authenticated user found, returning null")
            null
        }
    }
}