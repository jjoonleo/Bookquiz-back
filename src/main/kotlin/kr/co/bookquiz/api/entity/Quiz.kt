package kr.co.bookquiz.api.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "quizzes")
data class Quiz(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long = 0,
        @Column(nullable = false) val title: String,
        @Column(name = "question_type", nullable = false)
        val questionType: String, // "MULTIPLE_CHOICE", "SUBJECTIVE", "TRUE_FALSE"
        @Column(nullable = false) val points: Int = 1,
        val explanation: String? = null,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "book_id", nullable = false)
        @JsonIgnore
        val book: Book,
        @Column(name = "created_at", nullable = false)
        val createdAt: LocalDateTime = LocalDateTime.now()
)
