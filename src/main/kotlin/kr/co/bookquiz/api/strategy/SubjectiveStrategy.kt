package kr.co.bookquiz.api.strategy

import kr.co.bookquiz.api.dto.quiz.SubjectiveAnswerData
import org.springframework.stereotype.Component

@Component
class SubjectiveStrategy : AnswerStrategy<SubjectiveAnswerData> {
  override fun validate(correct: SubjectiveAnswerData, user: SubjectiveAnswerData): Boolean {
    val wordCount = user.answer.trim().split("\\s+".toRegex()).size
    return (correct.maxWords == null || wordCount <= correct.maxWords) && user.answer.isNotBlank()
  }

  override fun isCorrect(correct: SubjectiveAnswerData, user: SubjectiveAnswerData): Boolean {
    val userAnswer =
            if (correct.caseSensitive) user.answer.trim() else user.answer.trim().lowercase()
    return correct.possibleAnswers.any { possibleAnswer ->
      val normalizedPossible =
              if (correct.caseSensitive) possibleAnswer.trim()
              else possibleAnswer.trim().lowercase()
      userAnswer == normalizedPossible
    }
  }

  override fun score(correct: SubjectiveAnswerData, user: SubjectiveAnswerData): Double =
          if (isCorrect(correct, user)) 1.0 else 0.0

  override fun feedback(
          correct: SubjectiveAnswerData,
          user: SubjectiveAnswerData,
          score: Double
  ): String =
          if (score == 1.0) "Correct!"
          else "Incorrect. Possible answers: ${correct.possibleAnswers.joinToString(", ")}"
}
