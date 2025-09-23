package com.gamez24.backend.springboot_gamez24.repository

import com.gamez24.backend.springboot_gamez24.entity.GameStore
import com.gamez24.backend.springboot_gamez24.entity.UserGameClaim
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
interface UserGameClaimRepository : JpaRepository<UserGameClaim, Long> {

    // Check if user already claimed this game from this store
    fun existsByUserIdAndGameNameAndStore(userId: Long, gameName: String, store: GameStore): Boolean

    // Get all claims for a user (paginated)
    fun findByUserIdOrderByClaimedAtDesc(userId: Long, pageable: Pageable): Page<UserGameClaim>

    // Get claims by user and store
    fun findByUserIdAndStoreOrderByClaimedAtDesc(userId: Long, store: GameStore, pageable: Pageable): Page<UserGameClaim>

    // Get user's last claimed game
    fun findFirstByUserIdOrderByClaimedAtDesc(userId: Long): UserGameClaim?

    // Statistics queries (if not using separate statistics table)
    @Query("SELECT COUNT(c) FROM UserGameClaim c WHERE c.userId = :userId")
    fun countByUserId(@Param("userId") userId: Long): Long

    @Query("SELECT COUNT(c) FROM UserGameClaim c WHERE c.userId = :userId AND c.store = :store")
    fun countByUserIdAndStore(@Param("userId") userId: Long, @Param("store") store: GameStore): Long

    @Query("SELECT COALESCE(SUM(c.moneySaved), 0) FROM UserGameClaim c WHERE c.userId = :userId")
    fun sumMoneySavedByUserId(@Param("userId") userId: Long): BigDecimal

    @Query("SELECT COALESCE(SUM(c.moneySaved), 0) FROM UserGameClaim c WHERE c.userId = :userId AND c.store = :store")
    fun sumMoneySavedByUserIdAndStore(@Param("userId") userId: Long, @Param("store") store: GameStore): BigDecimal
}

