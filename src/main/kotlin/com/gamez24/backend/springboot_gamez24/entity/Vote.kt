// Vote.kt - New Vote Entity
package com.gamez24.backend.springboot_gamez24.entity

import jakarta.persistence.*
import java.time.LocalDateTime

enum class VoteType {
    UP, DOWN
}

@Entity
@Table(
    name = "votes",
    indexes = [
        Index(name = "idx_votes_user_article", columnList = "user_id,article_id"),
        Index(name = "idx_votes_article", columnList = "article_id"),
        Index(name = "idx_votes_user", columnList = "user_id")
    ],
    uniqueConstraints = [
        UniqueConstraint(
            name = "unique_user_article_vote",
            columnNames = ["user_id", "article_id"]
        )
    ]
)
class Vote(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "user_id", nullable = false)
    var userId: Long = 0,

    @Column(name = "article_id", nullable = false)
    var articleId: Long = 0,

    @Enumerated(EnumType.STRING)
    @Column(name = "vote_type", nullable = false)
    var voteType: VoteType = VoteType.UP,

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    var user: User? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", insertable = false, updatable = false)
    var article: Article? = null
) {
    constructor() : this(0, 0, 0, VoteType.UP, LocalDateTime.now())

    constructor(userId: Long, articleId: Long, voteType: VoteType) :
            this(0, userId, articleId, voteType, LocalDateTime.now())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Vote
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "Vote(id=$id, userId=$userId, articleId=$articleId, voteType=$voteType)"
}