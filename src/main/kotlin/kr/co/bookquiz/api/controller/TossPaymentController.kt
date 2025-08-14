package kr.co.bookquiz.api.controller

import kr.co.bookquiz.api.dto.tosspayment.TossPaymentConfirmRequestDto
import kr.co.bookquiz.api.dto.tosspayment.TossPaymentResponseDto
import kr.co.bookquiz.api.service.TossPaymentService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/payments")
class TossPaymentController(private val tossPaymentService: TossPaymentService) {
  @PostMapping("/confirm")
  fun confirmPayment(
          @RequestBody req: TossPaymentConfirmRequestDto
  ): ResponseEntity<TossPaymentResponseDto> {
    val payment = tossPaymentService.confirmPayment(req.paymentKey, req.orderId, req.amount)
    val responseDto =
            TossPaymentResponseDto(
                    paymentKey = payment.paymentKey,
                    orderId = payment.order.id.toString(),
                    amount = payment.amount,
                    status = payment.status
                    )
    return ResponseEntity.ok(responseDto)
  }
}
