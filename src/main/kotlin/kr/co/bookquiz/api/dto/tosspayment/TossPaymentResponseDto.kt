package kr.co.bookquiz.api.dto.tosspayment

import kr.co.bookquiz.api.entity.enums.TossPaymentStatus

data class TossPaymentResponseDto(
    val paymentKey: String,
    val orderId: String,
    val amount: Long,
    val status: TossPaymentStatus,
// Add more fields as needed from TossPayment entity
)
