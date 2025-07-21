package kr.co.bookquiz.api

import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kr.co.bookquiz.api.entity.Book
import kr.co.bookquiz.api.entity.MultipleChoiceUserAnswer
import kr.co.bookquiz.api.entity.SubjectiveUserAnswer
import kr.co.bookquiz.api.entity.TrueFalseUserAnswer
import kr.co.bookquiz.api.entity.User
import kr.co.bookquiz.api.entity.enums.Grade
import kr.co.bookquiz.api.entity.enums.Province
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

class UserAnswerTest {

  private val testUser =
          User(
                  username = "testuser",
                  name = "Test User",
                  password = "password",
                  email = "test@example.com",
                  phoneNumber = "+1234567890",
                  dateOfBirth = LocalDateTime.now().minusYears(25),
                  province = Province.SEOUL,
                  grade = Grade.COLLEGE_GENERAL,
                  gender = true
          )

  private val testBook =
          Book(
                  id = 1L,
                  title = "Test Book",
                  isbn = "1234567890123",
                  publisher = "Test Publisher",
                  quizPrice = 1000
          )

  @Test
  fun `MultipleChoiceUserAnswer should create and convert to DTO correctly`() {
    val userAnswer =
            MultipleChoiceUserAnswer(
                    user = testUser,
                    quiz =
                            kr.co.bookquiz.api.entity.MultipleChoiceQuiz(
                                    title = "Test Quiz",
                                    answer = 2,
                                    book = testBook
                            ),
                    userAnswer = 1,
                    attemptNumber = 1,
                    isCorrect = false,
                    answeredAt = LocalDateTime.now()
            )

    val dto = userAnswer.toDto()

    assertAll(
            { assertEquals(testUser.username, dto.username) },
            { assertEquals(1L, dto.quizId) },
            { assertEquals(1, dto.userAnswer) },
            { assertEquals(1, dto.attemptNumber) },
            { assertFalse(dto.isCorrect) },
            { assertEquals("MULTIPLE_CHOICE", userAnswer.getAnswerType()) }
    )
  }

  @Test
  fun `SubjectiveUserAnswer should create and convert to DTO correctly`() {
    val userAnswer =
            SubjectiveUserAnswer(
                    user = testUser,
                    quiz =
                            kr.co.bookquiz.api.entity.SubjectiveQuiz(
                                    title = "Test Quiz",
                                    answer = "Correct Answer",
                                    book = testBook
                            ),
                    userAnswer = "User's Answer",
                    attemptNumber = 2,
                    isCorrect = false,
                    answeredAt = LocalDateTime.now()
            )

    val dto = userAnswer.toDto()

    assertAll(
            { assertEquals(testUser.username, dto.username) },
            { assertEquals(1L, dto.quizId) },
            { assertEquals("User's Answer", dto.userAnswer) },
            { assertEquals(2, dto.attemptNumber) },
            { assertFalse(dto.isCorrect) },
            { assertEquals("SUBJECTIVE", userAnswer.getAnswerType()) }
    )
  }

  @Test
  fun `TrueFalseUserAnswer should create and convert to DTO correctly`() {
    val userAnswer =
            TrueFalseUserAnswer(
                    user = testUser,
                    quiz =
                            kr.co.bookquiz.api.entity.TrueFalseQuiz(
                                    title = "Test Quiz",
                                    answer = true,
                                    book = testBook
                            ),
                    userAnswer = false,
                    attemptNumber = 1,
                    isCorrect = false,
                    answeredAt = LocalDateTime.now()
            )

    val dto = userAnswer.toDto()

    assertAll(
            { assertEquals(testUser.username, dto.username) },
            { assertEquals(1L, dto.quizId) },
            { assertFalse(dto.userAnswer) },
            { assertEquals(1, dto.attemptNumber) },
            { assertFalse(dto.isCorrect) },
            { assertEquals("TRUE_FALSE", userAnswer.getAnswerType()) }
    )
  }

  @Test
  fun `MultipleChoiceUserAnswer copy should work correctly`() {
    val original =
            MultipleChoiceUserAnswer(
                    user = testUser,
                    quiz =
                            kr.co.bookquiz.api.entity.MultipleChoiceQuiz(
                                    title = "Test Quiz",
                                    answer = 2,
                                    book = testBook
                            ),
                    userAnswer = 1,
                    attemptNumber = 1,
                    isCorrect = false,
                    answeredAt = LocalDateTime.now()
            )

    val copied = original.copy(userAnswer = 2, attemptNumber = 2, isCorrect = true)

    assertAll(
            { assertEquals(2, copied.userAnswer) },
            { assertEquals(2, copied.attemptNumber) },
            { assertTrue(copied.isCorrect) },
            { assertEquals(original.id, copied.id) },
            { assertEquals(original.user, copied.user) },
            { assertEquals(original.quiz, copied.quiz) }
    )
  }
}
