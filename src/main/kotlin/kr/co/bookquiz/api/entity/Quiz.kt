package kr.co.bookquiz.api.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import kr.co.bookquiz.api.dto.quiz.*

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "quiz_type")
abstract class Quiz(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) open val id: Long? = null,
        @Column(nullable = false) open val title: String = "",
        @Column open val explanation: String? = null,
        @Column open val hint: String? = null,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "book_id", nullable = false)
        open val book: Book,
        @Column(nullable = false) open val createdAt: LocalDateTime = LocalDateTime.now()
) {
        // Declare answer as abstract property in class body
        abstract val answer: Any

        abstract fun getQuizType(): String
        abstract fun toDto(): QuizResponseDto
}

@Entity
@DiscriminatorValue("MULTIPLE_CHOICE")
class MultipleChoiceQuiz(
        id: Long? = null,
        title: String,
        @Column(nullable = false) override val answer: Int,
        explanation: String? = null,
        hint: String? = null,
        book: Book,
        @ElementCollection(fetch = FetchType.EAGER)
        @CollectionTable(name = "quiz_options", joinColumns = [JoinColumn(name = "quiz_id")])
        @Column(name = "option_text")
        val options: List<String> = emptyList(),
        createdAt: LocalDateTime = LocalDateTime.now()
) : Quiz(id, title, explanation, hint, book, createdAt) {

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
                        options = options
                )

        fun copy(
                title: String? = null,
                answer: Int? = null,
                explanation: String? = null,
                hint: String? = null,
                options: List<String>? = null
        ): MultipleChoiceQuiz {
                return MultipleChoiceQuiz(
                        id = this.id,
                        title = title ?: this.title,
                        answer = answer ?: this.answer,
                        explanation = explanation ?: this.explanation,
                        hint = hint ?: this.hint,
                        book = this.book,
                        options = options ?: this.options,
                        createdAt = this.createdAt
                )
        }
}

@Entity
@DiscriminatorValue("SUBJECTIVE")
class SubjectiveQuiz(
        id: Long? = null,
        title: String,
        answer: String,
        explanation: String? = null,
        hint: String? = null,
        book: Book,
        createdAt: LocalDateTime = LocalDateTime.now()
) : Quiz(id, title, explanation, hint, book, createdAt) {

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
@DiscriminatorValue("TRUE_FALSE")
class TrueFalseQuiz(
        id: Long? = null,
        title: String,
        @Column(nullable = false) override val answer: Boolean,
        explanation: String? = null,
        hint: String? = null,
        book: Book,
        createdAt: LocalDateTime = LocalDateTime.now()
) : Quiz(id, title, explanation, hint, book, createdAt) {

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
