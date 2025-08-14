package kr.co.bookquiz.api.dto.book

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size

data class BookCreateRequest(
        @field:NotBlank(message = "Title is required")
        @field:Size(max = 255, message = "Title must not exceed 255 characters")
        val title: String,
        @field:Size(max = 500, message = "Subtitle must not exceed 500 characters")
        val subtitle: String? = null,
        @field:NotBlank(message = "ISBN is required")
        @field:Size(max = 30, message = "ISBN must not exceed 30 characters")
        val isbn: String,
        @field:NotBlank(message = "Publisher is required")
        @field:Size(max = 255, message = "Publisher must not exceed 255 characters")
        val publisher: String,
        @field:NotNull(message = "Quiz price is required")
        @field:Positive(message = "Quiz price must be positive")
        val quizPrice: Int,
        @field:Positive(message = "Max attempt must be positive") val maxAttempt: Int = 3,
        @field:Size(max = 1000, message = "Thumbnail URL must not exceed 1000 characters")
        val thumbnail: String? = null,
        @field:NotBlank(message = "Authors are required")
        @field:Size(max = 2000, message = "Authors must not exceed 2000 characters")
        val authors: String,
        @field:Size(max = 2000, message = "Translators must not exceed 2000 characters")
        val translators: String = "",
        @field:Size(max = 2000, message = "Illustrators must not exceed 2000 characters")
        val illustrators: String = ""
)

data class BookUpdateRequest(
        @field:NotBlank(message = "Title is required")
        @field:Size(max = 255, message = "Title must not exceed 255 characters")
        val title: String,
        @field:Size(max = 500, message = "Subtitle must not exceed 500 characters")
        val subtitle: String? = null,
        @field:NotBlank(message = "ISBN is required")
        @field:Size(max = 30, message = "ISBN must not exceed 30 characters")
        val isbn: String,
        @field:NotBlank(message = "Publisher is required")
        @field:Size(max = 255, message = "Publisher must not exceed 255 characters")
        val publisher: String,
        @field:NotNull(message = "Quiz price is required")
        @field:Positive(message = "Quiz price must be positive")
        val quizPrice: Int,
        @field:Positive(message = "Max attempt must be positive") val maxAttempt: Int = 3,
        @field:Size(max = 1000, message = "Thumbnail URL must not exceed 1000 characters")
        val thumbnail: String? = null,
        @field:NotBlank(message = "Authors are required")
        @field:Size(max = 2000, message = "Authors must not exceed 2000 characters")
        val authors: String,
        @field:Size(max = 2000, message = "Translators must not exceed 2000 characters")
        val translators: String = "",
        @field:Size(max = 2000, message = "Illustrators must not exceed 2000 characters")
        val illustrators: String = ""
)

data class BookResponse(
        val id: Long?,
        val title: String,
        val subtitle: String?,
        val isbn: String,
        val publisher: String,
        val quizPrice: Int,
        val maxAttempt: Int,
        val thumbnail: String?,
        val authors: String,
        val translators: String,
        val illustrators: String
)
