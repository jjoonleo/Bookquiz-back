package kr.co.bookquiz.api.api.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.AuthenticationException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.net.URI

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ApiException::class)
    fun handleApiException(e: ApiException): ProblemDetail {
        val status = when (e.errorCode) {
            ErrorCode.USER_NOT_FOUND -> HttpStatus.NOT_FOUND
            ErrorCode.INVALID_CREDENTIALS, ErrorCode.AUTHENTICATION_FAILED -> HttpStatus.UNAUTHORIZED
            ErrorCode.ACCESS_DENIED -> HttpStatus.FORBIDDEN
            ErrorCode.INVALID_TOKEN, ErrorCode.REFRESH_TOKEN_INVALID -> HttpStatus.UNAUTHORIZED
            ErrorCode.EMAIL_DUPLICATE -> HttpStatus.CONFLICT
            ErrorCode.INVALID_REQUEST -> HttpStatus.BAD_REQUEST
            else -> HttpStatus.INTERNAL_SERVER_ERROR
        }

        return ProblemDetail.forStatusAndDetail(status, e.message ?: "").apply {
            title = "Application Error"
            type = URI.create("https://example.com/errors/${e.errorCode.name.lowercase()}")
            setProperty("errorCode", e.errorCode.code)
        }
    }

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentialsException(e: BadCredentialsException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ErrorCode.INVALID_CREDENTIALS.defaultMessage).apply {
            title = "Authentication Error"
            type = URI.create("https://example.com/errors/invalid_credentials")
            setProperty("errorCode", ErrorCode.INVALID_CREDENTIALS.code)
        }
    }

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(e: AuthenticationException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ErrorCode.AUTHENTICATION_FAILED.defaultMessage).apply {
            title = "Authentication Error"
            type = URI.create("https://example.com/errors/authentication_failed")
            setProperty("errorCode", ErrorCode.AUTHENTICATION_FAILED.code)
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(e: MethodArgumentNotValidException): ProblemDetail {
        val errors = e.bindingResult.allErrors.map { error ->
            when (error) {
                is FieldError -> "${error.field}: ${error.defaultMessage}"
                else -> error.defaultMessage ?: "Validation error"
            }
        }

        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed").apply {
            title = "Validation Error"
            type = URI.create("https://example.com/errors/validation")
            setProperty("errorCode", ErrorCode.INVALID_REQUEST.code)
            setProperty("validationErrors", errors)
        }
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralException(e: Exception): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, e.message ?: "Internal error").apply {
            title = "Internal Server Error"
            type = URI.create("https://example.com/errors/internal")
            setProperty("errorCode", ErrorCode.INTERNAL_ERROR.code)
        }
    }
}