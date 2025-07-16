package kr.co.bookquiz.api.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    var secret: String = "bookquiz-secret-key-that-is-at-least-256-bits-long-for-security",
    var expiration: Int = 86400, // 24 hours in seconds
    var refreshExpiration: Int = 604800 // 7 days in seconds
)