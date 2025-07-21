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
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.Transient
import java.time.LocalDateTime
import kr.co.bookquiz.api.dto.quiz.MultipleChoiceQuizResponseDto
import kr.co.bookquiz.api.dto.quiz.QuizResponseDto
import kr.co.bookquiz.api.dto.quiz.SubjectiveQuizResponseDto
import kr.co.bookquiz.api.dto.quiz.TrueFalseQuizResponseDto

@Entity
@Table(name = "quizzes")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "quiz_type")
abstract class Quiz<T>(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) open val id: Long? = null,
        @Column(nullable = false) open val title: String = "",
        @Column open val explanation: String? = null,
        @Column open val hint: String? = null,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "book_id", nullable = false)
        open val book: Book,
        @Column(name = "created_at", nullable = false)
        open val createdAt: LocalDateTime = LocalDateTime.now()
) {
    // Declare answer as abstract property in class body
    @get:Transient abstract val answer: T

    abstract fun getQuizType(): String
    abstract fun toDto(): QuizResponseDto

    abstract fun isCorrectAnswer(userAnswer: Any): Boolean
}

@Entity
@Table(name = "multiple_choice_options")
class MultipleChoiceOption(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
        @Column(name = "option_text", nullable = false) val optionText: String,
        @Column(name = "option_index", nullable = false) val optionIndex: Int,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "quiz_id", nullable = false)
        val quiz: MultipleChoiceQuiz
)

@Entity
@Table(name = "multiple_choice_quizzes")
@DiscriminatorValue("MULTIPLE_CHOICE")
class MultipleChoiceQuiz(
        id: Long? = null,
        title: String,
        @Column(nullable = false) override val answer: Int,
        explanation: String? = null,
        hint: String? = null,
        book: Book,
        @OneToMany(
                mappedBy = "quiz",
                fetch = FetchType.EAGER,
                cascade = [jakarta.persistence.CascadeType.ALL],
                orphanRemoval = true
        )
        val options: MutableList<MultipleChoiceOption> = mutableListOf(),
        createdAt: LocalDateTime = LocalDateTime.now()
) : Quiz<Int>(id, title, explanation, hint, book, createdAt) {

    override fun getQuizType(): String = "MULTIPLE_CHOICE"

    override fun toDto(): QuizResponseDto =
            MultipleChoiceQuizResponseDto(
                    id = id,
                    title = title,
                    explanation = explanation,
                    hint = hint,
                    bookId = book.id ?: 0L,
                    createdAt = createdAt,
                    answer = answer,
                    options = options.sortedBy { it.optionIndex }.map { it.optionText }
            )

    override fun isCorrectAnswer(userAnswer: Any): Boolean {
        return userAnswer is Int && userAnswer == answer
    }

    fun copy(
            title: String? = null,
            answer: Int? = null,
            explanation: String? = null,
            hint: String? = null,
            options: List<String>? = null
    ): MultipleChoiceQuiz {
        val newQuiz =
                MultipleChoiceQuiz(
                        id = this.id,
                        title = title ?: this.title,
                        answer = answer ?: this.answer,
                        explanation = explanation ?: this.explanation,
                        hint = hint ?: this.hint,
                        book = this.book,
                        createdAt = this.createdAt
                )

        if (options != null) {
            newQuiz.options.clear()
            options.forEachIndexed { index, optionText ->
                newQuiz.options.add(
                        MultipleChoiceOption(
                                optionText = optionText,
                                optionIndex = index,
                                quiz = newQuiz
                        )
                )
            }
        } else {
            // Copy existing options
            this.options.forEach { option ->
                newQuiz.options.add(
                        MultipleChoiceOption(
                                id = option.id,
                                optionText = option.optionText,
                                optionIndex = option.optionIndex,
                                quiz = newQuiz
                        )
                )
            }
        }

        return newQuiz
    }
}

@Entity
@Table(name = "subjective_quizzes")
@DiscriminatorValue("SUBJECTIVE")
class SubjectiveQuiz(
        id: Long? = null,
        title: String,
        answer: String,
        explanation: String? = null,
        hint: String? = null,
        book: Book,
        createdAt: LocalDateTime = LocalDateTime.now()
) : Quiz<String>(id, title, explanation, hint, book, createdAt) {

    @Column(nullable = false, columnDefinition = "TEXT") override val answer: String = answer

    override fun getQuizType(): String = "SUBJECTIVE"

    override fun toDto(): QuizResponseDto =
            SubjectiveQuizResponseDto(
                    id = id,
                    title = title,
                    explanation = explanation,
                    hint = hint,
                    bookId = book.id ?: 0L,
                    createdAt = createdAt,
                    answer = answer
            )

    override fun isCorrectAnswer(userAnswer: Any): Boolean {
        return userAnswer is String && userAnswer.trim().equals(answer.trim(), ignoreCase = true)
    }

    fun copy(
            title: String? = null,
            answer: String? = null,
            explanation: String? = null,
            hint: String? = null
    ): SubjectiveQuiz {
        return SubjectiveQuiz(
                id = this.id,
                title = title ?: this.title,
                answer = answer ?: this.answer,
                explanation = explanation ?: this.explanation,
                hint = hint ?: this.hint,
                book = this.book,
                createdAt = this.createdAt
        )
    }
}

@Entity
@Table(name = "true_false_quizzes")
@DiscriminatorValue("TRUE_FALSE")
class TrueFalseQuiz(
        id: Long? = null,
        title: String,
        answer: Boolean,
        explanation: String? = null,
        hint: String? = null,
        book: Book,
        createdAt: LocalDateTime = LocalDateTime.now()
) : Quiz<Boolean>(id, title, explanation, hint, book, createdAt) {

    @Column(nullable = false) override val answer: Boolean = answer

    override fun getQuizType(): String = "TRUE_FALSE"

    override fun toDto(): QuizResponseDto =
            TrueFalseQuizResponseDto(
                    id = id,
                    title = title,
                    explanation = explanation,
                    hint = hint,
                    bookId = book.id ?: 0L,
                    createdAt = createdAt,
                    answer = answer
            )

    override fun isCorrectAnswer(userAnswer: Any): Boolean {
        return userAnswer is Boolean && userAnswer == answer
    }

    fun copy(
            title: String? = null,
            answer: Boolean? = null,
            explanation: String? = null,
            hint: String? = null
    ): TrueFalseQuiz {
        return TrueFalseQuiz(
                id = this.id,
                title = title ?: this.title,
                answer = answer ?: this.answer,
                explanation = explanation ?: this.explanation,
                hint = hint ?: this.hint,
                book = this.book,
                createdAt = this.createdAt
        )
    }
}
