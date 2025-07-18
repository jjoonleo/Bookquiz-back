package kr.co.bookquiz.api.repository

import kr.co.bookquiz.api.entity.SubjectiveAnswerEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SubjectiveAnswerRepository : JpaRepository<SubjectiveAnswerEntity, Long> {
  fun findByQuizId(quizId: Long): SubjectiveAnswerEntity?
}
