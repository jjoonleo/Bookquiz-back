package kr.co.bookquiz.api.service

import kr.co.bookquiz.api.dto.quiz.*
import kr.co.bookquiz.api.entity.*
import kr.co.bookquiz.api.repository.*
import kr.co.bookquiz.api.strategy.AnswerStrategyRegistry
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class QuizService(
        private val quizRepo: QuizRepository,
        private val mcRepo: MultipleChoiceAnswerRepository,
        private val subjRepo: SubjectiveAnswerRepository,
        private val tfRepo: TrueFalseAnswerRepository,
        private val registry: AnswerStrategyRegistry
) {
  @Transactional(readOnly = true)
  fun evaluateQuizAnswer(
          quizId: Long,
          userAnswer: Any // MultipleChoiceAnswerData, SubjectiveAnswerData, or TrueFalseAnswerData
  ): AnswerResult {
    val quiz =
            quizRepo.findById(quizId).orElseThrow {
              IllegalArgumentException("No quiz found with id: $quizId")
            }

    return when (quiz.questionType) {
      "MULTIPLE_CHOICE" -> {
        val correct = mcRepo.findByQuizId(quizId) ?: error("No MCQ answer found for quiz $quizId")
        val userAnswerData = userAnswer as MultipleChoiceAnswerData
        val correctData =
                MultipleChoiceAnswerData(
                        options = correct.options,
                        correctIndex = correct.correctIndex,
                        selectedIndex = userAnswerData.selectedIndex
                )
        val strat = registry.getStrategy<MultipleChoiceAnswerData>("MULTIPLE_CHOICE")
        val valid = strat.validate(correctData, userAnswerData)
        val score = if (valid) strat.score(correctData, userAnswerData) else 0.0
        val feedback = strat.feedback(correctData, userAnswerData, score)
        AnswerResult(valid && score == 1.0, score, feedback)
      }
      "SUBJECTIVE" -> {
        val correct =
                subjRepo.findByQuizId(quizId)
                        ?: error("No subjective answer found for quiz $quizId")
        val userAnswerData = userAnswer as SubjectiveAnswerData
        val correctData =
                SubjectiveAnswerData(
                        possibleAnswers = correct.possibleAnswers,
                        caseSensitive = correct.caseSensitive,
                        maxWords = correct.maxWords,
                        answer = userAnswerData.answer
                )
        val strat = registry.getStrategy<SubjectiveAnswerData>("SUBJECTIVE")
        val valid = strat.validate(correctData, userAnswerData)
        val score = if (valid) strat.score(correctData, userAnswerData) else 0.0
        val feedback = strat.feedback(correctData, userAnswerData, score)
        AnswerResult(valid && score == 1.0, score, feedback)
      }
      "TRUE_FALSE" -> {
        val correct =
                tfRepo.findByQuizId(quizId) ?: error("No True/False answer found for quiz $quizId")
        val userAnswerData = userAnswer as TrueFalseAnswerData
        val correctData =
                TrueFalseAnswerData(
                        correctAnswer = correct.correctAnswer,
                        answer = userAnswerData.answer
                )
        val strat = registry.getStrategy<TrueFalseAnswerData>("TRUE_FALSE")
        val valid = strat.validate(correctData, userAnswerData)
        val score = if (valid) strat.score(correctData, userAnswerData) else 0.0
        val feedback = strat.feedback(correctData, userAnswerData, score)
        AnswerResult(valid && score == 1.0, score, feedback)
      }
      else -> throw IllegalArgumentException("Unknown question type: ${quiz.questionType}")
    }
  }

  @Transactional(readOnly = true)
  fun getQuiz(quizId: Long): Quiz {
    return quizRepo.findById(quizId).orElseThrow {
      IllegalArgumentException("No quiz found with id: $quizId")
    }
  }

  @Transactional(readOnly = true)
  fun getMultipleChoiceAnswer(quizId: Long): MultipleChoiceAnswerEntity? {
    return mcRepo.findByQuizId(quizId)
  }

  @Transactional(readOnly = true)
  fun getSubjectiveAnswer(quizId: Long): SubjectiveAnswerEntity? {
    return subjRepo.findByQuizId(quizId)
  }

  @Transactional(readOnly = true)
  fun getTrueFalseAnswer(quizId: Long): TrueFalseAnswerEntity? {
    return tfRepo.findByQuizId(quizId)
  }

  @Transactional(readOnly = true)
  fun getQuizzesByBookId(bookId: Long): List<QuizResponseDto> {
    return quizRepo.findByBookId(bookId).map { QuizResponseDto.from(it) }
  }
}
