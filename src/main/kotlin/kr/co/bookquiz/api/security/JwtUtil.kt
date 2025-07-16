package kr.co.bookquiz.api.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SecurityException
import kr.co.bookquiz.api.api.exception.ApiException
import kr.co.bookquiz.api.api.exception.ErrorCode
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtUtil {

    @Value("\${jwt.secret:bookquiz-secret-key-that-is-at-least-256-bits-long-for-security}")
    private lateinit var jwtSecret: String

    @Value("\${jwt.expiration:86400}") // 24 hours in seconds
    private val jwtExpirationInSeconds: Int = 86400

    @Value("\${jwt.refresh-expiration:604800}") // 7 days in seconds
    private val refreshTokenExpirationInSeconds: Int = 604800

    private val key: SecretKey by lazy {
        Keys.hmacShaKeyFor(jwtSecret.toByteArray())
    }

    fun generateToken(authentication: Authentication): String {
        try {
            val userPrincipal = authentication.principal as? UserDetails
                ?: throw ApiException(ErrorCode.AUTHENTICATION_FAILED, "Invalid user principal")
            return generateTokenFromUsername(userPrincipal.username)
        } catch (e: ApiException) {
            throw e // Re-throw ApiException
        } catch (e: Exception) {
            throw ApiException(ErrorCode.INTERNAL_ERROR, "Failed to generate JWT token: ${e.message}")
        }
    }

    fun generateTokenFromUsername(username: String): String {
        try {
            if (username.isBlank()) {
                throw ApiException(ErrorCode.INVALID_REQUEST, "Username cannot be empty")
            }
            return Jwts.builder()
                .subject(username)
                .issuedAt(Date())
                .expiration(Date(Date().time + jwtExpirationInSeconds * 1000))
                .signWith(key)
                .compact()
        } catch (e: ApiException) {
            throw e // Re-throw ApiException
        } catch (e: Exception) {
            throw ApiException(ErrorCode.INTERNAL_ERROR, "Failed to generate JWT token: ${e.message}")
        }
    }

    fun generateRefreshToken(username: String): String {
        try {
            if (username.isBlank()) {
                throw ApiException(ErrorCode.INVALID_REQUEST, "Username cannot be empty")
            }
            return Jwts.builder()
                .subject(username)
                .issuedAt(Date())
                .expiration(Date(Date().time + refreshTokenExpirationInSeconds * 1000))
                .signWith(key)
                .compact()
        } catch (e: ApiException) {
            throw e // Re-throw ApiException
        } catch (e: Exception) {
            throw ApiException(ErrorCode.INTERNAL_ERROR, "Failed to generate refresh token: ${e.message}")
        }
    }

    fun getUsernameFromToken(token: String): String {
        try {
            return getClaimsFromToken(token).subject
        } catch (e: ExpiredJwtException) {
            throw ApiException(ErrorCode.INVALID_TOKEN, "JWT token has expired")
        } catch (e: MalformedJwtException) {
            throw ApiException(ErrorCode.INVALID_TOKEN, "Malformed JWT token")
        } catch (e: SecurityException) {
            throw ApiException(ErrorCode.INVALID_TOKEN, "Invalid JWT signature")
        } catch (e: UnsupportedJwtException) {
            throw ApiException(ErrorCode.INVALID_TOKEN, "Unsupported JWT token")
        } catch (e: IllegalArgumentException) {
            throw ApiException(ErrorCode.INVALID_TOKEN, "JWT token is empty or null")
        } catch (e: Exception) {
            throw ApiException(ErrorCode.INVALID_TOKEN, "Failed to parse JWT token")
        }
    }

    fun validateToken(token: String): Boolean {
        try {
            if (token.isBlank()) {
                return false
            }
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token)
            return true
        } catch (e: SecurityException) {
            // Invalid JWT signature - log but don't throw for validation method
            return false
        } catch (e: MalformedJwtException) {
            // Invalid JWT token - log but don't throw for validation method
            return false
        } catch (e: ExpiredJwtException) {
            // JWT token is expired - log but don't throw for validation method
            return false
        } catch (e: UnsupportedJwtException) {
            // JWT token is unsupported - log but don't throw for validation method
            return false
        } catch (e: IllegalArgumentException) {
            // JWT claims string is empty - log but don't throw for validation method
            return false
        } catch (e: Exception) {
            // Any other exception - log but don't throw for validation method
            return false
        }
    }

    fun isTokenExpired(token: String): Boolean {
        return try {
            if (token.isBlank()) {
                throw ApiException(ErrorCode.INVALID_TOKEN, "Token is empty or null")
            }
            val claims = getClaimsFromToken(token)
            claims.expiration.before(Date())
        } catch (e: ExpiredJwtException) {
            true
        } catch (e: ApiException) {
            throw e // Re-throw ApiException
        } catch (e: Exception) {
            throw ApiException(ErrorCode.INVALID_TOKEN, "Failed to check token expiration: ${e.message}")
        }
    }

    private fun getClaimsFromToken(token: String): Claims {
        try {
            if (token.isBlank()) {
                throw ApiException(ErrorCode.INVALID_TOKEN, "Token is empty or null")
            }
            return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (e: ExpiredJwtException) {
            throw ApiException(ErrorCode.INVALID_TOKEN, "JWT token has expired")
        } catch (e: MalformedJwtException) {
            throw ApiException(ErrorCode.INVALID_TOKEN, "Malformed JWT token")
        } catch (e: SecurityException) {
            throw ApiException(ErrorCode.INVALID_TOKEN, "Invalid JWT signature")
        } catch (e: UnsupportedJwtException) {
            throw ApiException(ErrorCode.INVALID_TOKEN, "Unsupported JWT token")
        } catch (e: IllegalArgumentException) {
            throw ApiException(ErrorCode.INVALID_TOKEN, "JWT token is empty or null")
        } catch (e: ApiException) {
            throw e // Re-throw ApiException
        } catch (e: Exception) {
            throw ApiException(ErrorCode.INVALID_TOKEN, "Failed to parse JWT token: ${e.message}")
        }
    }
}