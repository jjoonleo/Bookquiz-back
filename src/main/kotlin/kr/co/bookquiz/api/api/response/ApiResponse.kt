package kr.co.bookquiz.api.api.response

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null
)