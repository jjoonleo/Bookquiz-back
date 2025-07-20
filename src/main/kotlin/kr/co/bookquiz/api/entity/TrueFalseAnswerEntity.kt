package kr.co.bookquiz.api.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.PrimaryKeyJoinColumn
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "true_false_answers")
@PrimaryKeyJoinColumn(name = "id")
class TrueFalseAnswerEntity(
    @Column(name = "correct_answer", nullable = false) val correctAnswer: Boolean,
    id: Long = 0,
    quiz: Quiz,
    createdAt: LocalDateTime = LocalDateTime.now()
) : AnswerEntity(id, quiz, createdAt)