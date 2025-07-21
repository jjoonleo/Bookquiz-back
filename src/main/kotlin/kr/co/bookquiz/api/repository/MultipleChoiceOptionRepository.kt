package kr.co.bookquiz.api.repository

import kr.co.bookquiz.api.entity.MultipleChoiceOption
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MultipleChoiceOptionRepository : JpaRepository<MultipleChoiceOption, Long> {
    fun findByQuizIdOrderByOptionIndex(quizId: Long): List<MultipleChoiceOption>
}