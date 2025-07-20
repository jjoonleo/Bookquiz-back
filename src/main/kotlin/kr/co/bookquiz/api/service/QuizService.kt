package kr.co.bookquiz.api.service

import kr.co.bookquiz.api.dto.quiz.*
import kr.co.bookquiz.api.entity.*
import kr.co.bookquiz.api.repository.BookRepository
import kr.co.bookquiz.api.repository.QuizRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class QuizService(
        private val quizRepository: QuizRepository,
        private val bookRepository: BookRepository
) {

  @Transactional
  fun createQuiz(createQuizDto: CreateQuizDto): QuizResponseDto {
    val book =
            bookRepository.findById(createQuizDto.bookId).orElseThrow {
              IllegalArgumentException("No book found with id: ${createQuizDto.bookId}")
            }

    val quiz = createQuizDto.toEntity(book)
    val savedQuiz = quizRepository.save(quiz)
    return savedQuiz.toDto()
  }

  @Transactional(readOnly = true)
  fun getQuiz(quizId: Long): QuizResponseDto {
    val quiz =
            quizRepository.findById(quizId).orElseThrow {
              IllegalArgumentException("No quiz found with id: $quizId")
            }
    return quiz.toDto()
  }

  @Transactional
  fun updateQuiz(quizId: Long, updateQuizDto: UpdateQuizDto): QuizResponseDto {
    val existingQuiz =
            quizRepository.findById(quizId).orElseThrow {
              IllegalArgumentException("No quiz found with id: $quizId")
            }

    val updatedQuiz = updateQuizDto.updateEntity(existingQuiz)
    val savedQuiz = quizRepository.save(updatedQuiz)
    return savedQuiz.toDto()
  }

  @Transactional
  fun deleteQuiz(quizId: Long) {
    if (!quizRepository.existsById(quizId)) {
      throw IllegalArgumentException("No quiz found with id: $quizId")
    }
    quizRepository.deleteById(quizId)
  }

  @Transactional(readOnly = true)
  fun getQuizzesByBookId(bookId: Long): List<QuizResponseDto> {
    return quizRepository.findByBookId(bookId).map { it.toDto() }
  }

  @Transactional(readOnly = true)
  fun getQuizzesByTitle(title: String): List<QuizResponseDto> {
    return quizRepository.findByTitleContaining(title).map { it.toDto() }
  }

  @Transactional(readOnly = true)
  fun getQuizzesWithFilters(filter: QuizFilterDto): List<QuizResponseDto> {
    val entityClass =
            when (filter.type) {
              "MULTIPLE_CHOICE" -> MultipleChoiceQuiz::class.java
              "SUBJECTIVE" -> SubjectiveQuiz::class.java
              "TRUE_FALSE" -> TrueFalseQuiz::class.java
              null -> null
              else -> null
            }

    return quizRepository.findQuizzesWithFilters(
                    bookId = filter.bookId,
                    title = filter.title,
                    type = filter.type,
                    entityClass = entityClass
            )
            .map { it.toDto() }
  }

  @Transactional(readOnly = true)
  fun getQuizzesByBookIdAndType(bookId: Long, type: String): List<QuizResponseDto> {
    val entityClass =
            when (type) {
              "MULTIPLE_CHOICE" -> MultipleChoiceQuiz::class.java
              "SUBJECTIVE" -> SubjectiveQuiz::class.java
              "TRUE_FALSE" -> TrueFalseQuiz::class.java
              else -> throw IllegalArgumentException("Unknown quiz type: $type")
            }

    return quizRepository.findByBookIdAndType(bookId, entityClass).map { it.toDto() }
  }

  @Transactional(readOnly = true)
  fun evaluateQuizAnswer(quizId: Long, userAnswer: Any): AnswerResult {
    val quiz =
            quizRepository.findById(quizId).orElseThrow {
              IllegalArgumentException("No quiz found with id: $quizId")
            }

    return when (quiz) {
      is MultipleChoiceQuiz -> {
        val userAnswerInt =
                userAnswer as? Int
                        ?: throw IllegalArgumentException(
                                "Invalid answer type for multiple choice quiz"
                        )
        val isCorrect = userAnswerInt == quiz.answer
        val score = if (isCorrect) 1.0 else 0.0
        val feedback =
                if (isCorrect) "Correct!"
                else "Incorrect. The correct answer was option ${quiz.answer + 1}."
        AnswerResult(isCorrect, score, feedback)
      }
      is SubjectiveQuiz -> {
        val userAnswerString =
                userAnswer as? String
                        ?: throw IllegalArgumentException("Invalid answer type for subjective quiz")
        val isCorrect = userAnswerString.trim().equals(quiz.answer.trim(), ignoreCase = true)
        val score = if (isCorrect) 1.0 else 0.0
        val feedback =
                if (isCorrect) "Correct!" else "Incorrect. The correct answer was: ${quiz.answer}"
        AnswerResult(isCorrect, score, feedback)
      }
      is TrueFalseQuiz -> {
        val userAnswerBoolean =
                userAnswer as? Boolean
                        ?: throw IllegalArgumentException("Invalid answer type for true/false quiz")
        val isCorrect = userAnswerBoolean == quiz.answer
        val score = if (isCorrect) 1.0 else 0.0
        val feedback =
                if (isCorrect) "Correct!" else "Incorrect. The correct answer was: ${quiz.answer}"
        AnswerResult(isCorrect, score, feedback)
      }
      else -> throw IllegalArgumentException("Unknown quiz type: ${quiz.getQuizType()}")
    }
  }
}

data class AnswerResult(val isCorrect: Boolean, val score: Double, val feedback: String)
