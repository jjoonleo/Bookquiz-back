package kr.co.bookquiz.api.service

import java.time.LocalDateTime
import java.util.Base64
import java.util.UUID
import kr.co.bookquiz.api.entity.TossPayment
import kr.co.bookquiz.api.entity.enums.TossPaymentMethod
import kr.co.bookquiz.api.entity.enums.TossPaymentStatus
import kr.co.bookquiz.api.entity.enums.TossPaymentType
import kr.co.bookquiz.api.repository.OrderRepository
import kr.co.bookquiz.api.repository.TossPaymentRepository
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate

@Service
class TossPaymentService(
        private val tossPaymentRepository: TossPaymentRepository,
        private val orderRepository: OrderRepository
) {
  // Temporarily use the secret key assignment as in the Java example
  private val widgetSecretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6"

  @Transactional
  fun confirmPayment(paymentKey: String, orderId: String, amount: Int): TossPayment {
    val url = "https://api.tosspayments.com/v1/payments/confirm"
    val restTemplate = RestTemplate()

    // Use the widgetSecretKey directly for encoding
    val encodedKey = Base64.getEncoder().encodeToString((widgetSecretKey + ":").toByteArray())
    val authorization = "Basic $encodedKey"

    val headers =
            HttpHeaders().apply {
              contentType = MediaType.APPLICATION_JSON
              set("Authorization", authorization)
            }

    val body = mapOf("paymentKey" to paymentKey, "orderId" to orderId, "amount" to amount)
    val request = HttpEntity(body, headers)
    val response = restTemplate.postForEntity(url, request, Map::class.java)
    val order =
            orderRepository.findById(UUID.fromString(orderId)).orElseThrow {
              IllegalArgumentException("Order not found: $orderId")
            }

    if (response.statusCode == HttpStatus.OK) {
      val responseBody = response.body!!
      val tossPayment =
              TossPayment(
                      paymentKey = responseBody["paymentKey"] as String,
                      order = order,
                      amount = (responseBody["totalAmount"] as Number).toLong(),
                      status = TossPaymentStatus.valueOf(responseBody["status"] as String),
                      method = TossPaymentMethod.valueOf(responseBody["method"] as String),
                      type = TossPaymentType.valueOf(responseBody["type"] as String),
                      requestedAt = LocalDateTime.parse(responseBody["requestedAt"] as String),
                      approvedAt = responseBody["approvedAt"]?.let { LocalDateTime.parse(it as String) }
              )
      return tossPaymentRepository.save(tossPayment)
    } else {
      throw RuntimeException("Toss payment failed: ${response.body}")
    }
  }
}
