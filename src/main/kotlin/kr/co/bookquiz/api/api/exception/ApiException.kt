package kr.co.bookquiz.api.api.exception

class ApiException(
    val errorCode: ErrorCode,
    override val message: String? = errorCode.defaultMessage
) : RuntimeException(message)