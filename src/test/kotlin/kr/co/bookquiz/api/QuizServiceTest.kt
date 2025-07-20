package kr.co.bookquiz.api

import kr.co.bookquiz.api.dto.quiz.QuizResponseDto
import kr.co.bookquiz.api.entity.Book
import kr.co.bookquiz.api.entity.MultipleChoiceQuiz
import kr.co.bookquiz.api.entity.Quiz
import kr.co.bookquiz.api.entity.SubjectiveQuiz
import kr.co.bookquiz.api.entity.TrueFalseQuiz
import kr.co.bookquiz.api.repository.BookRepository
import kr.co.bookquiz.api.repository.QuizRepository
import kr.co.bookquiz.api.service.QuizService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class QuizServiceTest {

    @Mock private lateinit var quizRepo: QuizRepository

    @Mock private lateinit var bookRepo: BookRepository

    private lateinit var quizService: QuizService

    private lateinit var mockBook: Book

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        quizService = QuizService(quizRepo, bookRepo)
        mockBook =
            Book(
                id = 1L,
                title = "Test Book",
                isbn = "1234567890123",
                publisher = "Test Publisher",
                quizPrice = 1000,
                thumbnail = null
            )
    }

    @Test
    fun `evaluate multiple choice correct answer`() {
        // Given
        val quizId = 1L
        val quiz =
            MultipleChoiceQuiz(
                id = quizId,
                title = "Test MCQ",
                answer = 1,
                options = listOf("Option A", "Option B", "Option C"),
                book = mockBook
            )
        val userAnswer = 1

        `when`(quizRepo.findById(quizId)).thenReturn(java.util.Optional.of(quiz))

        // When
        val result = quizService.evaluateQuizAnswer(quizId, userAnswer)

        // Then
        assertTrue(result.isCorrect)
        assertEquals(1.0, result.score)
        assertEquals("Correct!", result.feedback)
    }

    @Test
    fun `evaluate multiple choice incorrect answer`() {
        // Given
        val quizId = 1L
        val quiz =
            MultipleChoiceQuiz(
                id = quizId,
                title = "Test MCQ",
                answer = 1,
                options = listOf("Option A", "Option B", "Option C"),
                book = mockBook
            )
        val userAnswer = 0

        `when`(quizRepo.findById(quizId)).thenReturn(java.util.Optional.of(quiz))

        // When
        val result = quizService.evaluateQuizAnswer(quizId, userAnswer)

        // Then
        assertFalse(result.isCorrect)
        assertEquals(0.0, result.score)
        assertEquals("Incorrect. The correct answer was option 2.", result.feedback)
    }

    @Test
    fun `evaluate subjective correct answer`() {
        // Given
        val quizId = 1L
        val quiz =
            SubjectiveQuiz(
                id = quizId,
                title = "Test Subjective",
                answer = "Paris",
                book = mockBook
            )
        val userAnswer = "Paris"

        `when`(quizRepo.findById(quizId)).thenReturn(java.util.Optional.of(quiz))

        // When
        val result = quizService.evaluateQuizAnswer(quizId, userAnswer)

        // Then
        assertTrue(result.isCorrect)
        assertEquals(1.0, result.score)
        assertEquals("Correct!", result.feedback)
    }

    @Test
    fun `evaluate true false correct answer`() {
        // Given
        val quizId = 1L
        val quiz = TrueFalseQuiz(id = quizId, title = "Test TF", answer = true, book = mockBook)
        val userAnswer = true

        `when`(quizRepo.findById(quizId)).thenReturn(java.util.Optional.of(quiz))

        // When
        val result = quizService.evaluateQuizAnswer(quizId, userAnswer)

        // Then
        assertTrue(result.isCorrect)
        assertEquals(1.0, result.score)
        assertEquals("Correct!", result.feedback)
    }

    @Test
    fun `throw exception for unknown question type`() {
        // Given
        val quizId = 1L
        val quiz =
            object : Quiz(id = quizId, title = "Test Unknown", book = mockBook) {
                override val answer: Any = "unknown"
                override fun getQuizType(): String = "UNKNOWN"
                override fun toDto(): QuizResponseDto = throw NotImplementedError()
            }
        val userAnswer = 1

        `when`(quizRepo.findById(quizId)).thenReturn(java.util.Optional.of(quiz))

        // When & Then
        assertThrows<IllegalArgumentException> {
            quizService.evaluateQuizAnswer(quizId, userAnswer)
        }
    }

    @Test
    fun `throw exception for non-existent quiz`() {
        // Given
        val quizId = 999L
        val userAnswer = 1

        `when`(quizRepo.findById(quizId)).thenReturn(java.util.Optional.empty())

        // When & Then
        assertThrows<IllegalArgumentException> {
            quizService.evaluateQuizAnswer(quizId, userAnswer)
        }
    }

    @Test
    fun `get quizzes by book id`() {
        // Given
        val bookId = 1L
        val quizzes =
            listOf(
                MultipleChoiceQuiz(
                    id = 1L,
                    title = "Quiz 1",
                    answer = 0,
                    options = listOf("A", "B", "C"),
                    book = mockBook
                ),
                SubjectiveQuiz(
                    id = 2L,
                    title = "Quiz 2",
                    answer = "Answer",
                    book = mockBook
                ),
                TrueFalseQuiz(id = 3L, title = "Quiz 3", answer = true, book = mockBook)
            )

        `when`(quizRepo.findByBookId(bookId)).thenReturn(quizzes)

        // When
        val result = quizService.getQuizzesByBookId(bookId)

        // Then
        assertEquals(3, result.size)
        assertEquals("Quiz 1", result[0].title)
        assertEquals("Quiz 2", result[1].title)
        assertEquals("Quiz 3", result[2].title)
        assertTrue(result[0] is QuizResponseDto)
        assertTrue(result[1] is QuizResponseDto)
        assertTrue(result[2] is QuizResponseDto)
    }
}