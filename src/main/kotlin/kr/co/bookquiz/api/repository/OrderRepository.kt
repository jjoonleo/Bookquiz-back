package kr.co.bookquiz.api.repository

import kr.co.bookquiz.api.entity.Order
import org.springframework.data.jpa.repository.JpaRepository

interface OrderRepository : JpaRepository<Order, java.util.UUID>
