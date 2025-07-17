package kr.co.bookquiz.api.dto.book

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size

data class BookCreateRequest(
    @field:NotBlank(message = "Title is required")
    @field:Size(max = 255, message = "Title must not exceed 255 characters")
    val title: String,

    @field:NotBlank(message = "ISBN is required")
    @field:Size(max = 30, message = "ISBN must not exceed 30 characters")
    val isbn: String,

    @field:NotBlank(message = "Publisher is required")
    @field:Size(max = 255, message = "Publisher must not exceed 255 characters")
    val publisher: String,

    @field:NotNull(message = "Quiz price is required")
    @field:Positive(message = "Quiz price must be positive")
    val quizPrice: Int,

    @field:Size(max = 1000, message = "Thumbnail URL must not exceed 1000 characters")
    val thumbnail: String? = null,

    val authorIds: List<Long> = emptyList(),
    val translatorIds: List<Long> = emptyList(),
    val illustratorIds: List<Long> = emptyList()
)

data class BookUpdateRequest(
    @field:NotBlank(message = "Title is required")
    @field:Size(max = 255, message = "Title must not exceed 255 characters")
    val title: String,

    @field:NotBlank(message = "ISBN is required")
    @field:Size(max = 30, message = "ISBN must not exceed 30 characters")
    val isbn: String,

    @field:NotBlank(message = "Publisher is required")
    @field:Size(max = 255, message = "Publisher must not exceed 255 characters")
    val publisher: String,

    @field:NotNull(message = "Quiz price is required")
    @field:Positive(message = "Quiz price must be positive")
    val quizPrice: Int,

    @field:Size(max = 1000, message = "Thumbnail URL must not exceed 1000 characters")
    val thumbnail: String? = null,

    val authorIds: List<Long> = emptyList(),
    val translatorIds: List<Long> = emptyList(),
    val illustratorIds: List<Long> = emptyList()
)

data class BookResponse(
    val id: Long?,
    val title: String,
    val isbn: String,
    val publisher: String,
    val quizPrice: Int,
    val thumbnail: String?,
    val authors: List<AuthorResponse>,
    val translators: List<TranslatorResponse>,
    val illustrators: List<IllustratorResponse>
)

data class AuthorResponse(
    val id: Long?,
    val name: String
)

data class TranslatorResponse(
    val id: Long?,
    val name: String
)

data class IllustratorResponse(
    val id: Long?,
    val name: String
)