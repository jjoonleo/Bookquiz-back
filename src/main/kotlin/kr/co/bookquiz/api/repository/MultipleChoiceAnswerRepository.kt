package kr.co.bookquiz.api.repository

import kr.co.bookquiz.api.entity.MultipleChoiceAnswerEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MultipleChoiceAnswerRepository : JpaRepository<MultipleChoiceAnswerEntity, Long> {
  fun findByQuizId(quizId: Long): MultipleChoiceAnswerEntity?
}
