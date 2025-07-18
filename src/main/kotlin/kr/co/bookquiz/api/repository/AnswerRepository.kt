package kr.co.bookquiz.api.repository

import kr.co.bookquiz.api.entity.AnswerEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AnswerRepository : JpaRepository<AnswerEntity, Long> {
  fun findByQuizId(quizId: Long): AnswerEntity?
}
