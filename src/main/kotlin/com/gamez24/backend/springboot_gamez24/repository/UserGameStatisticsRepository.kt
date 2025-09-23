package com.gamez24.backend.springboot_gamez24.repository

// UserGameStatisticsRepository.kt (Optional)

import com.gamez24.backend.springboot_gamez24.entity.UserGameStatistics
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserGameStatisticsRepository : JpaRepository<UserGameStatistics, Long> {
    fun findByUserId(userId: Long): UserGameStatistics?
    fun existsByUserId(userId: Long): Boolean
}