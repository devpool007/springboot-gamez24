// ArticleRepository.kt - Minimal and Clean
package com.gamez24.backend.springboot_gamez24.repository

import com.gamez24.backend.springboot_gamez24.entity.Article
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

@Repository
interface ArticleRepository : JpaRepository<Article, Long> {

    // Basic queries using Spring Data JPA method names (no custom @Query needed)
    fun findBySlug(slug: String): Article?
    fun existsBySlug(slug: String): Boolean
    fun findByAuthorIdOrderByCreatedAtDesc(authorId: Long, pageable: Pageable): Page<Article>
    fun findAllByOrderByCreatedAtDesc(pageable: Pageable): Page<Article>

    // ✅ Optional: Simple search if you need it
    @Query("SELECT a FROM Article a WHERE LOWER(a.title) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY a.createdAt DESC")
    fun searchArticles(@Param("query") query: String, pageable: Pageable): Page<Article>
}