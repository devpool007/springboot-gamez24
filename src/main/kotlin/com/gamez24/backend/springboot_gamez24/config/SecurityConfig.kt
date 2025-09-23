package com.gamez24.backend.springboot_gamez24.config

import com.gamez24.backend.springboot_gamez24.filter.JwtAuthenticationFilter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthFilter: JwtAuthenticationFilter,
    private val authenticationProvider: AuthenticationProvider,

    @Value("\${cors.allowed-origins}")
    private val allowedCorsOrigins: String,

    @Value("\${cors.allowed-methods}")
    private val allowedMethods: String,

    @Value("\${cors.allowed-headers}")
    private val allowedHeaders: String,

    @Value("\${cors.allow-credentials}")
    private val allowCredentials: Boolean
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .csrf { it.disable() }
            .cors { it.configurationSource(corsConfigurationSource()) }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/users/signup", "/users/login").permitAll()
                    .requestMatchers("/h2-console/**").permitAll()
                    .requestMatchers("/error").permitAll()
                    .anyRequest().authenticated()
            }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)
            .headers { headers -> headers.frameOptions { frameOptions -> frameOptions.sameOrigin() } } // For H2 console
            .build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration().apply {
            // Parse allowed origins from properties
            val patterns = allowedCorsOrigins.split(",").map { it.trim() }
            allowedOriginPatterns = patterns

            // Parse allowed methods from properties
            allowedMethods = this@SecurityConfig.allowedMethods.split(",").map { it.trim() }

            // Parse allowed headers from properties
            if (this@SecurityConfig.allowedHeaders == "*") {
                addAllowedHeader("*")
            } else {
                allowedHeaders = this@SecurityConfig.allowedHeaders.split(",").map { it.trim() }
            }

            allowCredentials = this@SecurityConfig.allowCredentials
        }

        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", configuration)
        }
    }
}