package kr.co.bookquiz.api

import com.fasterxml.jackson.databind.ObjectMapper
import java.time.LocalDateTime
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kr.co.bookquiz.api.dto.quiz.MultipleChoiceQuizResponseDto
import org.junit.jupiter.api.Test

class QuizJsonTest {

    private val objectMapper = ObjectMapper()

    @Test
    fun `quiz json should not include book information`() {
        // Given
        val quizDto =
                MultipleChoiceQuizResponseDto(
                        id = 1L,
                        title = "Test Quiz",
                        explanation = "Test explanation",
                        hint = null,
                        bookId = 1L,
                        createdAt = LocalDateTime.of(2024, 1, 15, 10, 30, 0),
                        answer = 0,
                        options = listOf("A", "B", "C")
                )

        // When
        val json = objectMapper.writeValueAsString(quizDto)

        // Then
        assertTrue(json.contains("\"id\":1"))
        assertTrue(json.contains("\"title\":\"Test Quiz\""))
        assertTrue(json.contains("\"explanation\":\"Test explanation\""))
        assertTrue(json.contains("\"bookId\":1"))
        assertFalse(json.contains("\"book\""), "Book information should not be included in JSON")
        assertFalse(json.contains("\"Test Book\""), "Book title should not be included in JSON")
    }
}
