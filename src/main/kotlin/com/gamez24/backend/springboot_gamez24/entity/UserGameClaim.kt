package com.gamez24.backend.springboot_gamez24.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PositiveOrZero

@Entity
@Table(
    name = "user_game_claims",
    indexes = [
        Index(name = "idx_user_claims", columnList = "user_id"),
        Index(name = "idx_store_claims", columnList = "store"),
        Index(name = "idx_claimed_at", columnList = "claimed_at")
    ],
    uniqueConstraints = [
        UniqueConstraint(
            name = "unique_user_game_store",
            columnNames = ["user_id", "game_name", "store"]
        )
    ]
)
class UserGameClaim(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "user_id", nullable = false)
    var userId: Long = 0,

    // Game snapshot data (from external APIs)
    @Column(name = "game_name", nullable = false)
    @field:NotBlank
    var gameName: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var store: GameStore = GameStore.STEAM,

    @Column(name = "original_price", nullable = false, precision = 10, scale = 2)
    @field:PositiveOrZero
    var originalPrice: BigDecimal = BigDecimal.ZERO,

    @Column(name = "discounted_price", nullable = false, precision = 10, scale = 2)
    @field:PositiveOrZero
    var discountedPrice: BigDecimal = BigDecimal.ZERO,

    @Column(name = "money_saved", nullable = false, precision = 10, scale = 2)
    @field:PositiveOrZero
    var moneySaved: BigDecimal = BigDecimal.ZERO,

    // Optional metadata
    @Column(name = "game_image_url", length = 500)
    var gameImageUrl: String? = null,

    @Column(name = "external_game_id", length = 100)
    var externalGameId: String? = null,

    @Column(name = "claimed_at", nullable = false)
    var claimedAt: LocalDateTime = LocalDateTime.now(),

    // Relationship to User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    var user: User? = null
) {
    // No-arg constructor for JPA
    constructor() : this(
        0, 0, "", GameStore.STEAM,
        BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
        null, null, LocalDateTime.now()
    )

    // Convenient constructor
    constructor(
        userId: Long,
        gameName: String,
        store: GameStore,
        originalPrice: BigDecimal,
        discountedPrice: BigDecimal = BigDecimal.ZERO,
        gameImageUrl: String? = null,
        externalGameId: String? = null
    ) : this(
        0, userId, gameName, store, originalPrice, discountedPrice,
        originalPrice.subtract(discountedPrice), gameImageUrl, externalGameId, LocalDateTime.now()
    )

    // Calculate money saved automatically
    @PrePersist
    @PreUpdate
    fun calculateMoneySaved() {
        moneySaved = originalPrice.subtract(discountedPrice)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as UserGameClaim
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String =
        "UserGameClaim(id=$id, userId=$userId, gameName='$gameName', store=$store, moneySaved=$moneySaved)"
}