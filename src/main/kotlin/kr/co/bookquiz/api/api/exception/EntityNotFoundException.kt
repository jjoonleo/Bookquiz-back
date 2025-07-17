package kr.co.bookquiz.api.api.exception

class EntityNotFoundException(
    val entityType: String,
    val missingIds: List<String>,
    errorCode: ErrorCode
) : ApiException(errorCode, "$entityType not found: ${missingIds.joinToString(", ")}")