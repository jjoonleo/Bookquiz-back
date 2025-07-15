package kr.co.bookquiz.api.entity

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    val id: java.util.UUID,

    @Column(nullable = false, length = 50)
    val name: String,

    @Column(nullable = false, length = 60)
    val password: String,

    @NotBlank
    @Email
    @Size(max = 255)
    @Column(unique = true, nullable = false, length = 255)
    val email: String,

    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Phone number must be in E.164 format (e.g., +12345678901)")
    @Size(max = 20) // Max length for E.164 is 15 digits + '+'
    @Column(length = 20, nullable = false) // Can be nullable if phone is optional
    val phoneNumber: String,

    @Column(name = "date_of_birth", nullable = false)
    val dateOfBirth: LocalDateTime,

    @Column(name = "province", columnDefinition = "province_enum", nullable = false)
    val province: Province,

    @Column(nullable = false)
    val deleted: Boolean = false,

    @Convert(converter = RoleEnumConverter::class)
    @Column(name = "role", columnDefinition = "role_enum", nullable = false)
    val role: Role = Role.USER,

    @Column(nullable = false)
    val banned: Boolean = false,

    @Column(nullable = false)
    val gender: Boolean,

    @Column(name = "refresh_token", length = 1000)
    var refreshToken: String? = null,

    @Column(name = "last_login")
    val lastLogin: LocalDateTime? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now(),
)