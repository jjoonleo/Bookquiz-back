package kr.co.bookquiz.api

import com.fasterxml.jackson.databind.ObjectMapper
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kr.co.bookquiz.api.entity.Book
import kr.co.bookquiz.api.entity.Quiz
import org.junit.jupiter.api.Test

class QuizJsonTest {

  private val objectMapper = ObjectMapper()

  @Test
  fun `quiz json should not include book information`() {
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
                    book = book
            )

    // When
    val json = objectMapper.writeValueAsString(quiz)

    // Then
    assertTrue(json.contains("\"id\":1"))
    assertTrue(json.contains("\"title\":\"Test Quiz\""))
    assertTrue(json.contains("\"questionType\":\"MULTIPLE_CHOICE\""))
    assertTrue(json.contains("\"points\":5"))
    assertTrue(json.contains("\"explanation\":\"Test explanation\""))
    assertFalse(json.contains("\"book\""), "Book information should not be included in JSON")
    assertFalse(json.contains("\"Test Book\""), "Book title should not be included in JSON")
  }
}
