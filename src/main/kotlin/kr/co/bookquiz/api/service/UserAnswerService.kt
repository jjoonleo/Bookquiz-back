package kr.co.bookquiz.api.service

import java.time.LocalDateTime
import kr.co.bookquiz.api.api.exception.ApiException
import kr.co.bookquiz.api.api.exception.ErrorCode.DUPLICATE_ATTEMPT
import kr.co.bookquiz.api.dto.useranswer.*
import kr.co.bookquiz.api.repository.AnswerRepository
import kr.co.bookquiz.api.repository.QuizRepository
import kr.co.bookquiz.api.repository.UserRepository
import org.hibernate.exception.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserAnswerService(
        private val answerRepository: AnswerRepository,
        private val userRepository: UserRepository,
        private val quizRepository: QuizRepository
) {

  @Transactional
  fun createUserAnswer(createDto: CreateUserAnswerDto, username: String): UserAnswerResponseDto {
    val user =
            userRepository.findById(username).orElseThrow {
              IllegalArgumentException("User not found: ${username}")
            }
    val quiz =
            quizRepository.findById(createDto.quizId).orElseThrow {
              IllegalArgumentException("Quiz not found: ${createDto.quizId}")
            }

    if (createDto.type != quiz.getQuizType()) {
      throw IllegalArgumentException(
              "Quiz type mismatch: ${createDto.type} != ${quiz.getQuizType()}"
      )
    }

    val isCorrect = quiz.isCorrectAnswer(createDto.userAnswer)
    val attemptNumber = createDto.attemptNumber ?: getNextAttemptNumber(username, createDto.quizId)

    val answeredAt = LocalDateTime.now()
    val userAnswerEntity = createDto.toEntity(user, quiz, isCorrect, attemptNumber, answeredAt)
    try {
      return answerRepository.save(userAnswerEntity).toDto()
    } catch (ex: DataIntegrityViolationException) {
      // Check if it's a unique constraint violation on user_id, quiz_id, attempt_number
      if (isUserQuizAttemptConstraintViolation(ex)) {
        throw ApiException(
                DUPLICATE_ATTEMPT,
                "Answer for attemptNumber $attemptNumber already exists for this quiz and user."
        )
      }
      throw ex
    }
  }

  @Transactional
  fun updateUserAnswer(id: Long, updateDto: UpdateUserAnswerDto): UserAnswerResponseDto {
    val existingAnswer =
            answerRepository.findById(id).orElseThrow {
              IllegalArgumentException("UserAnswer not found: $id")
            }

    val updatedAnswer = updateDto.updateEntity(existingAnswer)
    return answerRepository.save(updatedAnswer).toDto()
  }

  @Transactional(readOnly = true)
  fun getUserAnswersByQuiz(username: String, quizId: Long): List<UserAnswerResponseDto> {
    return answerRepository.findByUserUsernameAndQuizIdOrderByAttemptNumberDesc(username, quizId)
            .map { it.toDto() }
  }

  @Transactional(readOnly = true)
  fun getUserAnswersByUser(username: String): List<UserAnswerResponseDto> {
    return answerRepository.findByUserUsername(username).map { it.toDto() }
  }

  @Transactional(readOnly = true)
  fun getLatestAnswer(username: String, quizId: Long): UserAnswerResponseDto? {
    return answerRepository
            .findByUserUsernameAndQuizIdOrderByAttemptNumberDesc(username, quizId)
            .firstOrNull()
            ?.toDto()
  }

  @Transactional(readOnly = true)
  fun getAttemptCount(username: String, quizId: Long): Long {
    return answerRepository.countByUserUsernameAndQuizId(username, quizId)
  }

  @Transactional(readOnly = true)
  fun hasCorrectAnswer(username: String, quizId: Long): Boolean {
    return answerRepository.findByUserUsernameAndQuizId(username, quizId).any { it.isCorrect }
  }

  @Transactional(readOnly = true)
  fun getUserQuizSummary(username: String, quizId: Long): UserQuizSummaryDto? {
    val answers =
            answerRepository.findByUserUsernameAndQuizIdOrderByAttemptNumberDesc(username, quizId)
    if (answers.isEmpty()) return null

    val totalAttempts = answers.size
    val correctAttempts = answers.count { it.isCorrect }
    val bestAttempt =
            answers.indexOfFirst { it.isCorrect }.let { if (it == -1) totalAttempts else it + 1 }
    val latestAttempt = answers.first().toDto()
    val firstAttemptAt = answers.last().answeredAt
    val lastAttemptAt = answers.first().answeredAt

    return UserQuizSummaryDto(
            username = username,
            quizId = quizId,
            totalAttempts = totalAttempts,
            correctAttempts = correctAttempts,
            bestAttempt = bestAttempt,
            latestAttempt = latestAttempt,
            firstAttemptAt = firstAttemptAt,
            lastAttemptAt = lastAttemptAt
    )
  }

  @Transactional(readOnly = true)
  fun getUserAnswerStats(username: String): UserAnswerStatsDto {
    val allAnswers = answerRepository.findByUserUsername(username)
    val totalQuizzesAttempted = allAnswers.map { it.quiz.id }.distinct().size
    val totalAttempts = allAnswers.size
    val correctAnswers = allAnswers.count { it.isCorrect }
    val accuracy = if (totalAttempts > 0) correctAnswers.toDouble() / totalAttempts else 0.0
    val averageAttemptsPerQuiz =
            if (totalQuizzesAttempted > 0) totalAttempts.toDouble() / totalQuizzesAttempted else 0.0

    return UserAnswerStatsDto(
            username = username,
            totalQuizzesAttempted = totalQuizzesAttempted,
            totalAttempts = totalAttempts,
            correctAnswers = correctAnswers,
            accuracy = accuracy,
            averageAttemptsPerQuiz = averageAttemptsPerQuiz
    )
  }

  private fun getNextAttemptNumber(username: String, quizId: Long): Int {
    val attemptCount = answerRepository.countByUserUsernameAndQuizId(username, quizId)
    return (attemptCount + 1).toInt()
  }

  private fun isUserQuizAttemptConstraintViolation(ex: DataIntegrityViolationException): Boolean {
    val rootCause = ex.rootCause
    return when {
      rootCause is ConstraintViolationException -> {
        val constraintName = rootCause.constraintName
        constraintName == "user_quiz_attempt_unique"
      }
      // Fallback: check error message if constraint name is null
      ex.message?.contains("user_quiz_attempt_unique") == true -> true
      else -> false
    }
  }
}
