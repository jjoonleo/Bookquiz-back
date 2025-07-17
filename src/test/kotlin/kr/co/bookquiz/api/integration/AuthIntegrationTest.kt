package kr.co.bookquiz.api.integration

import com.fasterxml.jackson.databind.ObjectMapper
import kr.co.bookquiz.api.TestcontainersConfiguration
import kr.co.bookquiz.api.dto.auth.LoginRequest
import kr.co.bookquiz.api.dto.auth.SignupRequest
import kr.co.bookquiz.api.entity.Grade
import kr.co.bookquiz.api.entity.Province
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration::class)
@ActiveProfiles("test")
@Transactional
class AuthIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `should complete full signup and login flow`() {
        val signupRequest = SignupRequest(
            username = "integrationtest",
            name = "Integration Test User",
            password = "IntegrationTest123!",
            confirmPassword = "IntegrationTest123!",
            email = "integration@test.com",
            phoneNumber = "+82101234567",
            dateOfBirth = LocalDateTime.of(1990, 1, 1, 0, 0),
            province = Province.SEOUL,
            grade = Grade.COLLEGE_GENERAL,
            gender = true
        )

        // Step 1: Check username availability (should be available)
        mockMvc.perform(get("/api/auth/check-username?username=integrationtest"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.available").value(true))

        // Step 2: Check email availability (should be available)
        mockMvc.perform(get("/api/auth/check-email?email=integration@test.com"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.available").value(true))

        // Step 3: Check phone number availability (should be available)
        mockMvc.perform(get("/api/auth/check-phone?phoneNumber=+82101234567"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.available").value(true))

        // Step 4: Register the user
        mockMvc.perform(
            post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("User registered successfully"))
            .andExpect(jsonPath("$.data.username").value("integrationtest"))
            .andExpect(jsonPath("$.data.name").value("Integration Test User"))
            .andExpect(jsonPath("$.data.email").value("integration@test.com"))

        // Step 5: Check availability again (should now be taken)
        mockMvc.perform(get("/api/auth/check-username?username=integrationtest"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.available").value(false))

        mockMvc.perform(get("/api/auth/check-email?email=integration@test.com"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.available").value(false))

        mockMvc.perform(get("/api/auth/check-phone?phoneNumber=+82101234567"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.available").value(false))

        // Step 6: Login with the new user
        val loginRequest = LoginRequest(
            username = "integrationtest",
            password = "IntegrationTest123!"
        )

        val loginResult = mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Login successful"))
            .andExpect(jsonPath("$.data.accessToken").exists())
            .andExpect(jsonPath("$.data.refreshToken").exists())
            .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
            .andExpect(jsonPath("$.data.user.username").value("integrationtest"))
            .andExpect(jsonPath("$.data.user.name").value("Integration Test User"))
            .andExpect(jsonPath("$.data.user.email").value("integration@test.com"))
            .andExpect(jsonPath("$.data.user.authorities[0]").value("ROLE_USER"))
            .andReturn()

        // Step 7: Extract tokens for further testing
        val responseJson = objectMapper.readTree(loginResult.response.contentAsString)
        val accessToken = responseJson.get("data").get("accessToken").asText()
        val refreshToken = responseJson.get("data").get("refreshToken").asText()

        // Step 8: Test refresh token functionality
        val refreshTokenRequest = mapOf("refreshToken" to refreshToken)

        mockMvc.perform(
            post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshTokenRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Token refreshed successfully"))
            .andExpect(jsonPath("$.data.accessToken").exists())
            .andExpect(jsonPath("$.data.tokenType").value("Bearer"))

        // Step 9: Test protected endpoint access
        mockMvc.perform(
            get("/api/test/protected")
                .header("Authorization", "Bearer $accessToken")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.username").value("integrationtest"))
            .andExpect(jsonPath("$.data.authorities[0]").value("ROLE_USER"))

        // Step 10: Test logout
        mockMvc.perform(
            post("/api/auth/logout")
                .header("Authorization", "Bearer $accessToken")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Logged out successfully"))
    }

    @Test
    fun `should fail signup with duplicate username`() {
        val firstUser = SignupRequest(
            username = "duplicatetest",
            name = "First User",
            password = "Password123!",
            confirmPassword = "Password123!",
            email = "first@test.com",
            phoneNumber = "+82101111111",
            dateOfBirth = LocalDateTime.of(1990, 1, 1, 0, 0),
            province = Province.SEOUL,
            grade = Grade.COLLEGE_GENERAL,
            gender = true
        )

        val secondUser = SignupRequest(
            username = "duplicatetest", // Same username
            name = "Second User",
            password = "Password123!",
            confirmPassword = "Password123!",
            email = "second@test.com",
            phoneNumber = "+82102222222",
            dateOfBirth = LocalDateTime.of(1990, 1, 1, 0, 0),
            province = Province.SEOUL,
            grade = Grade.COLLEGE_GENERAL,
            gender = true
        )

        // Register first user successfully
        mockMvc.perform(
            post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firstUser))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.success").value(true))

        // Try to register second user with same username (should fail)
        mockMvc.perform(
            post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(secondUser))
        )
            .andExpect(status().isInternalServerError)
            .andExpect(jsonPath("$.title").value("Application Error"))
            .andExpect(jsonPath("$.detail").value("Username already exists"))
    }

    @Test
    fun `should fail login with invalid credentials`() {
        val invalidLoginRequest = LoginRequest(
            username = "nonexistent",
            password = "wrongpassword"
        )

        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidLoginRequest))
        )
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.title").value("Application Error"))
            .andExpect(jsonPath("$.detail").value("Invalid username or password"))
    }

    @Test
    fun `should fail to access protected endpoint without token`() {
        mockMvc.perform(get("/api/test/protected"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `should access public endpoint without authentication`() {
        mockMvc.perform(get("/api/test/public"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.message").value("This is a public endpoint"))
    }

    @Test
    fun `should fail refresh with invalid token`() {
        val invalidRefreshRequest = mapOf("refreshToken" to "invalid-token")

        mockMvc.perform(
            post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRefreshRequest))
        )
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.title").value("Application Error"))
    }

    @Test
    fun `should validate signup request fields`() {
        val invalidSignupRequest = SignupRequest(
            username = "ab", // Too short
            name = "", // Empty
            password = "weak", // Too weak
            confirmPassword = "different", // Doesn't match
            email = "invalid-email", // Invalid format
            phoneNumber = "123", // Invalid format
            dateOfBirth = LocalDateTime.of(1990, 1, 1, 0, 0),
            province = Province.SEOUL,
            grade = Grade.COLLEGE_GENERAL,
            gender = true
        )

        mockMvc.perform(
            post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidSignupRequest))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.title").value("Validation Error"))
    }

    @Test
    fun `should validate login request fields`() {
        val invalidLoginRequest = LoginRequest(
            username = "", // Empty
            password = "" // Empty
        )

        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidLoginRequest))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.title").value("Validation Error"))
    }
}