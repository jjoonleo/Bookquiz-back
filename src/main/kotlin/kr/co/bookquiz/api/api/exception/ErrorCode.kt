enum class ErrorCode(val code: String, val defaultMessage: String) {
    USER_NOT_FOUND("E1001", "User not found"),
    EMAIL_DUPLICATE("E1002", "Email already exists"),
    INVALID_REQUEST("E1003", "Invalid request parameters"),
    INTERNAL_ERROR("E9999", "Internal server error")
}
