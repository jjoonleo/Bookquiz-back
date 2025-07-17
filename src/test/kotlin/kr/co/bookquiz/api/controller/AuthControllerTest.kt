package kr.co.bookquiz.api.controller

import com.fasterxml.jackson.databind.ObjectMapper
import kr.co.bookquiz.api.api.exception.ApiException
import kr.co.bookquiz.api.api.exception.ErrorCode
import kr.co.bookquiz.api.config.TestSecurityConfig
import kr.co.bookquiz.api.dto.auth.*
import kr.co.bookquiz.api.entity.Grade
import kr.co.bookquiz.api.entity.Province
import kr.co.bookquiz.api.security.JwtUtil
import kr.co.bookquiz.api.service.AuthService
import kr.co.bookquiz.api.service.SignupService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDateTime

@WebMvcTest(AuthController::class)
@Import(TestSecurityConfig::class)
class AuthControllerTest {
    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var authService: AuthService

    @MockitoBean
    private lateinit var signupService: SignupService

    @MockitoBean
    private lateinit var jwtUtil: JwtUtil

    private lateinit var validLoginRequest: LoginRequest
    private lateinit var validSignupRequest: SignupRequest
    private lateinit var loginResponse: LoginResponse
    private lateinit var signupResponse: SignupResponse

    @BeforeEach
    fun setUp() {
        validLoginRequest = LoginRequest(
            username = "testuser",
            password = "password123"
        )

        validSignupRequest = SignupRequest(
            username = "newuser",
            name = "New User",
            password = "NewPassword123!",
            confirmPassword = "NewPassword123!",
            email = "newuser@example.com",
            phoneNumber = "+82101234567",
            dateOfBirth = LocalDateTime.of(1990, 1, 1, 0, 0),
            province = Province.SEOUL,
            grade = Grade.COLLEGE_GENERAL,
            gender = true
        )

        val userInfo = UserInfo(
            username = "testuser",
            name = "Test User",
            email = "test@example.com",
            authorities = listOf("ROLE_USER")
        )

        loginResponse = LoginResponse(
            accessToken = "access-token",
            refreshToken = "refresh-token",
            tokenType = "Bearer",
            expiresIn = 3600L,
            user = userInfo
        )

        signupResponse = SignupResponse(
            username = "newuser",
            name = "New User",
            email = "newuser@example.com"
        )
    }

    @Test
    fun `should login successfully with valid credentials`() {
        // Given
        given(authService.authenticateUser(validLoginRequest)).willReturn(loginResponse)

        // When & Then
        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validLoginRequest))
                .with(csrf())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Login successful"))
            .andExpect(jsonPath("$.data.accessToken").value("access-token"))
            .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"))
            .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
            .andExpect(jsonPath("$.data.expiresIn").value(3600))
            .andExpect(jsonPath("$.data.user.username").value("testuser"))
            .andExpect(jsonPath("$.data.user.name").value("Test User"))
            .andExpect(jsonPath("$.data.user.email").value("test@example.com"))
            .andExpect(jsonPath("$.data.user.authorities[0]").value("ROLE_USER"))

        then(authService).should().authenticateUser(validLoginRequest)
    }

    @Test
    fun `should return 401 when login fails with invalid credentials`() {
        // Given
        given(authService.authenticateUser(validLoginRequest))
            .willThrow(ApiException(ErrorCode.INVALID_CREDENTIALS))

        // When & Then
        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validLoginRequest))
        )
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.title").value("Application Error"))
            .andExpect(jsonPath("$.detail").value(ErrorCode.INVALID_CREDENTIALS.defaultMessage))

        then(authService).should().authenticateUser(validLoginRequest)
    }

    @Test
    fun `should return 400 when login request is invalid`() {
        // Given
        val invalidLoginRequest = LoginRequest(
            username = "", // Empty username
            password = "password123"
        )

        // When & Then
        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidLoginRequest))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.title").value("Validation Error"))

        verifyNoInteractions(authService)
    }

    @Test
    fun `should refresh token successfully`() {
        // Given
        val refreshTokenRequest = RefreshTokenRequest("valid-refresh-token")
        val refreshTokenResponse = RefreshTokenResponse(
            accessToken = "new-access-token",
            tokenType = "Bearer",
            expiresIn = 3600L
        )
        given(authService.refreshToken("valid-refresh-token")).willReturn(refreshTokenResponse)

        // When & Then
        mockMvc.perform(
            post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshTokenRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Token refreshed successfully"))
            .andExpect(jsonPath("$.data.accessToken").value("new-access-token"))
            .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
            .andExpect(jsonPath("$.data.expiresIn").value(3600))

        then(authService).should().refreshToken("valid-refresh-token")
    }

    @Test
    fun `should return 401 when refresh token is invalid`() {
        // Given
        val refreshTokenRequest = RefreshTokenRequest("invalid-refresh-token")
        given(authService.refreshToken("invalid-refresh-token"))
            .willThrow(ApiException(ErrorCode.INVALID_TOKEN))

        // When & Then
        mockMvc.perform(
            post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshTokenRequest))
        )
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.title").value("Application Error"))
            .andExpect(jsonPath("$.detail").value(ErrorCode.INVALID_TOKEN.defaultMessage))

        then(authService).should().refreshToken("invalid-refresh-token")
    }

    @Test
    @WithMockUser(username = "testuser")
    fun `should logout successfully`() {
        // When & Then
        mockMvc.perform(post("/api/auth/logout"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Logged out successfully"))
            .andExpect(jsonPath("$.data").isEmpty)

        then(authService).should().logout("testuser")
    }

    @Test
    fun `should signup successfully with valid data`() {
        // Given
        given(signupService.registerUser(validSignupRequest)).willReturn(signupResponse)

        // When & Then
        mockMvc.perform(
            post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validSignupRequest))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("User registered successfully"))
            .andExpect(jsonPath("$.data.username").value("newuser"))
            .andExpect(jsonPath("$.data.name").value("New User"))
            .andExpect(jsonPath("$.data.email").value("newuser@example.com"))

        then(signupService).should().registerUser(validSignupRequest)
    }

    @Test
    fun `should return 409 when signup with duplicate email`() {
        // Given
        given(signupService.registerUser(validSignupRequest))
            .willThrow(ApiException(ErrorCode.EMAIL_DUPLICATE))

        // When & Then
        mockMvc.perform(
            post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validSignupRequest))
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.title").value("Application Error"))
            .andExpect(jsonPath("$.detail").value(ErrorCode.EMAIL_DUPLICATE.defaultMessage))

        then(signupService).should().registerUser(validSignupRequest)
    }

    @Test
    fun `should return 400 when signup request is invalid`() {
        // Given
        val invalidSignupRequest = validSignupRequest.copy(
            username = "ab", // Too short
            email = "invalid-email", // Invalid format
            password = "weak" // Too weak
        )

        // When & Then
        mockMvc.perform(
            post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidSignupRequest))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.title").value("Validation Error"))

        verifyNoInteractions(signupService)
    }

    @Test
    fun `should check username availability`() {
        // Given
        given(signupService.checkUsernameAvailability("available")).willReturn(true)
        given(signupService.checkUsernameAvailability("taken")).willReturn(false)

        // When & Then - Available username
        mockMvc.perform(get("/api/auth/check-username?username=available"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Username availability checked"))
            .andExpect(jsonPath("$.data.available").value(true))

        // When & Then - Taken username
        mockMvc.perform(get("/api/auth/check-username?username=taken"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Username availability checked"))
            .andExpect(jsonPath("$.data.available").value(false))

        then(signupService).should().checkUsernameAvailability("available")
        then(signupService).should().checkUsernameAvailability("taken")
    }

    @Test
    fun `should check email availability`() {
        // Given
        given(signupService.checkEmailAvailability("available@example.com")).willReturn(true)
        given(signupService.checkEmailAvailability("taken@example.com")).willReturn(false)

        // When & Then - Available email
        mockMvc.perform(get("/api/auth/check-email?email=available@example.com"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Email availability checked"))
            .andExpect(jsonPath("$.data.available").value(true))

        // When & Then - Taken email
        mockMvc.perform(get("/api/auth/check-email?email=taken@example.com"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Email availability checked"))
            .andExpect(jsonPath("$.data.available").value(false))

        then(signupService).should().checkEmailAvailability("available@example.com")
        then(signupService).should().checkEmailAvailability("taken@example.com")
    }

    @Test
    fun `should check phone number availability`() {
        // Given
        given(signupService.checkPhoneNumberAvailability("+82101111111")).willReturn(true)
        given(signupService.checkPhoneNumberAvailability("+82102222222")).willReturn(false)

        // When & Then - Available phone number
        mockMvc.perform(get("/api/auth/check-phone?phoneNumber=+82101111111"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Phone number availability checked"))
            .andExpect(jsonPath("$.data.available").value(true))

        // When & Then - Taken phone number
        mockMvc.perform(get("/api/auth/check-phone?phoneNumber=+82102222222"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Phone number availability checked"))
            .andExpect(jsonPath("$.data.available").value(false))

        then(signupService).should().checkPhoneNumberAvailability("+82101111111")
        then(signupService).should().checkPhoneNumberAvailability("+82102222222")
    }
}