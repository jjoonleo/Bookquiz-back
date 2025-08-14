package kr.co.bookquiz.api.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "user_books")
data class UserBook(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long = 0,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "username", nullable = false)
        val user: User,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "book_id", nullable = false)
        val book: Book,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "order_id", nullable = true, columnDefinition = "UUID")
        val order: Order?,
        @Column(name = "max_attempts", nullable = false) val maxAttempts: Int = 3,
        @Column(name = "current_attempts", nullable = false) val currentAttempts: Int = 0,
        @Column(name = "owned_at", nullable = false)
        val ownedAt: LocalDateTime = LocalDateTime.now(),
        @Column(name = "updated_at", nullable = false)
        val updatedAt: LocalDateTime = LocalDateTime.now()
)
