package kr.co.bookquiz.api.service

import kr.co.bookquiz.api.api.exception.ApiException
import kr.co.bookquiz.api.api.exception.ErrorCode
import kr.co.bookquiz.api.dto.auth.SignupRequest
import kr.co.bookquiz.api.entity.Authority
import kr.co.bookquiz.api.entity.Grade
import kr.co.bookquiz.api.entity.Province
import kr.co.bookquiz.api.entity.User
import kr.co.bookquiz.api.repository.AuthorityRepository
import kr.co.bookquiz.api.repository.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDateTime
import org.assertj.core.api.Assertions.assertThat


@ExtendWith(MockitoExtension::class)
class SignupServiceTest {

    private lateinit var signupService: SignupService
    private lateinit var userRepository: UserRepository
    private lateinit var authorityRepository: AuthorityRepository
    private lateinit var passwordEncoder: PasswordEncoder

    private lateinit var defaultAuthority: Authority
    private lateinit var validSignupRequest: SignupRequest

    @BeforeEach
    fun setUp() {
        userRepository = mock(UserRepository::class.java)
        authorityRepository = mock(AuthorityRepository::class.java)
        passwordEncoder = mock(PasswordEncoder::class.java)
        signupService = SignupService(userRepository, authorityRepository, passwordEncoder)

        defaultAuthority = Authority(
            id = 1L,
            name = "ROLE_USER",
            description = "Default user role",
            createdAt = LocalDateTime.now()
        )

        validSignupRequest = SignupRequest(
            username = "testuser",
            name = "Test User",
            password = "TestPassword123!",
            confirmPassword = "TestPassword123!",
            email = "test@example.com",
            phoneNumber = "+82101234567",
            dateOfBirth = LocalDateTime.of(1990, 1, 1, 0, 0),
            province = Province.SEOUL,
            grade = Grade.COLLEGE_GENERAL,
            gender = true
        )
    }

    @Test
    fun `should register user successfully`() {
        // Given
        given(userRepository.existsByUsernameAndDeletedFalse("testuser")).willReturn(false)
        given(userRepository.existsByEmailAndDeletedFalse("test@example.com")).willReturn(false)
        given(userRepository.existsByPhoneNumberAndDeletedFalse("+82101234567")).willReturn(false)
        given(authorityRepository.findByName("ROLE_USER")).willReturn(defaultAuthority)
        given(passwordEncoder.encode("TestPassword123!")).willReturn("encodedPassword")
        
        val savedUser = User(
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
            authorities = setOf(defaultAuthority)
        )
        
        given(userRepository.save(any(User::class.java))).willReturn(savedUser)

        // When
        val result = signupService.registerUser(validSignupRequest)

        // Then
        assertThat(result.username).isEqualTo("testuser")
        assertThat(result.name).isEqualTo("Test User")
        assertThat(result.email).isEqualTo("test@example.com")

        then(userRepository).should().existsByUsernameAndDeletedFalse("testuser")
        then(userRepository).should().existsByEmailAndDeletedFalse("test@example.com")
        then(userRepository).should().existsByPhoneNumberAndDeletedFalse("+82101234567")
        then(passwordEncoder).should().encode("TestPassword123!")
        then(userRepository).should().save(any(User::class.java))
    }

    @Test
    fun `should throw exception when passwords do not match`() {
        // Given
        val requestWithMismatchedPasswords = validSignupRequest.copy(
            confirmPassword = "DifferentPassword123!"
        )

        // When & Then
        val exception = assertThrows<ApiException> {
            signupService.registerUser(requestWithMismatchedPasswords)
        }

        assertThat(exception.errorCode).isEqualTo(ErrorCode.PASSWORD_MISMATCH)
        verifyNoInteractions(userRepository, passwordEncoder)
    }

    @Test
    fun `should throw exception when username already exists`() {
        // Given
        given(userRepository.existsByUsernameAndDeletedFalse("testuser")).willReturn(true)

        // When & Then
        val exception = assertThrows<ApiException> {
            signupService.registerUser(validSignupRequest)
        }

        assertThat(exception.errorCode).isEqualTo(ErrorCode.USERNAME_DUPLICATE)
        then(userRepository).should().existsByUsernameAndDeletedFalse("testuser")
        then(userRepository).shouldHaveNoMoreInteractions()
    }

    @Test
    fun `should throw exception when email already exists`() {
        // Given
        given(userRepository.existsByUsernameAndDeletedFalse("testuser")).willReturn(false)
        given(userRepository.existsByEmailAndDeletedFalse("test@example.com")).willReturn(true)

        // When & Then
        val exception = assertThrows<ApiException> {
            signupService.registerUser(validSignupRequest)
        }

        assertThat(exception.errorCode).isEqualTo(ErrorCode.EMAIL_DUPLICATE)
        then(userRepository).should().existsByUsernameAndDeletedFalse("testuser")
        then(userRepository).should().existsByEmailAndDeletedFalse("test@example.com")
    }

    @Test
    fun `should throw exception when phone number already exists`() {
        // Given
        given(userRepository.existsByUsernameAndDeletedFalse("testuser")).willReturn(false)
        given(userRepository.existsByEmailAndDeletedFalse("test@example.com")).willReturn(false)
        given(userRepository.existsByPhoneNumberAndDeletedFalse("+82101234567")).willReturn(true)

        // When & Then
        val exception = assertThrows<ApiException> {
            signupService.registerUser(validSignupRequest)
        }

        assertThat(exception.errorCode).isEqualTo(ErrorCode.PHONE_NUMBER_DUPLICATE)
        then(userRepository).should().existsByUsernameAndDeletedFalse("testuser")
        then(userRepository).should().existsByEmailAndDeletedFalse("test@example.com")
        then(userRepository).should().existsByPhoneNumberAndDeletedFalse("+82101234567")
    }

    @Test
    fun `should create default authority if not exists`() {
        // Given
        given(userRepository.existsByUsernameAndDeletedFalse("testuser")).willReturn(false)
        given(userRepository.existsByEmailAndDeletedFalse("test@example.com")).willReturn(false)
        given(userRepository.existsByPhoneNumberAndDeletedFalse("+82101234567")).willReturn(false)
        given(authorityRepository.findByName("ROLE_USER")).willReturn(null)
        given(authorityRepository.save(any(Authority::class.java))).willReturn(defaultAuthority)
        given(passwordEncoder.encode("TestPassword123!")).willReturn("encodedPassword")
        
        val savedUser = User(
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
            authorities = setOf(defaultAuthority)
        )
        
        given(userRepository.save(any(User::class.java))).willReturn(savedUser)

        // When
        signupService.registerUser(validSignupRequest)

        // Then
        then(authorityRepository).should().findByName("ROLE_USER")
        then(authorityRepository).should().save(any(Authority::class.java))
        then(userRepository).should().save(any(User::class.java))
    }

    @Test
    fun `should check username availability correctly`() {
        // Given
        given(userRepository.existsByUsernameAndDeletedFalse("available")).willReturn(false)
        given(userRepository.existsByUsernameAndDeletedFalse("taken")).willReturn(true)

        // When
        val availableResult = signupService.checkUsernameAvailability("available")
        val takenResult = signupService.checkUsernameAvailability("taken")

        // Then
        assertThat(availableResult).isTrue()
        assertThat(takenResult).isFalse()
    }

    @Test
    fun `should check email availability correctly`() {
        // Given
        given(userRepository.existsByEmailAndDeletedFalse("available@example.com")).willReturn(false)
        given(userRepository.existsByEmailAndDeletedFalse("taken@example.com")).willReturn(true)

        // When
        val availableResult = signupService.checkEmailAvailability("available@example.com")
        val takenResult = signupService.checkEmailAvailability("taken@example.com")

        // Then
        assertThat(availableResult).isTrue()
        assertThat(takenResult).isFalse()
    }

    @Test
    fun `should check phone number availability correctly`() {
        // Given
        given(userRepository.existsByPhoneNumberAndDeletedFalse("+82101111111")).willReturn(false)
        given(userRepository.existsByPhoneNumberAndDeletedFalse("+82102222222")).willReturn(true)

        // When
        val availableResult = signupService.checkPhoneNumberAvailability("+82101111111")
        val takenResult = signupService.checkPhoneNumberAvailability("+82102222222")

        // Then
        assertThat(availableResult).isTrue()
        assertThat(takenResult).isFalse()
    }

    @Test
    fun `should handle exception during user save`() {
        // Given
        given(userRepository.existsByUsernameAndDeletedFalse("testuser")).willReturn(false)
        given(userRepository.existsByEmailAndDeletedFalse("test@example.com")).willReturn(false)
        given(userRepository.existsByPhoneNumberAndDeletedFalse("+82101234567")).willReturn(false)
        given(authorityRepository.findByName("ROLE_USER")).willReturn(defaultAuthority)
        given(passwordEncoder.encode("TestPassword123!")).willReturn("encodedPassword")
        given(userRepository.save(any(User::class.java))).willThrow(RuntimeException("Database error"))

        // When & Then
        val exception = assertThrows<ApiException> {
            signupService.registerUser(validSignupRequest)
        }

        assertThat(exception.errorCode).isEqualTo(ErrorCode.INTERNAL_ERROR)
        assertThat(exception.message).contains("Failed to register user")
    }
}
