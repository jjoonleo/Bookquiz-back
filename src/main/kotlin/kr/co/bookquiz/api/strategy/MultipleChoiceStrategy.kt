package kr.co.bookquiz.api.strategy

import kr.co.bookquiz.api.dto.quiz.MultipleChoiceAnswerData
import org.springframework.stereotype.Component

@Component
class MultipleChoiceStrategy : AnswerStrategy<MultipleChoiceAnswerData> {
  override fun validate(
          correct: MultipleChoiceAnswerData,
          user: MultipleChoiceAnswerData
  ): Boolean = user.selectedIndex in correct.options.indices

  override fun isCorrect(
          correct: MultipleChoiceAnswerData,
          user: MultipleChoiceAnswerData
  ): Boolean = user.selectedIndex == correct.correctIndex

  override fun score(correct: MultipleChoiceAnswerData, user: MultipleChoiceAnswerData): Double =
          if (isCorrect(correct, user)) 1.0 else 0.0

  override fun feedback(
          correct: MultipleChoiceAnswerData,
          user: MultipleChoiceAnswerData,
          score: Double
  ): String =
          if (score == 1.0) "Correct!"
          else "Incorrect. Correct answer: ${correct.options[correct.correctIndex]}"
}
