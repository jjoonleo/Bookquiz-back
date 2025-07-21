package kr.co.bookquiz.api.entity.enums

enum class TossPaymentStatus {
  READY, // 결제를 생성하면 가지게 되는 초기 상태
  IN_PROGRESS, // 결제수단 정보와 소유자 인증 완료
  WAITING_FOR_DEPOSIT, // 가상계좌 결제에서 입금 대기
  DONE, // 결제 승인 완료
  CANCELED, // 결제 취소
  PARTIAL_CANCELED, // 결제 부분 취소
  ABORTED, // 결제 승인 실패
  EXPIRED // 결제 유효 시간 만료
}
