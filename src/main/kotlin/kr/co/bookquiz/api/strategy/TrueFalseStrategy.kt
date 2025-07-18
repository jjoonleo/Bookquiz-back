package kr.co.bookquiz.api.strategy

import kr.co.bookquiz.api.dto.quiz.TrueFalseAnswerData
import org.springframework.stereotype.Component

@Component
class TrueFalseStrategy : AnswerStrategy<TrueFalseAnswerData> {
  override fun validate(correct: TrueFalseAnswerData, user: TrueFalseAnswerData): Boolean = true

  override fun isCorrect(correct: TrueFalseAnswerData, user: TrueFalseAnswerData): Boolean =
          user.answer == correct.correctAnswer

  override fun score(correct: TrueFalseAnswerData, user: TrueFalseAnswerData): Double =
          if (isCorrect(correct, user)) 1.0 else 0.0

  override fun feedback(
          correct: TrueFalseAnswerData,
          user: TrueFalseAnswerData,
          score: Double
  ): String =
          if (score == 1.0) "Correct!" else "Incorrect. Correct answer: ${correct.correctAnswer}"
}
