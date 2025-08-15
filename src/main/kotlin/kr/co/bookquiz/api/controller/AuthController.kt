package kr.co.bookquiz.api.controller

import jakarta.validation.Valid
import kr.co.bookquiz.api.api.response.ApiResponse
import kr.co.bookquiz.api.dto.auth.LoginRequest
import kr.co.bookquiz.api.dto.auth.LoginResponse
import kr.co.bookquiz.api.dto.auth.ProfileResponse
import kr.co.bookquiz.api.dto.auth.RefreshTokenRequest
import kr.co.bookquiz.api.dto.auth.RefreshTokenResponse
import kr.co.bookquiz.api.dto.auth.SignupRequest
import kr.co.bookquiz.api.dto.auth.SignupResponse
import kr.co.bookquiz.api.service.AuthService
import kr.co.bookquiz.api.service.SignupService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
        private val authService: AuthService,
        private val signupService: SignupService
) {

    @PostMapping("/login")
    fun login(
            @Valid @RequestBody loginRequest: LoginRequest
    ): ResponseEntity<ApiResponse<LoginResponse>> {
        val response = authService.authenticateUser(loginRequest)
        val apiResponse = ApiResponse(success = true, message = "Login successful", data = response)
        return ResponseEntity.ok(apiResponse)
    }

    @PostMapping("/refresh")
    fun refresh(
            @Valid @RequestBody refreshTokenRequest: RefreshTokenRequest
    ): ResponseEntity<ApiResponse<RefreshTokenResponse>> {
        val response = authService.refreshToken(refreshTokenRequest.refreshToken)
        val apiResponse =
                ApiResponse(
                        success = true,
                        message = "Token refreshed successfully",
                        data = response
                )
        return ResponseEntity.ok(apiResponse)
    }

    @PostMapping("/logout")
    fun logout(authentication: Authentication): ResponseEntity<ApiResponse<Nothing>> {
        authService.logout(authentication.name)
        val apiResponse =
                ApiResponse(success = true, message = "Logged out successfully", data = null)
        return ResponseEntity.ok(apiResponse)
    }

    @PostMapping("/signup")
    fun signup(
            @Valid @RequestBody signupRequest: SignupRequest
    ): ResponseEntity<ApiResponse<SignupResponse>> {
        val response = signupService.registerUser(signupRequest)
        val apiResponse =
                ApiResponse(
                        success = true,
                        message = "User registered successfully",
                        data = response
                )
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse)
    }

    @GetMapping("/check-username")
    fun checkUsernameAvailability(
            @RequestParam username: String
    ): ResponseEntity<ApiResponse<Map<String, Boolean>>> {
        val isAvailable = signupService.checkUsernameAvailability(username)
        val response = mapOf("available" to isAvailable)
        val apiResponse =
                ApiResponse(
                        success = true,
                        message = "Username availability checked",
                        data = response
                )
        return ResponseEntity.ok(apiResponse)
    }

    @GetMapping("/check-email")
    fun checkEmailAvailability(
            @RequestParam email: String
    ): ResponseEntity<ApiResponse<Map<String, Boolean>>> {
        val isAvailable = signupService.checkEmailAvailability(email)
        val response = mapOf("available" to isAvailable)
        val apiResponse =
                ApiResponse(success = true, message = "Email availability checked", data = response)
        return ResponseEntity.ok(apiResponse)
    }

    @GetMapping("/check-phone")
    fun checkPhoneNumberAvailability(
            @RequestParam phoneNumber: String
    ): ResponseEntity<ApiResponse<Map<String, Boolean>>> {
        val isAvailable = signupService.checkPhoneNumberAvailability(phoneNumber)
        val response = mapOf("available" to isAvailable)
        val apiResponse =
                ApiResponse(
                        success = true,
                        message = "Phone number availability checked",
                        data = response
                )
        return ResponseEntity.ok(apiResponse)
    }

    @GetMapping("/profile")
    fun getProfile(authentication: Authentication): ResponseEntity<ApiResponse<ProfileResponse>> {
        val profile = authService.getUserProfile(authentication.name)
        val apiResponse =
                ApiResponse(
                        success = true,
                        message = "Profile retrieved successfully",
                        data = profile
                )
        return ResponseEntity.ok(apiResponse)
    }
}
