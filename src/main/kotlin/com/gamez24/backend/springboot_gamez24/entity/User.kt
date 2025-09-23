package com.gamez24.backend.springboot_gamez24.entity

// User.kt - Fixed Entity
import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(unique = true, nullable = false)
    @field:Email
    @field:NotBlank
    var email: String = "",

    @Column(nullable = false)
    @field:NotBlank
    private var password: String = "",

    @Column(name = "created_at")
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
) : UserDetails {

    // No-argument constructor for JPA
    constructor() : this(0, "", "", LocalDateTime.now(), LocalDateTime.now())

    // Secondary constructor for easy creation
    constructor(email: String, password: String) : this(0, email, password, LocalDateTime.now(), LocalDateTime.now())

    // UserDetails implementation
    override fun getAuthorities(): Collection<GrantedAuthority> = emptyList()

    override fun getPassword(): String = password

    fun setPassword(password: String) {
        this.password = password
    }

    override fun getUsername(): String = email

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true

    @PrePersist
    fun onCreate() {
        val now = LocalDateTime.now()
        if (createdAt == LocalDateTime.MIN) createdAt = now
        updatedAt = now
    }

    @PreUpdate
    fun onUpdate() {
        updatedAt = LocalDateTime.now()
    }

    // Equals and hashCode for JPA
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as User
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "User(id=$id, email='$email')"
}