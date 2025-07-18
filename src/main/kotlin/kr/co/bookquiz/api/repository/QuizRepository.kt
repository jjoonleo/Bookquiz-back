package kr.co.bookquiz.api.repository

import kr.co.bookquiz.api.entity.Quiz
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface QuizRepository : JpaRepository<Quiz, Long> {
  fun findByBookId(bookId: Long): List<Quiz>
}
