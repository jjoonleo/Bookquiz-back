class ApiException(
    val errorCode: ErrorCode,
    override val message: String? = errorCode.defaultMessage
) : RuntimeException(message)