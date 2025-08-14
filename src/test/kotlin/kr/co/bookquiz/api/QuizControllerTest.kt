package kr.co.bookquiz.api

import kotlin.test.assertEquals
import kr.co.bookquiz.api.controller.QuizController
import kr.co.bookquiz.api.dto.quiz.QuizResponseDto
import kr.co.bookquiz.api.entity.Book
import kr.co.bookquiz.api.entity.MultipleChoiceQuiz
import kr.co.bookquiz.api.entity.SubjectiveQuiz
import kr.co.bookquiz.api.entity.TrueFalseQuiz
import kr.co.bookquiz.api.service.QuizService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class QuizControllerTest {

    @Mock private lateinit var quizService: QuizService

    private lateinit var quizController: QuizController

    private lateinit var mockBook: Book

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        quizController = QuizController(quizService)
        mockBook =
                Book(
                        id = 1L,
                        title = "Test Book",
                        subtitle = "Test Book Subtitle",
                        isbn = "1234567890123",
                        publisher = "Test Publisher",
                        quizPrice = 1000,
                        thumbnail = null
                )
    }

    @Test
    fun `get quizzes by book id returns correct response`() {
        // Given
        val bookId = 1L
        val quizzes =
                listOf(
                        MultipleChoiceQuiz(id = 1L, title = "Quiz 1", answer = 0, book = mockBook)
                                .apply {
                                    options.add(
                                            kr.co.bookquiz.api.entity.MultipleChoiceOption(
                                                    optionText = "A",
                                                    optionIndex = 0,
                                                    quiz = this
                                            )
                                    )
                                    options.add(
                                            kr.co.bookquiz.api.entity.MultipleChoiceOption(
                                                    optionText = "B",
                                                    optionIndex = 1,
                                                    quiz = this
                                            )
                                    )
                                    options.add(
                                            kr.co.bookquiz.api.entity.MultipleChoiceOption(
                                                    optionText = "C",
                                                    optionIndex = 2,
                                                    quiz = this
                                            )
                                    )
                                },
                        SubjectiveQuiz(
                                id = 2L,
                                title = "Quiz 2",
                                answer = "Answer",
                                book = mockBook
                        ),
                        TrueFalseQuiz(id = 3L, title = "Quiz 3", answer = true, book = mockBook)
                )
        val quizDtos = quizzes.map { it.toDto() }

        `when`(quizService.getQuizzesByBookId(bookId)).thenReturn(quizDtos)

        // When
        val response: ResponseEntity<List<QuizResponseDto>> =
                quizController.getQuizzesByBookId(bookId)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(quizDtos, response.body)
        assertEquals(3, response.body?.size)
        verify(quizService).getQuizzesByBookId(bookId)
    }

    @Test
    fun `get quizzes by book id returns empty list when no quizzes found`() {
        // Given
        val bookId = 999L
        val emptyQuizzes = emptyList<QuizResponseDto>()

        `when`(quizService.getQuizzesByBookId(bookId)).thenReturn(emptyQuizzes)

        // When
        val response: ResponseEntity<List<QuizResponseDto>> =
                quizController.getQuizzesByBookId(bookId)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(emptyQuizzes, response.body)
        assertEquals(0, response.body?.size)
        verify(quizService).getQuizzesByBookId(bookId)
    }
}
