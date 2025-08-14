package kr.co.bookquiz.api.entity

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "order_items")
data class OrderItem(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long = 0,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "order_id", columnDefinition = "UUID")
        val order: Order,
        @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "book_id") val book: Book,
        val price: Int
)
