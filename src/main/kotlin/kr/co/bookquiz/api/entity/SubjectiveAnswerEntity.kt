package kr.co.bookquiz.api.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "subjective_answers")
@PrimaryKeyJoinColumn(name = "id")
class SubjectiveAnswerEntity(
        @ElementCollection
        @CollectionTable(
                name = "subjective_possible_answers",
                joinColumns = [JoinColumn(name = "answer_id")]
        )
        @Column(name = "possible_answer", nullable = false)
        val possibleAnswers: List<String> = emptyList(),
        @Column(name = "case_sensitive", nullable = false) val caseSensitive: Boolean = false,
        @Column(name = "max_words") val maxWords: Int? = null,
        id: Long = 0,
        quiz: Quiz,
        createdAt: LocalDateTime = LocalDateTime.now()
) : AnswerEntity(id, quiz, createdAt)
