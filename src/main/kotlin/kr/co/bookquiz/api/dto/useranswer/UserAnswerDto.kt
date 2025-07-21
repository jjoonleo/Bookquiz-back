package kr.co.bookquiz.api.dto.useranswer

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.time.LocalDateTime
import kr.co.bookquiz.api.entity.*

// Create DTOs
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
        JsonSubTypes.Type(
                value = CreateMultipleChoiceUserAnswerDto::class,
                name = "MULTIPLE_CHOICE"
        ),
        JsonSubTypes.Type(value = CreateSubjectiveUserAnswerDto::class, name = "SUBJECTIVE"),
        JsonSubTypes.Type(value = CreateTrueFalseUserAnswerDto::class, name = "TRUE_FALSE")
)
abstract class CreateUserAnswerDto {
  abstract val type: String
  abstract val quizId: Long
  abstract val userAnswer: Any

  abstract fun toEntity(
          user: User,
          quiz: Quiz<*>,
          isCorrect: Boolean,
          attemptNumber: Int,
          answeredAt: LocalDateTime,
  ): UserAnswer<*>
}

data class CreateMultipleChoiceUserAnswerDto(
        override val type: String = "MULTIPLE_CHOICE",
        override val quizId: Long,
        override val userAnswer: Int
) : CreateUserAnswerDto() {
  override fun toEntity(
          user: User,
          quiz: Quiz<*>,
          isCorrect: Boolean,
          attemptNumber: Int,
          answeredAt: LocalDateTime,
  ): UserAnswer<*> =
          MultipleChoiceUserAnswer(
                  user = user,
                  quiz = quiz,
                  userAnswer = userAnswer,
                  attemptNumber = attemptNumber,
                  isCorrect = isCorrect,
                  answeredAt = answeredAt
          )
}

data class CreateSubjectiveUserAnswerDto(
        override val type: String = "SUBJECTIVE",
        override val quizId: Long,
        override val userAnswer: String
) : CreateUserAnswerDto() {
  override fun toEntity(
          user: User,
          quiz: Quiz<*>,
          isCorrect: Boolean,
          attemptNumber: Int,
          answeredAt: LocalDateTime
  ): UserAnswer<*> =
          SubjectiveUserAnswer(
                  user = user,
                  quiz = quiz,
                  userAnswer = userAnswer,
                  attemptNumber = attemptNumber,
                  isCorrect = isCorrect,
                  answeredAt = answeredAt
          )
}

data class CreateTrueFalseUserAnswerDto(
        override val type: String = "TRUE_FALSE",
        override val quizId: Long,
        override val userAnswer: Boolean
) : CreateUserAnswerDto() {
  override fun toEntity(
          user: User,
          quiz: Quiz<*>,
          isCorrect: Boolean,
          attemptNumber: Int,
          answeredAt: LocalDateTime
  ): UserAnswer<*> =
          TrueFalseUserAnswer(
                  user = user,
                  quiz = quiz,
                  userAnswer = userAnswer,
                  attemptNumber = attemptNumber,
                  isCorrect = isCorrect,
                  answeredAt = answeredAt
          )
}

// Update DTOs
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
        JsonSubTypes.Type(
                value = UpdateMultipleChoiceUserAnswerDto::class,
                name = "MULTIPLE_CHOICE"
        ),
        JsonSubTypes.Type(value = UpdateSubjectiveUserAnswerDto::class, name = "SUBJECTIVE"),
        JsonSubTypes.Type(value = UpdateTrueFalseUserAnswerDto::class, name = "TRUE_FALSE")
)
abstract class UpdateUserAnswerDto {
  abstract val type: String
  abstract val attemptNumber: Int?
  abstract val isCorrect: Boolean?

  abstract fun updateEntity(existingUserAnswer: UserAnswer<*>): UserAnswer<*>
}

data class UpdateMultipleChoiceUserAnswerDto(
        override val type: String = "MULTIPLE_CHOICE",
        override val attemptNumber: Int? = null,
        override val isCorrect: Boolean? = null,
        val userAnswer: Int? = null
) : UpdateUserAnswerDto() {
  override fun updateEntity(existingUserAnswer: UserAnswer<*>): UserAnswer<*> {
    if (existingUserAnswer !is MultipleChoiceUserAnswer) {
      throw IllegalArgumentException("UserAnswer type mismatch for update")
    }
    return existingUserAnswer.copy(
            userAnswer = userAnswer ?: existingUserAnswer.userAnswer,
            attemptNumber = attemptNumber ?: existingUserAnswer.attemptNumber,
            isCorrect = isCorrect ?: existingUserAnswer.isCorrect
    )
  }
}

data class UpdateSubjectiveUserAnswerDto(
        override val type: String = "SUBJECTIVE",
        override val attemptNumber: Int? = null,
        override val isCorrect: Boolean? = null,
        val userAnswer: String? = null
) : UpdateUserAnswerDto() {
  override fun updateEntity(existingUserAnswer: UserAnswer<*>): UserAnswer<*> {
    if (existingUserAnswer !is SubjectiveUserAnswer) {
      throw IllegalArgumentException("UserAnswer type mismatch for update")
    }
    return existingUserAnswer.copy(
            userAnswer = userAnswer ?: existingUserAnswer.userAnswer,
            attemptNumber = attemptNumber ?: existingUserAnswer.attemptNumber,
            isCorrect = isCorrect ?: existingUserAnswer.isCorrect
    )
  }
}

data class UpdateTrueFalseUserAnswerDto(
        override val type: String = "TRUE_FALSE",
        override val attemptNumber: Int? = null,
        override val isCorrect: Boolean? = null,
        val userAnswer: Boolean? = null
) : UpdateUserAnswerDto() {
  override fun updateEntity(existingUserAnswer: UserAnswer<*>): UserAnswer<*> {
    if (existingUserAnswer !is TrueFalseUserAnswer) {
      throw IllegalArgumentException("UserAnswer type mismatch for update")
    }
    return existingUserAnswer.copy(
            userAnswer = userAnswer ?: existingUserAnswer.userAnswer,
            attemptNumber = attemptNumber ?: existingUserAnswer.attemptNumber,
            isCorrect = isCorrect ?: existingUserAnswer.isCorrect
    )
  }
}

// Response DTOs
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
        JsonSubTypes.Type(
                value = MultipleChoiceUserAnswerResponseDto::class,
                name = "MULTIPLE_CHOICE"
        ),
        JsonSubTypes.Type(value = SubjectiveUserAnswerResponseDto::class, name = "SUBJECTIVE"),
        JsonSubTypes.Type(value = TrueFalseUserAnswerResponseDto::class, name = "TRUE_FALSE")
)
abstract class UserAnswerResponseDto {
  abstract val id: Long?
  abstract val type: String
  abstract val username: String
  abstract val quizId: Long
  abstract val attemptNumber: Int
  abstract val isCorrect: Boolean
  abstract val answeredAt: LocalDateTime
}

data class MultipleChoiceUserAnswerResponseDto(
        override val id: Long?,
        override val type: String = "MULTIPLE_CHOICE",
        override val username: String,
        override val quizId: Long,
        override val attemptNumber: Int,
        override val isCorrect: Boolean,
        override val answeredAt: LocalDateTime,
        val userAnswer: Int
) : UserAnswerResponseDto()

data class SubjectiveUserAnswerResponseDto(
        override val id: Long?,
        override val type: String = "SUBJECTIVE",
        override val username: String,
        override val quizId: Long,
        override val attemptNumber: Int,
        override val isCorrect: Boolean,
        override val answeredAt: LocalDateTime,
        val userAnswer: String
) : UserAnswerResponseDto()

data class TrueFalseUserAnswerResponseDto(
        override val id: Long?,
        override val type: String = "TRUE_FALSE",
        override val username: String,
        override val quizId: Long,
        override val attemptNumber: Int,
        override val isCorrect: Boolean,
        override val answeredAt: LocalDateTime,
        val userAnswer: Boolean
) : UserAnswerResponseDto()

// Filter DTO for getting user answers
data class UserAnswerFilterDto(
        val type: String? = null,
        val username: String? = null,
        val quizId: Long? = null,
        val attemptNumber: Int? = null,
        val isCorrect: Boolean? = null,
        val fromDate: LocalDateTime? = null,
        val toDate: LocalDateTime? = null
)

// Summary DTOs for analytics
data class UserQuizSummaryDto(
        val username: String,
        val quizId: Long,
        val totalAttempts: Int,
        val correctAttempts: Int,
        val bestAttempt: Int,
        val latestAttempt: UserAnswerResponseDto,
        val firstAttemptAt: LocalDateTime,
        val lastAttemptAt: LocalDateTime
)

data class UserAnswerStatsDto(
        val username: String,
        val totalQuizzesAttempted: Int,
        val totalAttempts: Int,
        val correctAnswers: Int,
        val accuracy: Double,
        val averageAttemptsPerQuiz: Double
)
