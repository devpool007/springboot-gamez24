package com.gamez24.backend.springboot_gamez24.repository

import com.gamez24.backend.springboot_gamez24.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
    fun existsByEmail(email: String): Boolean

    // ✅ Add username-related queries
    fun existsByUsername(username: String): Boolean
    fun findByUsername(username: String): User?
}