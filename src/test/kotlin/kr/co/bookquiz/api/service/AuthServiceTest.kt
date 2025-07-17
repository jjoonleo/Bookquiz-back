package kr.co.bookquiz.api.service

import kr.co.bookquiz.api.api.exception.ApiException
import kr.co.bookquiz.api.api.exception.ErrorCode
import kr.co.bookquiz.api.dto.auth.LoginRequest
import kr.co.bookquiz.api.entity.Authority
import kr.co.bookquiz.api.entity.Grade
import kr.co.bookquiz.api.entity.Province
import kr.co.bookquiz.api.entity.User
import kr.co.bookquiz.api.repository.UserRepository
import kr.co.bookquiz.api.security.JwtUtil
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime
import org.assertj.core.api.Assertions.assertThat

@ExtendWith(MockitoExtension::class)
class AuthServiceTest {

    private lateinit var authService: AuthService
    private lateinit var authenticationManager: AuthenticationManager
    private lateinit var userRepository: UserRepository
    private lateinit var jwtUtil: JwtUtil

    private lateinit var testUser: User
    private lateinit var userDetails: UserDetails
    private lateinit var authentication: Authentication
    private lateinit var loginRequest: LoginRequest

    @BeforeEach
    fun setUp() {
        authenticationManager = mock(AuthenticationManager::class.java)
        userRepository = mock(UserRepository::class.java)
        jwtUtil = mock(JwtUtil::class.java)
        authService = AuthService(authenticationManager, userRepository, jwtUtil, 3600)

        val authority = Authority(
            id = 1L,
            name = "ROLE_USER",
            description = "Default user role",
            createdAt = LocalDateTime.now()
        )

        testUser = User(
            username = "testuser",
            name = "Test User",
            password = "encodedPassword",
            email = "test@example.com",
            phoneNumber = "+82101234567",
            dateOfBirth = LocalDateTime.of(1990, 1, 1, 0, 0),
            province = Province.SEOUL,
            deleted = false,
            grade = Grade.COLLEGE_GENERAL,
            enabled = true,
            gender = true,
            refreshToken = null,
            lastLogin = null,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            authorities = setOf(authority)
        )

        userDetails = object : UserDetails {
            override fun getUsername() = "testuser"
            override fun getPassword() = "encodedPassword"
            override fun isEnabled() = true
            override fun isAccountNonExpired() = true
            override fun isAccountNonLocked() = true
            override fun isCredentialsNonExpired() = true
            override fun getAuthorities(): Collection<GrantedAuthority> = 
                listOf(SimpleGrantedAuthority("ROLE_USER"))
        }

        authentication = mock(Authentication::class.java)

        loginRequest = LoginRequest(
            username = "testuser",
            password = "password123"
        )
    }

    @Test
    fun `should authenticate user successfully`() {
        // Given
        given(authentication.principal).willReturn(userDetails)
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken::class.java)))
            .willReturn(authentication)
        given(userRepository.findByUsernameAndDeletedFalse("testuser")).willReturn(testUser)
        given(jwtUtil.generateToken(authentication)).willReturn("access-token")
        given(jwtUtil.generateRefreshToken("testuser")).willReturn("refresh-token")
        given(userRepository.save(any(User::class.java))).willReturn(testUser.copy(refreshToken = "refresh-token"))

        // When
        val result = authService.authenticateUser(loginRequest)

        // Then
        assertThat(result.accessToken).isEqualTo("access-token")
        assertThat(result.refreshToken).isEqualTo("refresh-token")
        assertThat(result.tokenType).isEqualTo("Bearer")
        assertThat(result.expiresIn).isEqualTo(3600L)
        assertThat(result.user.username).isEqualTo("testuser")
        assertThat(result.user.name).isEqualTo("Test User")
        assertThat(result.user.email).isEqualTo("test@example.com")
        assertThat(result.user.authorities).containsExactly("ROLE_USER")

        then(authenticationManager).should().authenticate(any(UsernamePasswordAuthenticationToken::class.java))
        then(userRepository).should().findByUsernameAndDeletedFalse("testuser")
        then(jwtUtil).should().generateToken(authentication)
        then(jwtUtil).should().generateRefreshToken("testuser")
        then(userRepository).should().save(any(User::class.java))
    }

    @Test
    fun `should throw exception when authentication fails with bad credentials`() {
        // Given
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken::class.java)))
            .willThrow(BadCredentialsException("Bad credentials"))

        // When & Then
        val exception = assertThrows<ApiException> {
            authService.authenticateUser(loginRequest)
        }

        assertThat(exception.errorCode).isEqualTo(ErrorCode.INVALID_CREDENTIALS)
        then(authenticationManager).should().authenticate(any(UsernamePasswordAuthenticationToken::class.java))
        verifyNoInteractions(userRepository, jwtUtil)
    }

    @Test
    fun `should throw exception when user not found after authentication`() {
        // Given
        given(authentication.principal).willReturn(userDetails)
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken::class.java)))
            .willReturn(authentication)
        given(userRepository.findByUsernameAndDeletedFalse("testuser")).willReturn(null)

        // When & Then
        val exception = assertThrows<ApiException> {
            authService.authenticateUser(loginRequest)
        }

        assertThat(exception.errorCode).isEqualTo(ErrorCode.USER_NOT_FOUND)
        then(authenticationManager).should().authenticate(any(UsernamePasswordAuthenticationToken::class.java))
        then(userRepository).should().findByUsernameAndDeletedFalse("testuser")
    }

    @Test
    fun `should refresh token successfully`() {
        // Given
        val refreshToken = "valid-refresh-token"
        given(jwtUtil.validateToken(refreshToken)).willReturn(true)
        given(jwtUtil.getUsernameFromToken(refreshToken)).willReturn("testuser")
        given(userRepository.findByUsernameAndDeletedFalse("testuser"))
            .willReturn(testUser.copy(refreshToken = refreshToken))
        given(jwtUtil.generateTokenFromUsername("testuser")).willReturn("new-access-token")

        // When
        val result = authService.refreshToken(refreshToken)

        // Then
        assertThat(result.accessToken).isEqualTo("new-access-token")
        assertThat(result.tokenType).isEqualTo("Bearer")
        assertThat(result.expiresIn).isEqualTo(3600L)

        then(jwtUtil).should().validateToken(refreshToken)
        then(jwtUtil).should().getUsernameFromToken(refreshToken)
        then(userRepository).should().findByUsernameAndDeletedFalse("testuser")
        then(jwtUtil).should().generateTokenFromUsername("testuser")
    }

    @Test
    fun `should throw exception when refresh token is invalid`() {
        // Given
        val invalidRefreshToken = "invalid-refresh-token"
        given(jwtUtil.validateToken(invalidRefreshToken)).willReturn(false)

        // When & Then
        val exception = assertThrows<ApiException> {
            authService.refreshToken(invalidRefreshToken)
        }

        assertThat(exception.errorCode).isEqualTo(ErrorCode.INVALID_TOKEN)
        assertThat(exception.message).isEqualTo("Invalid refresh token")
        then(jwtUtil).should().validateToken(invalidRefreshToken)
        verifyNoMoreInteractions(jwtUtil)
        verifyNoInteractions(userRepository)
    }

    @Test
    fun `should throw exception when refresh token does not match stored token`() {
        // Given
        val refreshToken = "valid-refresh-token"
        val storedRefreshToken = "different-refresh-token"
        given(jwtUtil.validateToken(refreshToken)).willReturn(true)
        given(jwtUtil.getUsernameFromToken(refreshToken)).willReturn("testuser")
        given(userRepository.findByUsernameAndDeletedFalse("testuser"))
            .willReturn(testUser.copy(refreshToken = storedRefreshToken))

        // When & Then
        val exception = assertThrows<ApiException> {
            authService.refreshToken(refreshToken)
        }

        assertThat(exception.errorCode).isEqualTo(ErrorCode.REFRESH_TOKEN_INVALID)
        assertThat(exception.message).isEqualTo("Refresh token mismatch")
        then(jwtUtil).should().validateToken(refreshToken)
        then(jwtUtil).should().getUsernameFromToken(refreshToken)
        then(userRepository).should().findByUsernameAndDeletedFalse("testuser")
    }

    @Test
    fun `should throw exception when user not found during refresh`() {
        // Given
        val refreshToken = "valid-refresh-token"
        given(jwtUtil.validateToken(refreshToken)).willReturn(true)
        given(jwtUtil.getUsernameFromToken(refreshToken)).willReturn("nonexistent")
        given(userRepository.findByUsernameAndDeletedFalse("nonexistent")).willReturn(null)

        // When & Then
        val exception = assertThrows<ApiException> {
            authService.refreshToken(refreshToken)
        }

        assertThat(exception.errorCode).isEqualTo(ErrorCode.USER_NOT_FOUND)
        then(jwtUtil).should().validateToken(refreshToken)
        then(jwtUtil).should().getUsernameFromToken(refreshToken)
        then(userRepository).should().findByUsernameAndDeletedFalse("nonexistent")
    }

    @Test
    fun `should logout user successfully`() {
        // Given
        given(userRepository.findByUsernameAndDeletedFalse("testuser"))
            .willReturn(testUser.copy(refreshToken = "some-refresh-token"))
        given(userRepository.save(any(User::class.java)))
            .willReturn(testUser.copy(refreshToken = null))

        // When
        authService.logout("testuser")

        // Then
        then(userRepository).should().findByUsernameAndDeletedFalse("testuser")
        then(userRepository).should().save(any(User::class.java))
    }

    @Test
    fun `should handle logout when user not found`() {
        // Given
        given(userRepository.findByUsernameAndDeletedFalse("nonexistent")).willReturn(null)

        // When
        authService.logout("nonexistent")

        // Then
        then(userRepository).should().findByUsernameAndDeletedFalse("nonexistent")
        then(userRepository).shouldHaveNoMoreInteractions()
    }

    @Test
    fun `should handle authentication with invalid principal type`() {
        // Given
        val invalidAuthentication = mock(Authentication::class.java)
        given(invalidAuthentication.principal).willReturn("invalid-principal")
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken::class.java)))
            .willReturn(invalidAuthentication)

        // When & Then
        val exception = assertThrows<ClassCastException> {
            authService.authenticateUser(loginRequest)
        }

        assertThat(exception.message).contains("cannot be cast to")
    }
}
