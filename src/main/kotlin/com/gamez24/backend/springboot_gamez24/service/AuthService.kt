package com.gamez24.backend.springboot_gamez24.service



import com.gamez24.backend.springboot_gamez24.dto.TokenDTO
import com.gamez24.backend.springboot_gamez24.dto.UserCreateDTO
import com.gamez24.backend.springboot_gamez24.dto.UserLoginDTO
import com.gamez24.backend.springboot_gamez24.dto.UserOutDTO
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userService: UserService,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager
) {

    fun signup(userCreateDTO: UserCreateDTO): UserOutDTO {
        return userService.createUser(userCreateDTO)
    }

    fun login(userLoginDTO: UserLoginDTO): TokenDTO {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                userLoginDTO.email,
                userLoginDTO.password
            )
        )

        val user = userService.findByEmail(userLoginDTO.email)
        val jwtToken = jwtService.generateToken(user)

        return TokenDTO(jwtToken)
    }
}