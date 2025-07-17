package kr.co.bookquiz.api.api.exception

enum class ErrorCode(val code: String, val defaultMessage: String) {
    USER_NOT_FOUND("E1001", "User not found"),
    EMAIL_DUPLICATE("E1002", "Email already exists"),
    USERNAME_DUPLICATE("E1003", "Username already exists"),
    INVALID_REQUEST("E1004", "Invalid request parameters"),
    INVALID_CREDENTIALS("E1005", "Invalid username or password"),
    INVALID_TOKEN("E1006", "Invalid or expired token"),
    REFRESH_TOKEN_INVALID("E1007", "Invalid refresh token"),
    AUTHENTICATION_FAILED("E1008", "Authentication failed"),
    ACCESS_DENIED("E1009", "Access denied"),
    PASSWORD_MISMATCH("E1010", "Password and confirm password do not match"),
    WEAK_PASSWORD("E1011", "Password does not meet security requirements"),
    PHONE_NUMBER_DUPLICATE("E1012", "Phone number already exists"),
    AUTHOR_NOT_FOUND("E1013", "Author not found"),
    TRANSLATOR_NOT_FOUND("E1014", "Translator not found"),
    ILLUSTRATOR_NOT_FOUND("E1015", "Illustrator not found"),
    BOOK_NOT_FOUND("E1016", "Book not found"),
    INTERNAL_ERROR("E9999", "Internal server error")
}