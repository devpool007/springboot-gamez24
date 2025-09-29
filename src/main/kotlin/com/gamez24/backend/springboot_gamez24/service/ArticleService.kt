// ArticleService.kt
package com.gamez24.backend.springboot_gamez24.service

import com.gamez24.backend.springboot_gamez24.dto.*
import com.gamez24.backend.springboot_gamez24.entity.Article
import com.gamez24.backend.springboot_gamez24.entity.Vote
import com.gamez24.backend.springboot_gamez24.entity.VoteType
import com.gamez24.backend.springboot_gamez24.repository.ArticleRepository
import com.gamez24.backend.springboot_gamez24.repository.VoteRepository
import com.gamez24.backend.springboot_gamez24.toUniqueSlug
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.text.Normalizer
import java.util.*

@Service
class ArticleService(
    private val articleRepository: ArticleRepository,
    private val voteRepository: VoteRepository,
    private val userService: UserService
) {

    private val logger = LoggerFactory.getLogger(ArticleService::class.java)

    @Transactional
    fun createArticle(userId: Long, request: CreateArticleRequest): ArticleDTO {
        logger.info("📝 Creating article: ${request.title} for user: $userId")

        // Generate slug if not provided
        val slug = request.slug?.takeIf { it.isNotBlank() } ?: generateSlug(request.title).toUniqueSlug()

        // Check if slug already exists
        if (articleRepository.existsBySlug(slug)) {
            throw RuntimeException("Article with this slug already exists")
        }

        val article = Article(
            title = request.title,
            content = request.content,
            slug = slug,
            authorId = userId
        )

        val savedArticle = articleRepository.save(article)
        logger.info("✅ Article created with ID: ${savedArticle.id}")

        return convertToArticleDTO(savedArticle, userId)
    }

    fun getArticles(page: Int = 0, limit: Int = 20, userId: Long? = null): ArticleListResponse {
        logger.debug("📋 Getting articles (page: $page, limit: $limit)")

        val pageable: Pageable = PageRequest.of(page, limit)
        val articlesPage = articleRepository.findAllByOrderByCreatedAtDesc(pageable)

        val articleIds = articlesPage.content.map { it.id }
        val userVotes = userId?.let {
            voteRepository.findByUserIdAndArticleIdIn(it, articleIds)
                .associateBy({ it.articleId }, { it.voteType.name })
        } ?: emptyMap()

        val articleDTOs = articlesPage.content.map { article ->
            convertToArticleListDTO(article, userVotes[article.id])
        }

        val pagination = PaginationArticleDTO(
            page = page,
            limit = limit,
            total = articlesPage.totalElements,
            totalPages = articlesPage.totalPages,
            hasNext = articlesPage.hasNext(),
            hasPrevious = articlesPage.hasPrevious()
        )

        return ArticleListResponse(articleDTOs, pagination)
    }

    fun getArticleBySlug(slug: String, userId: Long? = null): ArticleDTO {
        logger.debug("🔍 Getting article by slug: $slug")

        val article = articleRepository.findBySlug(slug)
            ?: throw RuntimeException("Article not found")

        return convertToArticleDTO(article, userId)
    }

    @Transactional
    fun voteOnArticle(userId: Long, slug: String, voteType: VoteType): VoteResponse {
        logger.info("🗳️ User $userId voting $voteType on article: $slug")

        val article = articleRepository.findBySlug(slug)
            ?: throw RuntimeException("Article not found")

        // Check if user has already voted
        val existingVote = voteRepository.findByUserIdAndArticleId(userId, article.id)

        when {
            existingVote == null -> {
                // Create new vote
                val vote = Vote(userId, article.id, voteType)
                voteRepository.save(vote)
                logger.info("✅ New vote created: $voteType")
            }
            existingVote.voteType == voteType -> {
                // User is trying to vote the same way - remove vote (toggle off)
                voteRepository.delete(existingVote)
                logger.info("🔄 Vote removed (toggled off)")
            }
            else -> {
                // User is changing their vote
                existingVote.voteType = voteType
                voteRepository.save(existingVote)
                logger.info("🔄 Vote changed to: $voteType")
            }
        }

        // Get updated vote counts
        val upvoteCount = voteRepository.countByArticleIdAndVoteType(article.id, VoteType.UP)
        val downvoteCount = voteRepository.countByArticleIdAndVoteType(article.id, VoteType.DOWN)
        val netScore = upvoteCount - downvoteCount

        // Get user's current vote status
        val currentVote = voteRepository.findByUserIdAndArticleId(userId, article.id)

        return VoteResponse(
            success = true,
            message = "Vote recorded successfully",
            voteType = currentVote?.voteType?.name ?: "NONE",
            upvoteCount = upvoteCount,
            downvoteCount = downvoteCount,
            netScore = netScore
        )
    }

    fun getUserArticles(userId: Long, page: Int = 0, limit: Int = 20): ArticleListResponse {
        logger.debug("👤 Getting articles for user: $userId")

        val pageable: Pageable = PageRequest.of(page, limit)
        val articlesPage = articleRepository.findByAuthorIdOrderByCreatedAtDesc(userId, pageable)

        val articleDTOs = articlesPage.content.map { article ->
            convertToArticleListDTO(article, null) // User's own articles, no need for vote status
        }

        val pagination = PaginationArticleDTO(
            page = page,
            limit = limit,
            total = articlesPage.totalElements,
            totalPages = articlesPage.totalPages,
            hasNext = articlesPage.hasNext(),
            hasPrevious = articlesPage.hasPrevious()
        )

        return ArticleListResponse(articleDTOs, pagination)
    }

    @Transactional(readOnly = true)
    fun searchArticles(query: String, page: Int = 0, limit: Int = 20, userId: Long? = null): ArticleListResponse {
        logger.debug("🔍 Searching articles with query: $query")

        val pageable: Pageable = PageRequest.of(page, limit)
        val articlesPage = articleRepository.searchArticles(query, pageable)

        val articleIds = articlesPage.content.map { it.id }
        val userVotes = userId?.let {
            voteRepository.findByUserIdAndArticleIdIn(it, articleIds)
                .associateBy({ it.articleId }, { it.voteType.name })
        } ?: emptyMap()

        val articleDTOs = articlesPage.content.map { article ->
            convertToArticleListDTO(article, userVotes[article.id])
        }

        val pagination = PaginationArticleDTO(
            page = page,
            limit = limit,
            total = articlesPage.totalElements,
            totalPages = articlesPage.totalPages,
            hasNext = articlesPage.hasNext(),
            hasPrevious = articlesPage.hasPrevious()
        )

        return ArticleListResponse(articleDTOs, pagination)
    }

    private fun convertToArticleDTO(article: Article, userId: Long? = null): ArticleDTO {
        val upvoteCount = voteRepository.countByArticleIdAndVoteType(article.id, VoteType.UP)
        val downvoteCount = voteRepository.countByArticleIdAndVoteType(article.id, VoteType.DOWN)
        val userVote = userId?.let {
            voteRepository.findByUserIdAndArticleId(it, article.id)?.voteType?.name
        }

        // Get author username
        val authorUsername = try {
            userService.getUserById(article.authorId).username
        } catch (e: Exception) {
            null
        }

        return ArticleDTO(
            id = article.id,
            title = article.title,
            content = article.content,
            slug = article.slug,
            authorId = article.authorId,
            authorUsername = authorUsername,
            createdAt = article.createdAt,
            updatedAt = article.updatedAt,
            upvoteCount = upvoteCount,
            downvoteCount = downvoteCount,
            netScore = upvoteCount - downvoteCount,
            userVote = userVote
        )
    }

    private fun convertToArticleListDTO(article: Article, userVote: String? = null): ArticleListDTO {
        val upvoteCount = voteRepository.countByArticleIdAndVoteType(article.id, VoteType.UP)
        val downvoteCount = voteRepository.countByArticleIdAndVoteType(article.id, VoteType.DOWN)

        // Get author username
        val authorUsername = try {
            userService.getUserById(article.authorId).username
        } catch (e: Exception) {
            null
        }

        // Create content preview (first 150 characters)
        val contentPreview = if (article.content.length > 150) {
            article.content.substring(0, 147) + "..."
        } else {
            article.content
        }

        return ArticleListDTO(
            id = article.id,
            title = article.title,
            slug = article.slug,
            authorId = article.authorId,
            authorUsername = authorUsername,
            createdAt = article.createdAt,
            upvoteCount = upvoteCount,
            downvoteCount = downvoteCount,
            netScore = upvoteCount - downvoteCount,
            userVote = userVote,
            contentPreview = contentPreview
        )
    }

    private fun generateSlug(title: String): String {
        return Normalizer.normalize(title, Normalizer.Form.NFD)
            .replace(Regex("[\\p{InCombiningDiacriticalMarks}]"), "")
            .lowercase(Locale.getDefault())
            .replace(Regex("[^a-z0-9\\s-]"), "")
            .replace(Regex("\\s+"), "-")
            .replace(Regex("-+"), "-")
            .trim('-')
            .take(50)
            .let { baseSlug ->
                var uniqueSlug = baseSlug
                var counter = 1
                while (articleRepository.existsBySlug(uniqueSlug)) {
                    uniqueSlug = "$baseSlug-$counter"
                    counter++
                }
                uniqueSlug
            }
    }
}