// VoteRepository.kt - Minimal and Clean
package com.gamez24.backend.springboot_gamez24.repository

import com.gamez24.backend.springboot_gamez24.entity.Vote
import com.gamez24.backend.springboot_gamez24.entity.VoteType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface VoteRepository : JpaRepository<Vote, Long> {

    // Basic queries using Spring Data JPA method names (no custom @Query needed)
    fun existsByUserIdAndArticleId(userId: Long, articleId: Long): Boolean
    fun findByUserIdAndArticleId(userId: Long, articleId: Long): Vote?
    fun countByArticleIdAndVoteType(articleId: Long, voteType: VoteType): Long
    fun findByUserIdAndArticleIdIn(userId: Long, articleIds: List<Long>): List<Vote>
    fun deleteByUserIdAndArticleId(userId: Long, articleId: Long)
}