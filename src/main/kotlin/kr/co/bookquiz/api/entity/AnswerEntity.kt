package kr.co.bookquiz.api.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "answers")
abstract class AnswerEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) open val id: Long = 0,
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false, unique = true)
    open val quiz: Quiz,
    @Column(name = "created_at", nullable = false)
    open val createdAt: LocalDateTime = LocalDateTime.now()
)