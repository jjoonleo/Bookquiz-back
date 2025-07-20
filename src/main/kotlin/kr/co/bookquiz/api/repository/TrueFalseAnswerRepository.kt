package kr.co.bookquiz.api.repository

import kr.co.bookquiz.api.entity.TrueFalseAnswerEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TrueFalseAnswerRepository : JpaRepository<TrueFalseAnswerEntity, Long> {
    fun findByQuizId(quizId: Long): TrueFalseAnswerEntity?
}