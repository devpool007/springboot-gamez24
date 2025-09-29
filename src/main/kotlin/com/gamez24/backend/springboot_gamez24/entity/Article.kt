package com.gamez24.backend.springboot_gamez24.entity

import com.gamez24.backend.springboot_gamez24.toSlug
import com.gamez24.backend.springboot_gamez24.toUniqueSlug
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

@Entity
@Table(
    name = "articles",
    indexes = [
        Index(name = "idx_articles_slug", columnList = "slug"),
        Index(name = "idx_articles_author", columnList = "author_id"),
        Index(name = "idx_articles_created", columnList = "created_at")
    ]
)
class Article(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false)
    @field:NotBlank(message = "Title is required")
    @field:Size(max = 255, message = "Title must not exceed 255 characters")
    var title: String = "",

    @Column(columnDefinition = "TEXT", nullable = false)
    @field:NotBlank(message = "Content is required")
    var content: String = "",

    @Column(unique = true, nullable = false)
    @field:NotBlank(message = "Slug is required")
    var slug: String = title.toUniqueSlug(),

    @Column(name = "author_id", nullable = false)
    var authorId: Long = 0,

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", insertable = false, updatable = false)
    var author: User? = null,

    @OneToMany(mappedBy = "article", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var votes: MutableList<Vote> = mutableListOf()
) {
    constructor() : this(0, "", "", "", 0, LocalDateTime.now(), LocalDateTime.now())

    constructor(title: String, content: String, slug: String, authorId: Long) :
            this(0, title, content, slug, authorId, LocalDateTime.now(), LocalDateTime.now())

    @PreUpdate
    fun onUpdate() {
        updatedAt = LocalDateTime.now()
    }

    // Helper methods for vote counts
    fun getUpvoteCount(): Long {
        return votes.count { it.voteType == VoteType.UP }.toLong()
    }

    fun getDownvoteCount(): Long {
        return votes.count { it.voteType == VoteType.DOWN }.toLong()
    }

    fun getNetScore(): Long {
        return getUpvoteCount() - getDownvoteCount()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Article
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "Article(id=$id, title='$title', slug='$slug', authorId=$authorId)"
}