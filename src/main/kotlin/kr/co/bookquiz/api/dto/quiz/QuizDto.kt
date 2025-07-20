package kr.co.bookquiz.api.dto.quiz

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import kr.co.bookquiz.api.entity.MultipleChoiceQuiz
import kr.co.bookquiz.api.entity.Quiz
import kr.co.bookquiz.api.entity.SubjectiveQuiz
import kr.co.bookquiz.api.entity.TrueFalseQuiz
import java.time.LocalDateTime

// Base DTO for all quiz operations
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = CreateMultipleChoiceQuizDto::class, name = "MULTIPLE_CHOICE"),
    JsonSubTypes.Type(value = CreateSubjectiveQuizDto::class, name = "SUBJECTIVE"),
    JsonSubTypes.Type(value = CreateTrueFalseQuizDto::class, name = "TRUE_FALSE")
)
abstract class CreateQuizDto {
    abstract val type: String
    abstract val title: String
    abstract val explanation: String?
    abstract val hint: String?
    abstract val bookId: Long

    abstract fun toEntity(book: kr.co.bookquiz.api.entity.Book): kr.co.bookquiz.api.entity.Quiz
}

data class CreateMultipleChoiceQuizDto(
    override val type: String = "MULTIPLE_CHOICE",
    override val title: String,
    override val explanation: String? = null,
    override val hint: String? = null,
    override val bookId: Long,
    val answer: Int,
    val options: List<String>
) : CreateQuizDto() {
    override fun toEntity(
        book: kr.co.bookquiz.api.entity.Book
    ): kr.co.bookquiz.api.entity.Quiz =
        kr.co.bookquiz.api.entity.MultipleChoiceQuiz(
            title = title,
            answer = answer,
            explanation = explanation,
            hint = hint,
            book = book,
            options = options
        )
}

data class CreateSubjectiveQuizDto(
    override val type: String = "SUBJECTIVE",
    override val title: String,
    override val explanation: String? = null,
    override val hint: String? = null,
    override val bookId: Long,
    val answer: String
) : CreateQuizDto() {
    override fun toEntity(
        book: kr.co.bookquiz.api.entity.Book
    ): kr.co.bookquiz.api.entity.Quiz =
        kr.co.bookquiz.api.entity.SubjectiveQuiz(
            title = title,
            answer = answer,
            explanation = explanation,
            hint = hint,
            book = book
        )
}

data class CreateTrueFalseQuizDto(
    override val type: String = "TRUE_FALSE",
    override val title: String,
    override val explanation: String? = null,
    override val hint: String? = null,
    override val bookId: Long,
    val answer: Boolean
) : CreateQuizDto() {
    override fun toEntity(
        book: kr.co.bookquiz.api.entity.Book
    ): kr.co.bookquiz.api.entity.Quiz =
        kr.co.bookquiz.api.entity.TrueFalseQuiz(
            title = title,
            answer = answer,
            explanation = explanation,
            hint = hint,
            book = book
        )
}

// Update DTOs
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = UpdateMultipleChoiceQuizDto::class, name = "MULTIPLE_CHOICE"),
    JsonSubTypes.Type(value = UpdateSubjectiveQuizDto::class, name = "SUBJECTIVE"),
    JsonSubTypes.Type(value = UpdateTrueFalseQuizDto::class, name = "TRUE_FALSE")
)
abstract class UpdateQuizDto {
    abstract val type: String
    abstract val title: String?
    abstract val explanation: String?
    abstract val hint: String?

    abstract fun updateEntity(existingQuiz: Quiz): Quiz
}

data class UpdateMultipleChoiceQuizDto(
    override val type: String = "MULTIPLE_CHOICE",
    override val title: String? = null,
    override val explanation: String? = null,
    override val hint: String? = null,
    val answer: Int? = null,
    val options: List<String>? = null
) : UpdateQuizDto() {
    override fun updateEntity(existingQuiz: Quiz): Quiz {
        if (existingQuiz !is MultipleChoiceQuiz) {
            throw IllegalArgumentException("Quiz type mismatch for update")
        }
        return existingQuiz.copy(
            title = title ?: existingQuiz.title,
            answer = answer ?: existingQuiz.answer,
            explanation = explanation,
            hint = hint,
            options = options ?: existingQuiz.options
        )
    }
}

data class UpdateSubjectiveQuizDto(
    override val type: String = "SUBJECTIVE",
    override val title: String? = null,
    override val explanation: String? = null,
    override val hint: String? = null,
    val answer: String? = null
) : UpdateQuizDto() {
    override fun updateEntity(existingQuiz: Quiz): Quiz {
        if (existingQuiz !is SubjectiveQuiz) {
            throw IllegalArgumentException("Quiz type mismatch for update")
        }
        return existingQuiz.copy(
            title = title ?: existingQuiz.title,
            answer = answer ?: existingQuiz.answer,
            explanation = explanation,
            hint = hint
        )
    }
}

data class UpdateTrueFalseQuizDto(
    override val type: String = "TRUE_FALSE",
    override val title: String? = null,
    override val explanation: String? = null,
    override val hint: String? = null,
    val answer: Boolean? = null
) : UpdateQuizDto() {
    override fun updateEntity(existingQuiz: Quiz): Quiz {
        if (existingQuiz !is TrueFalseQuiz) {
            throw IllegalArgumentException("Quiz type mismatch for update")
        }
        return existingQuiz.copy(
            title = title ?: existingQuiz.title,
            answer = answer ?: existingQuiz.answer,
            explanation = explanation,
            hint = hint
        )
    }
}

// Response DTOs
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = MultipleChoiceQuizResponseDto::class, name = "MULTIPLE_CHOICE"),
    JsonSubTypes.Type(value = SubjectiveQuizResponseDto::class, name = "SUBJECTIVE"),
    JsonSubTypes.Type(value = TrueFalseQuizResponseDto::class, name = "TRUE_FALSE")
)
abstract class QuizResponseDto {
    abstract val id: Long?
    abstract val type: String
    abstract val title: String
    abstract val explanation: String?
    abstract val hint: String?
    abstract val bookId: Long
    abstract val createdAt: LocalDateTime
}

data class MultipleChoiceQuizResponseDto(
    override val id: Long?,
    override val type: String = "MULTIPLE_CHOICE",
    override val title: String,
    override val explanation: String?,
    override val hint: String?,
    override val bookId: Long,
    override val createdAt: LocalDateTime,
    val answer: Int,
    val options: List<String>
) : QuizResponseDto()

data class SubjectiveQuizResponseDto(
    override val id: Long?,
    override val type: String = "SUBJECTIVE",
    override val title: String,
    override val explanation: String?,
    override val hint: String?,
    override val bookId: Long,
    override val createdAt: LocalDateTime,
    val answer: String
) : QuizResponseDto()

data class TrueFalseQuizResponseDto(
    override val id: Long?,
    override val type: String = "TRUE_FALSE",
    override val title: String,
    override val explanation: String?,
    override val hint: String?,
    override val bookId: Long,
    override val createdAt: LocalDateTime,
    val answer: Boolean
) : QuizResponseDto()

// Filter DTO for getting quizzes
data class QuizFilterDto(
    val type: String? = null,
    val bookId: Long? = null,
    val title: String? = null
)