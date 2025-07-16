package kr.co.bookquiz.api.dto.auth

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import kr.co.bookquiz.api.entity.Grade
import kr.co.bookquiz.api.entity.Province
import java.time.LocalDateTime

data class SignupRequest(
    @field:NotBlank(message = "Username is required")
    @field:Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @field:Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores")
    val username: String,

    @field:NotBlank(message = "Name is required")
    @field:Size(min = 2, max = 64, message = "Name must be between 2 and 64 characters")
    val name: String,

    @field:NotBlank(message = "Password is required")
    @field:Size(min = 8, message = "Password must be at least 8 characters long")
    @field:Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&].*$",
        message = "Password must contain at least one lowercase letter, one uppercase letter, one digit, and one special character"
    )
    val password: String,

    @field:NotBlank(message = "Password confirmation is required")
    val confirmPassword: String,

    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email should be valid")
    @field:Size(max = 255, message = "Email must not exceed 255 characters")
    val email: String,

    @field:NotBlank(message = "Phone number is required")
    @field:Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Phone number must be in E.164 format (e.g., +82101234567)")
    val phoneNumber: String,

    @field:NotNull(message = "Date of birth is required")
    val dateOfBirth: LocalDateTime,

    @field:NotNull(message = "Province is required")
    val province: Province,

    @field:NotNull(message = "Grade is required")
    val grade: Grade,

    @field:NotNull(message = "Gender is required")
    val gender: Boolean // true for male, false for female
)

data class SignupResponse(
    val username: String,
    val name: String,
    val email: String,
)