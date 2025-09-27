package com.gamez24.backend.springboot_gamez24.service


import com.gamez24.backend.springboot_gamez24.dto.UserCreateDTO
import com.gamez24.backend.springboot_gamez24.dto.UserOutDTO
import com.gamez24.backend.springboot_gamez24.entity.User
import com.gamez24.backend.springboot_gamez24.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun createUser(userCreateDTO: UserCreateDTO): UserOutDTO {
        if (userRepository.existsByEmail(userCreateDTO.email)) {
            throw RuntimeException("Email already registered")
        }

        // ✅ Check for existing username
        if (userRepository.existsByUsername(userCreateDTO.username)) {
            throw RuntimeException("Username already taken")
        }

        val user = User(
            email = userCreateDTO.email,
            username = userCreateDTO.username,
            password = passwordEncoder.encode(userCreateDTO.password)
        )

        val savedUser = userRepository.save(user)
        return UserOutDTO(savedUser.id, savedUser.username, savedUser.email)
    }

    fun findByEmail(email: String): User {
        return userRepository.findByEmail(email)
            ?: throw RuntimeException("User not found")
    }

    // ✅ Add method to find by username (optional)
    fun findByUsername(username: String): User {
        return userRepository.findByUsername(username)
            ?: throw RuntimeException("User not found")
    }

    fun getUserById(id: Long): UserOutDTO {
        val user = userRepository.findById(id).orElseThrow {
            RuntimeException("User not found")
        }
        return UserOutDTO(user.id, user.email, user.username)
    }
}