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
        @field:Positive(message = "Max attempt must be positive") val maxAttempt: Int = 3,
        @field:Size(max = 1000, message = "Thumbnail URL must not exceed 1000 characters")
        val thumbnail: String? = null,
        @field:NotNull(message = "Authors are required")
        @field:Size(min = 1, message = "At least one author is required")
        val authorNames: List<@NotBlank(message = "Author name cannot be blank") String> =
                emptyList(),
        val translatorNames: List<@NotBlank(message = "Translator name cannot be blank") String> =
                emptyList(),
        val illustratorNames: List<@NotBlank(message = "Illustrator name cannot be blank") String> =
                emptyList()
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
        @field:Positive(message = "Max attempt must be positive") val maxAttempt: Int = 3,
        @field:Size(max = 1000, message = "Thumbnail URL must not exceed 1000 characters")
        val thumbnail: String? = null,
        @field:NotNull(message = "Authors are required")
        @field:Size(min = 1, message = "At least one author is required")
        val authorNames: List<@NotBlank(message = "Author name cannot be blank") String> =
                emptyList(),
        val translatorNames: List<@NotBlank(message = "Translator name cannot be blank") String> =
                emptyList(),
        val illustratorNames: List<@NotBlank(message = "Illustrator name cannot be blank") String> =
                emptyList()
)

data class BookResponse(
        val id: Long?,
        val title: String,
        val isbn: String,
        val publisher: String,
        val quizPrice: Int,
        val maxAttempt: Int,
        val thumbnail: String?,
        val authors: List<PersonResponse>,
        val translators: List<PersonResponse>,
        val illustrators: List<PersonResponse>
)

data class PersonResponse(val id: Long?, val name: String)
