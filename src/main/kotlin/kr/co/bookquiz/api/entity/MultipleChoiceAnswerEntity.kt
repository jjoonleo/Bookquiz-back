package kr.co.bookquiz.api.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "multiple_choice_answers")
@PrimaryKeyJoinColumn(name = "id")
class MultipleChoiceAnswerEntity(
        @Column(name = "correct_index", nullable = false) val correctIndex: Int,
        @ElementCollection
        @CollectionTable(
                name = "multiple_choice_options",
                joinColumns = [JoinColumn(name = "answer_id")]
        )
        @Column(name = "option_text", nullable = false)
        val options: List<String> = emptyList(),
        id: Long = 0,
        quiz: Quiz,
        createdAt: LocalDateTime = LocalDateTime.now()
) : AnswerEntity(id, quiz, createdAt)
