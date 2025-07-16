package kr.co.bookquiz.api.service

import kr.co.bookquiz.api.api.exception.ApiException
import kr.co.bookquiz.api.api.exception.ErrorCode
import kr.co.bookquiz.api.dto.auth.LoginRequest
import kr.co.bookquiz.api.dto.auth.LoginResponse
import kr.co.bookquiz.api.dto.auth.RefreshTokenResponse
import kr.co.bookquiz.api.dto.auth.UserInfo
import kr.co.bookquiz.api.repository.UserRepository
import kr.co.bookquiz.api.security.JwtUtil
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AuthService(
    private val authenticationManager: AuthenticationManager,
    private val userRepository: UserRepository,
    private val jwtUtil: JwtUtil,
    @Value("\${jwt.expiration:86400}") private val jwtExpirationInSeconds: Int
) {

    fun authenticateUser(loginRequest: LoginRequest): LoginResponse {
        try {
            val authentication: Authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    loginRequest.username,
                    loginRequest.password
                )
            )

            val userDetails = authentication.principal as UserDetails
            val accessToken = jwtUtil.generateToken(authentication)
            val refreshToken = jwtUtil.generateRefreshToken(userDetails.username)

            // Update refresh token in database
            val user = userRepository.findByUsernameAndDeletedFalseWithAuthorities(userDetails.username)
                ?: throw ApiException(ErrorCode.USER_NOT_FOUND)
            user.refreshToken = refreshToken
            userRepository.save(user)

            val userInfo = UserInfo(
                username = userDetails.username,
                name = user.name,
                email = user.email,
                authorities = user.authorities.map { authority -> authority.name }
            )

            return LoginResponse(
                accessToken = accessToken,
                refreshToken = refreshToken,
                expiresIn = jwtExpirationInSeconds.toLong(),
                user = userInfo
            )
        } catch (e: BadCredentialsException) {
            throw ApiException(ErrorCode.INVALID_CREDENTIALS)
        } catch (e: AuthenticationException) {
            throw ApiException(ErrorCode.AUTHENTICATION_FAILED)
        }
    }

    fun refreshToken(refreshToken: String): RefreshTokenResponse {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw ApiException(ErrorCode.INVALID_TOKEN, "Invalid refresh token")
        }

        val username = jwtUtil.getUsernameFromToken(refreshToken)
        val user = userRepository.findByUsernameAndDeletedFalse(username)
            ?: throw ApiException(ErrorCode.USER_NOT_FOUND)

        if (user.refreshToken != refreshToken) {
            throw ApiException(ErrorCode.REFRESH_TOKEN_INVALID, "Refresh token mismatch")
        }

        val newAccessToken = jwtUtil.generateTokenFromUsername(username)

        return RefreshTokenResponse(
            accessToken = newAccessToken,
            expiresIn = jwtExpirationInSeconds.toLong()
        )
    }

    fun logout(username: String) {
        val user = userRepository.findByUsernameAndDeletedFalse(username)
        user?.let {
            it.refreshToken = null
            userRepository.save(it)
        }
    }
}