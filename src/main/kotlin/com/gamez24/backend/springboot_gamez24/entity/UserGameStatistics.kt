package com.gamez24.backend.springboot_gamez24.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "user_game_statistics")
class UserGameStatistics(
    @Id
    @Column(name = "user_id")
    var userId: Long = 0,

    // Game counts
    @Column(name = "total_games_claimed", nullable = false)
    var totalGamesClaimed: Int = 0,

    @Column(name = "steam_games_claimed", nullable = false)
    var steamGamesClaimed: Int = 0,

    @Column(name = "epic_games_claimed", nullable = false)
    var epicGamesClaimed: Int = 0,

    @Column(name = "gog_games_claimed", nullable = false)
    var gogGamesClaimed: Int = 0,

    // Money saved totals
    @Column(name = "total_money_saved", nullable = false, precision = 12, scale = 2)
    var totalMoneySaved: BigDecimal = BigDecimal.ZERO,

    @Column(name = "steam_money_saved", nullable = false, precision = 10, scale = 2)
    var steamMoneySaved: BigDecimal = BigDecimal.ZERO,

    @Column(name = "epic_money_saved", nullable = false, precision = 10, scale = 2)
    var epicMoneySaved: BigDecimal = BigDecimal.ZERO,

    @Column(name = "gog_money_saved", nullable = false, precision = 10, scale = 2)
    var gogMoneySaved: BigDecimal = BigDecimal.ZERO,

    @Column(name = "last_updated", nullable = false)
    var lastUpdated: LocalDateTime = LocalDateTime.now(),

    // Relationship to User
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    var user: User? = null
) {
    constructor() : this(0, 0, 0, 0, 0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, LocalDateTime.now())

    constructor(userId: Long) : this(
        userId, 0, 0, 0, 0,
        BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
        LocalDateTime.now()
    )

    // Add a game claim to statistics
    fun addClaim(store: GameStore, moneySaved: BigDecimal) {
        totalGamesClaimed++
        totalMoneySaved = totalMoneySaved.add(moneySaved)

        when (store) {
            GameStore.STEAM -> {
                steamGamesClaimed++
                steamMoneySaved = steamMoneySaved.add(moneySaved)
            }
            GameStore.EPIC_GAMES -> {
                epicGamesClaimed++
                epicMoneySaved = epicMoneySaved.add(moneySaved)
            }
            GameStore.GOG -> {
                gogGamesClaimed++
                gogMoneySaved = gogMoneySaved.add(moneySaved)
            }
        }

        lastUpdated = LocalDateTime.now()
    }

    // Remove a game claim from statistics (if unclaim feature is added)
    fun removeClaim(store: GameStore, moneySaved: BigDecimal) {
        if (totalGamesClaimed > 0) {
            totalGamesClaimed--
            totalMoneySaved = totalMoneySaved.subtract(moneySaved)

            when (store) {
                GameStore.STEAM -> {
                    if (steamGamesClaimed > 0) steamGamesClaimed--
                    steamMoneySaved = steamMoneySaved.subtract(moneySaved)
                }
                GameStore.EPIC_GAMES -> {
                    if (epicGamesClaimed > 0) epicGamesClaimed--
                    epicMoneySaved = epicMoneySaved.subtract(moneySaved)
                }
                GameStore.GOG -> {
                    if (gogGamesClaimed > 0) gogGamesClaimed--
                    gogMoneySaved = gogMoneySaved.subtract(moneySaved)
                }
            }

            lastUpdated = LocalDateTime.now()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as UserGameStatistics
        return userId == other.userId
    }

    override fun hashCode(): Int = userId.hashCode()

    override fun toString(): String =
        "UserGameStatistics(userId=$userId, totalGames=$totalGamesClaimed, totalSaved=$totalMoneySaved)"
}