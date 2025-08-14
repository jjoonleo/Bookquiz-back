package kr.co.bookquiz.api.repository

import kr.co.bookquiz.api.entity.TossPayment
import org.springframework.data.jpa.repository.JpaRepository

interface TossPaymentRepository : JpaRepository<TossPayment, Long>
