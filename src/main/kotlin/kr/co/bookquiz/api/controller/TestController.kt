package kr.co.bookquiz.api.controller

import kr.co.bookquiz.api.api.response.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/test")
class TestController {

    @GetMapping("/protected")
    fun protectedEndpoint(authentication: Authentication): ResponseEntity<ApiResponse<Map<String, Any>>> {
        val data = mapOf(
            "username" to authentication.name,
            "authorities" to authentication.authorities.map { it.authority }
        )
        val response = ApiResponse(
            success = true,
            message = "Access granted to protected endpoint",
            data = data
        )
        return ResponseEntity.ok(response)
    }

    @GetMapping("/public")
    fun publicEndpoint(): ResponseEntity<ApiResponse<Map<String, String>>> {
        val data = mapOf("message" to "This is a public endpoint")
        val response = ApiResponse(
            success = true,
            message = "Public endpoint accessed successfully",
            data = data
        )
        return ResponseEntity.ok(response)
    }
}