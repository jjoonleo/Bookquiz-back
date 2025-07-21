package kr.co.bookquiz.api.entity

import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorColumn
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.Transient
import jakarta.persistence.UniqueConstraint
import java.time.LocalDateTime
import kr.co.bookquiz.api.dto.useranswer.MultipleChoiceUserAnswerResponseDto
import kr.co.bookquiz.api.dto.useranswer.SubjectiveUserAnswerResponseDto
import kr.co.bookquiz.api.dto.useranswer.TrueFalseUserAnswerResponseDto
import kr.co.bookquiz.api.dto.useranswer.UserAnswerResponseDto

@Entity
@Table(
        uniqueConstraints =
                [UniqueConstraint(columnNames = ["user_id", "quiz_id", "attempt_number"])]
)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "answer_type")
abstract class UserAnswer<T>(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) open val id: Long? = null,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        open val user: User,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "quiz_id", nullable = false)
        open val quiz: Quiz<*>,
        @Column(nullable = false) open val attemptNumber: Int = 1,
        @Column(nullable = false) open val isCorrect: Boolean = false,
        @Column(nullable = false) open val answeredAt: LocalDateTime = LocalDateTime.now()
) {
    // Declare userAnswer as abstract property in class body
    @get:Transient abstract val userAnswer: T

    abstract fun getAnswerType(): String
    abstract fun toDto(): UserAnswerResponseDto
}

@Entity
@DiscriminatorValue("MULTIPLE_CHOICE")
class MultipleChoiceUserAnswer(
        id: Long? = null,
        user: User,
        quiz: Quiz<*>,
        @Column(nullable = false) override val userAnswer: Int,
        attemptNumber: Int = 1,
        isCorrect: Boolean = false,
        answeredAt: LocalDateTime = LocalDateTime.now()
) : UserAnswer<Int>(id, user, quiz, attemptNumber, isCorrect, answeredAt) {

    override fun getAnswerType(): String = "MULTIPLE_CHOICE"

    override fun toDto(): UserAnswerResponseDto =
            MultipleChoiceUserAnswerResponseDto(
                    id = id,
                    type = "MULTIPLE_CHOICE",
                    username = user.username,
                    quizId = quiz.id ?: 0L,
                    userAnswer = userAnswer,
                    attemptNumber = attemptNumber,
                    isCorrect = isCorrect,
                    answeredAt = answeredAt
            )

    fun copy(
            userAnswer: Int? = null,
            attemptNumber: Int? = null,
            isCorrect: Boolean? = null
    ): MultipleChoiceUserAnswer {
        return MultipleChoiceUserAnswer(
                id = this.id,
                user = this.user,
                quiz = this.quiz,
                userAnswer = userAnswer ?: this.userAnswer,
                attemptNumber = attemptNumber ?: this.attemptNumber,
                isCorrect = isCorrect ?: this.isCorrect,
                answeredAt = this.answeredAt
        )
    }
}

@Entity
@DiscriminatorValue("SUBJECTIVE")
class SubjectiveUserAnswer(
        id: Long? = null,
        user: User,
        quiz: Quiz<*>,
        userAnswer: String,
        attemptNumber: Int = 1,
        isCorrect: Boolean = false,
        answeredAt: LocalDateTime = LocalDateTime.now()
) : UserAnswer<String>(id, user, quiz, attemptNumber, isCorrect, answeredAt) {

    @Column(nullable = false, columnDefinition = "TEXT")
    override val userAnswer: String = userAnswer

    override fun getAnswerType(): String = "SUBJECTIVE"

    override fun toDto(): UserAnswerResponseDto =
            SubjectiveUserAnswerResponseDto(
                    id = id,
                    type = "SUBJECTIVE",
                    username = user.username,
                    quizId = quiz.id ?: 0L,
                    userAnswer = userAnswer,
                    attemptNumber = attemptNumber,
                    isCorrect = isCorrect,
                    answeredAt = answeredAt
            )

    fun copy(
            userAnswer: String? = null,
            attemptNumber: Int? = null,
            isCorrect: Boolean? = null
    ): SubjectiveUserAnswer {
        return SubjectiveUserAnswer(
                id = this.id,
                user = this.user,
                quiz = this.quiz,
                userAnswer = userAnswer ?: this.userAnswer,
                attemptNumber = attemptNumber ?: this.attemptNumber,
                isCorrect = isCorrect ?: this.isCorrect,
                answeredAt = this.answeredAt
        )
    }
}

@Entity
@DiscriminatorValue("TRUE_FALSE")
class TrueFalseUserAnswer(
        id: Long? = null,
        user: User,
        quiz: Quiz<*>,
        userAnswer: Boolean,
        attemptNumber: Int = 1,
        isCorrect: Boolean = false,
        answeredAt: LocalDateTime = LocalDateTime.now()
) : UserAnswer<Boolean>(id, user, quiz, attemptNumber, isCorrect, answeredAt) {

    @Column(nullable = false) override val userAnswer: Boolean = userAnswer

    override fun getAnswerType(): String = "TRUE_FALSE"

    override fun toDto(): UserAnswerResponseDto =
            TrueFalseUserAnswerResponseDto(
                    id = id,
                    type = "TRUE_FALSE",
                    username = user.username,
                    quizId = quiz.id ?: 0L,
                    userAnswer = userAnswer,
                    attemptNumber = attemptNumber,
                    isCorrect = isCorrect,
                    answeredAt = answeredAt
            )

    fun copy(
            userAnswer: Boolean? = null,
            attemptNumber: Int? = null,
            isCorrect: Boolean? = null
    ): TrueFalseUserAnswer {
        return TrueFalseUserAnswer(
                id = this.id,
                user = this.user,
                quiz = this.quiz,
                userAnswer = userAnswer ?: this.userAnswer,
                attemptNumber = attemptNumber ?: this.attemptNumber,
                isCorrect = isCorrect ?: this.isCorrect,
                answeredAt = this.answeredAt
        )
    }
}
