package kr.co.bookquiz.api

import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kr.co.bookquiz.api.dto.quiz.QuizResponseDto
import kr.co.bookquiz.api.entity.Book
import kr.co.bookquiz.api.entity.Quiz
import org.junit.jupiter.api.Test

class QuizResponseDtoTest {

  @Test
  fun `from method should create dto from quiz entity`() {
    // Given
    val book =
            Book(
                    id = 1L,
                    title = "Test Book",
                    isbn = "1234567890123",
                    publisher = "Test Publisher",
                    quizPrice = 1000,
                    thumbnail = null
            )

    val quiz =
            Quiz(
                    id = 1L,
                    title = "Test Quiz",
                    questionType = "MULTIPLE_CHOICE",
                    points = 5,
                    explanation = "Test explanation",
                    book = book,
                    createdAt = LocalDateTime.of(2024, 1, 15, 10, 30, 0)
            )

    // When
    val dto = QuizResponseDto.from(quiz)

    // Then
    assertNotNull(dto)
    assertEquals(1L, dto.id)
    assertEquals("Test Quiz", dto.title)
    assertEquals("MULTIPLE_CHOICE", dto.questionType)
    assertEquals(5, dto.points)
    assertEquals("Test explanation", dto.explanation)
    assertEquals(LocalDateTime.of(2024, 1, 15, 10, 30, 0), dto.createdAt)
  }

  @Test
  fun `from method should handle null explanation`() {
    // Given
    val book =
            Book(
                    id = 1L,
                    title = "Test Book",
                    isbn = "1234567890123",
                    publisher = "Test Publisher",
                    quizPrice = 1000,
                    thumbnail = null
            )

    val quiz =
            Quiz(
                    id = 2L,
                    title = "Test Quiz 2",
                    questionType = "SUBJECTIVE",
                    points = 10,
                    explanation = null,
                    book = book,
                    createdAt = LocalDateTime.of(2024, 1, 15, 10, 35, 0)
            )

    // When
    val dto = QuizResponseDto.from(quiz)

    // Then
    assertNotNull(dto)
    assertEquals(2L, dto.id)
    assertEquals("Test Quiz 2", dto.title)
    assertEquals("SUBJECTIVE", dto.questionType)
    assertEquals(10, dto.points)
    assertEquals(null, dto.explanation)
    assertEquals(LocalDateTime.of(2024, 1, 15, 10, 35, 0), dto.createdAt)
  }
}
