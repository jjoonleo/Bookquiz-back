package kr.co.bookquiz.api.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID
import kr.co.bookquiz.api.entity.enums.TossPaymentMethod
import kr.co.bookquiz.api.entity.enums.TossPaymentStatus
import kr.co.bookquiz.api.entity.enums.TossPaymentType

@Entity
@Table(name = "toss_payments")
data class TossPayment(
        @GeneratedValue(strategy = GenerationType.UUID)
        @Column(name = "id", nullable = false)
        @Id
        val id: UUID? = null,
        @Column(name = "payment_key", nullable = false, unique = true) val paymentKey: String,
        @OneToOne
        @JoinColumn(name = "order_id", nullable = false, columnDefinition = "UUID")
        val order: Order,
        @Column(nullable = false) val amount: Long = 0,
        @Enumerated(value = EnumType.STRING)
        @Column(nullable = false)
        val method: TossPaymentMethod,
        @Enumerated(value = EnumType.STRING)
        @Column(nullable = false)
        val status: TossPaymentStatus,
        @Enumerated(value = EnumType.STRING) @Column(nullable = false) val type: TossPaymentType,
        @Column(name = "requested_at", nullable = false) val requestedAt: LocalDateTime,
        @Column(name = "approved_at", nullable = false) val approvedAt: LocalDateTime? = null
)
