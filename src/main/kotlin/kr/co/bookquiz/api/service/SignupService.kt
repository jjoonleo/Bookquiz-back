package kr.co.bookquiz.api.service

import kr.co.bookquiz.api.api.exception.ApiException
import kr.co.bookquiz.api.api.exception.ErrorCode
import kr.co.bookquiz.api.dto.auth.SignupRequest
import kr.co.bookquiz.api.dto.auth.SignupResponse
import kr.co.bookquiz.api.entity.Authority
import kr.co.bookquiz.api.entity.User
import kr.co.bookquiz.api.repository.AuthorityRepository
import kr.co.bookquiz.api.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class SignupService(
    private val userRepository: UserRepository,
    private val authorityRepository: AuthorityRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun registerUser(signupRequest: SignupRequest): SignupResponse {
        // Validate password confirmation
        if (signupRequest.password != signupRequest.confirmPassword) {
            throw ApiException(ErrorCode.PASSWORD_MISMATCH)
        }

        // Check if username already exists
        if (userRepository.existsByUsernameAndDeletedFalse(signupRequest.username)) {
            throw ApiException(ErrorCode.USERNAME_DUPLICATE)
        }

        // Check if email already exists
        if (userRepository.existsByEmailAndDeletedFalse(signupRequest.email)) {
            throw ApiException(ErrorCode.EMAIL_DUPLICATE)
        }

        // Check if phone number already exists
        if (userRepository.existsByPhoneNumberAndDeletedFalse(signupRequest.phoneNumber)) {
            throw ApiException(ErrorCode.PHONE_NUMBER_DUPLICATE)
        }

        try {
            // Create new user
            val user = User(
                username = signupRequest.username,
                name = signupRequest.name,
                password = passwordEncoder.encode(signupRequest.password),
                email = signupRequest.email,
                phoneNumber = signupRequest.phoneNumber,
                dateOfBirth = signupRequest.dateOfBirth,
                province = signupRequest.province,
                deleted = false,
                grade = signupRequest.grade,
                enabled = true, // You might want to set this to false and enable after email verification
                gender = signupRequest.gender,
                refreshToken = null,
                lastLogin = null,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                authorities = emptySet()
            )

            // Save user
            val savedUser = userRepository.save(user)

            // Create default authority (ROLE_USER)
            val authority = Authority(
                authority = "ROLE_USER",
                user = savedUser
            )
            authorityRepository.save(authority)

            return SignupResponse(
                username = savedUser.username,
                name = savedUser.name,
                email = savedUser.email
            )
        } catch (e: ApiException) {
            throw e // Re-throw ApiException
        } catch (e: Exception) {
            throw ApiException(ErrorCode.INTERNAL_ERROR, "Failed to register user: ${e.message}")
        }
    }

    fun checkUsernameAvailability(username: String): Boolean {
        return !userRepository.existsByUsernameAndDeletedFalse(username)
    }

    fun checkEmailAvailability(email: String): Boolean {
        return !userRepository.existsByEmailAndDeletedFalse(email)
    }

    fun checkPhoneNumberAvailability(phoneNumber: String): Boolean {
        return !userRepository.existsByPhoneNumberAndDeletedFalse(phoneNumber)
    }
}