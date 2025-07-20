package kr.co.bookquiz.api

import kr.co.bookquiz.api.entity.Book
import kr.co.bookquiz.api.entity.MultipleChoiceQuiz
import kr.co.bookquiz.api.entity.SubjectiveQuiz
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class QuizResponseDtoTest {

    @Test
    fun `toDto method should create dto from quiz entity`() {
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
            MultipleChoiceQuiz(
                id = 1L,
                title = "Test Quiz",
                answer = 0,
                explanation = "Test explanation",
                options = listOf("A", "B", "C"),
                book = book,
                createdAt = LocalDateTime.of(2024, 1, 15, 10, 30, 0)
            )

        // When
        val dto = quiz.toDto()

        // Then
        assertNotNull(dto)
        assertEquals(1L, dto.id)
        assertEquals("Test Quiz", dto.title)
        assertEquals("MULTIPLE_CHOICE", dto.type)
        assertEquals("Test explanation", dto.explanation)
        assertEquals(LocalDateTime.of(2024, 1, 15, 10, 30, 0), dto.createdAt)
    }

    @Test
    fun `toDto method should handle null explanation`() {
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
            SubjectiveQuiz(
                id = 2L,
                title = "Test Quiz 2",
                answer = "Test Answer",
                explanation = null,
                book = book,
                createdAt = LocalDateTime.of(2024, 1, 15, 10, 35, 0)
            )

        // When
        val dto = quiz.toDto()

        // Then
        assertNotNull(dto)
        assertEquals(2L, dto.id)
        assertEquals("Test Quiz 2", dto.title)
        assertEquals("SUBJECTIVE", dto.type)
        assertEquals(null, dto.explanation)
        assertEquals(LocalDateTime.of(2024, 1, 15, 10, 35, 0), dto.createdAt)
    }
}