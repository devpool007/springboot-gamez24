package com.gamez24.backend.springboot_gamez24.controller


import com.gamez24.backend.springboot_gamez24.dto.TokenDTO
import com.gamez24.backend.springboot_gamez24.dto.UserCreateDTO
import com.gamez24.backend.springboot_gamez24.dto.UserLoginDTO
import com.gamez24.backend.springboot_gamez24.dto.UserOutDTO
import com.gamez24.backend.springboot_gamez24.service.AuthService
import com.gamez24.backend.springboot_gamez24.service.JwtService
import com.gamez24.backend.springboot_gamez24.service.UserService
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/users")
class UserController(
    private val authService: AuthService,
    private val jwtService: JwtService,
    private val userService: UserService
) {

    @PostMapping("/signup")
    fun signup(
        @Valid @RequestBody userCreateDTO: UserCreateDTO,
        response: HttpServletResponse
    ): ResponseEntity<UserOutDTO> {
        return try {
            val userOut = authService.signup(userCreateDTO)

            // Create JWT token for the new user
            val user = userService.findByEmail(userCreateDTO.email)
            val token = jwtService.generateToken(user)

            // Set JWT cookie - matching your FastAPI implementation
            val jwtCookie = Cookie("jwt", token).apply {
                isHttpOnly = true
                secure = false // Set to true in production with HTTPS
                maxAge = 86400 // 24 hours
                path = "/"
            }
            response.addCookie(jwtCookie)

            ResponseEntity.ok(userOut)
        } catch (e: Exception) {
            when (e.message) {
                "Email already registered" -> throw ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Email already registered"
                )
                else -> throw ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "User creation failed"
                )
            }
        }
    }

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody userLoginDTO: UserLoginDTO,
        response: HttpServletResponse
    ): ResponseEntity<TokenDTO> {
        return try {
            val tokenDTO = authService.login(userLoginDTO)

            // Set JWT cookie - matching your FastAPI implementation
            val jwtCookie = Cookie("jwt", tokenDTO.accessToken).apply {
                isHttpOnly = true
                secure = false // Set to true in production with HTTPS
                maxAge = 86400 // 24 hours
                path = "/"
            }
            response.addCookie(jwtCookie)

            ResponseEntity.ok(tokenDTO)
        } catch (e: Exception) {
            throw ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Invalid credentials"
            )
        }
    }

    @PostMapping("/logout")
    fun logout(response: HttpServletResponse): ResponseEntity<Map<String, String>> {
        println("👋 Logout request received")

        // Delete the JWT cookie by setting it with maxAge = 0
        val jwtCookie = Cookie("jwt", "").apply {
            isHttpOnly = true
            secure = false // Set to true in production with HTTPS
            maxAge = 0     // This deletes the cookie immediately
            path = "/"     // Must match the path used when setting the cookie
        }

        response.addCookie(jwtCookie)
        println("✅ JWT cookie deleted")

        return ResponseEntity.ok(mapOf("message" to "Logged out successfully"))
    }

    @GetMapping("/me")
    fun me(request: HttpServletRequest): ResponseEntity<UserOutDTO> {
        // Debug logging - matching your FastAPI implementation
        println("🌐 Request origin: ${request.getHeader("origin")}")
        println("🌐 Request host: ${request.getHeader("host")}")
        println("🍪 All cookies received: ${request.cookies?.associate { it.name to it.value } ?: emptyMap()}")
        println("🍪 Cookie header: ${request.getHeader("cookie")}")

        // Get JWT from cookie
        val jwtCookie = request.cookies?.find { it.name == "jwt" }
        val token = jwtCookie?.value

        if (token == null) {
            println("❌ No JWT cookie found in request!")
            throw ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Not authenticated"
            )
        }

        return try {
            val userEmail = jwtService.extractUsername(token)
            val user = userService.findByEmail(userEmail)

            println("✅ Found JWT cookie: ${token.take(20)}...")

            // Return user data with userid field for compatibility
            ResponseEntity.ok(UserOutDTO(user.id, user.email))
        } catch (e: Exception) {
            println("❌ Token validation failed: ${e.message}")
            throw ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Invalid token"
            )
        }
    }
}