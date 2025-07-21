package kr.co.bookquiz.api.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.time.LocalDateTime
import kr.co.bookquiz.api.entity.enums.TossPaymentMethod
import kr.co.bookquiz.api.entity.enums.TossPaymentStatus
import kr.co.bookquiz.api.entity.enums.TossPaymentType

@Entity
@Table(name = "toss_payments")
class TossPayment {
    @Column(name = "id", nullable = false) @Id lateinit var id: ByteArray

    @Column(name = "payment_key", nullable = false, unique = true) lateinit var paymentKey: String

    // 토스내부에서 관리하는 별도의 orderId가 존재함
    @Column(nullable = false) lateinit var tossOrderId: String

    @OneToOne @JoinColumn(name = "order_id", nullable = false) lateinit var order: Order

    @Column(nullable = false) var amount: Long = 0

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    lateinit var method: TossPaymentMethod

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    lateinit var status: TossPaymentStatus

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    lateinit var type: TossPaymentType

    @Column(nullable = false) lateinit var requestedAt: LocalDateTime

    var approvedAt: LocalDateTime? = null
}
