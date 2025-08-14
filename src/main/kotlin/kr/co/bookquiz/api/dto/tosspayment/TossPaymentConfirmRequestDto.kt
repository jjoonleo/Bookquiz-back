package kr.co.bookquiz.api.dto.tosspayment

data class TossPaymentConfirmRequestDto(
        val paymentKey: String,
        val orderId: String,
        val amount: Int
)
