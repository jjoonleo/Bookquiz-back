package kr.co.bookquiz.api.dto.quiz

import java.time.LocalDateTime

data class QuizResponseDto(
        val id: Long,
        val title: String,
        val questionType: String,
        val points: Int,
        val explanation: String?,
        val createdAt: LocalDateTime
) {
  companion object {
    fun from(quiz: kr.co.bookquiz.api.entity.Quiz): QuizResponseDto {
      return QuizResponseDto(
              id = quiz.id,
              title = quiz.title,
              questionType = quiz.questionType,
              points = quiz.points,
              explanation = quiz.explanation,
              createdAt = quiz.createdAt
      )
    }
  }
}
