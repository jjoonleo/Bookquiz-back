package kr.co.bookquiz.api.entity.enums

enum class TossPaymentType(val displayName: String) {
  NORMAL("일반결제"),
  BILLING("자동결제"),
  BRANDPAY("브랜드페이")
}
