package kr.co.bookquiz.api.dto.auth

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class LoginRequest(
    @field:NotBlank(message = "Username is required")
    @field:Size(max = 50, message = "Username must not exceed 50 characters")
    val username: String,

    @field:NotBlank(message = "Password is required")
    val password: String
)

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long,
    val user: UserInfo
)

data class UserInfo(
    val username: String,
    val name: String,
    val email: String,
    val authorities: List<String>
)