import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.net.URI

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ApiException::class)
    fun handleApiException(e: ApiException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message ?: "").apply {
            title = "Application Error"
            type = URI.create("https://example.com/errors/${e.errorCode.name.lowercase()}")
            setProperty("errorCode", e.errorCode.code)
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
