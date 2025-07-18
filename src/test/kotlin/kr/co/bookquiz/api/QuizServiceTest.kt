package kr.co.bookquiz.api

import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kr.co.bookquiz.api.dto.quiz.*
import kr.co.bookquiz.api.entity.*
import kr.co.bookquiz.api.repository.*
import kr.co.bookquiz.api.service.QuizService
import kr.co.bookquiz.api.strategy.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class QuizServiceTest {

  @Mock private lateinit var quizRepo: QuizRepository

  @Mock private lateinit var mcRepo: MultipleChoiceAnswerRepository

  @Mock private lateinit var subjRepo: SubjectiveAnswerRepository

  @Mock private lateinit var tfRepo: TrueFalseAnswerRepository

  @Mock private lateinit var registry: AnswerStrategyRegistry

  @Mock private lateinit var mcStrategy: MultipleChoiceStrategy

  @Mock private lateinit var subjStrategy: SubjectiveStrategy

  @Mock private lateinit var tfStrategy: TrueFalseStrategy

  private lateinit var quizService: QuizService

  private lateinit var mockBook: Book

  @BeforeEach
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    quizService = QuizService(quizRepo, mcRepo, subjRepo, tfRepo, registry)
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
            Quiz(id = quizId, title = "Test MCQ", questionType = "MULTIPLE_CHOICE", book = mockBook)
    val mcAnswer =
            MultipleChoiceAnswerEntity(
                    correctIndex = 1,
                    options = listOf("Option A", "Option B", "Option C"),
                    quiz = quiz
            )
    val userAnswer =
            MultipleChoiceAnswerData(
                    options = listOf("Option A", "Option B", "Option C"),
                    correctIndex = 1,
                    selectedIndex = 1
            )

    `when`(quizRepo.findById(quizId)).thenReturn(java.util.Optional.of(quiz))
    `when`(mcRepo.findByQuizId(quizId)).thenReturn(mcAnswer)
    `when`(registry.getStrategy<MultipleChoiceAnswerData>("MULTIPLE_CHOICE")).thenReturn(mcStrategy)
    `when`(mcStrategy.validate(any(), any())).thenReturn(true)
    `when`(mcStrategy.score(any(), any())).thenReturn(1.0)
    `when`(mcStrategy.feedback(any(), any(), any())).thenReturn("Correct!")

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
            Quiz(id = quizId, title = "Test MCQ", questionType = "MULTIPLE_CHOICE", book = mockBook)
    val mcAnswer =
            MultipleChoiceAnswerEntity(
                    correctIndex = 1,
                    options = listOf("Option A", "Option B", "Option C"),
                    quiz = quiz
            )
    val userAnswer =
            MultipleChoiceAnswerData(
                    options = listOf("Option A", "Option B", "Option C"),
                    correctIndex = 1,
                    selectedIndex = 0
            )

    `when`(quizRepo.findById(quizId)).thenReturn(java.util.Optional.of(quiz))
    `when`(mcRepo.findByQuizId(quizId)).thenReturn(mcAnswer)
    `when`(registry.getStrategy<MultipleChoiceAnswerData>("MULTIPLE_CHOICE")).thenReturn(mcStrategy)
    `when`(mcStrategy.validate(any(), any())).thenReturn(true)
    `when`(mcStrategy.score(any(), any())).thenReturn(0.0)
    `when`(mcStrategy.feedback(any(), any(), any()))
            .thenReturn("Incorrect. Correct answer: Option B")

    // When
    val result = quizService.evaluateQuizAnswer(quizId, userAnswer)

    // Then
    assertFalse(result.isCorrect)
    assertEquals(0.0, result.score)
    assertEquals("Incorrect. Correct answer: Option B", result.feedback)
  }

  @Test
  fun `evaluate subjective correct answer`() {
    // Given
    val quizId = 1L
    val quiz =
            Quiz(
                    id = quizId,
                    title = "Test Subjective",
                    questionType = "SUBJECTIVE",
                    book = mockBook
            )
    val subjAnswer =
            SubjectiveAnswerEntity(
                    possibleAnswers = listOf("Paris", "paris", "PARIS"),
                    caseSensitive = false,
                    quiz = quiz
            )
    val userAnswer =
            SubjectiveAnswerData(
                    possibleAnswers = listOf("Paris", "paris", "PARIS"),
                    caseSensitive = false,
                    answer = "Paris"
            )

    `when`(quizRepo.findById(quizId)).thenReturn(java.util.Optional.of(quiz))
    `when`(subjRepo.findByQuizId(quizId)).thenReturn(subjAnswer)
    `when`(registry.getStrategy<SubjectiveAnswerData>("SUBJECTIVE")).thenReturn(subjStrategy)
    `when`(subjStrategy.validate(any(), any())).thenReturn(true)
    `when`(subjStrategy.score(any(), any())).thenReturn(1.0)
    `when`(subjStrategy.feedback(any(), any(), any())).thenReturn("Correct!")

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
    val quiz = Quiz(id = quizId, title = "Test TF", questionType = "TRUE_FALSE", book = mockBook)
    val tfAnswer = TrueFalseAnswerEntity(correctAnswer = true, quiz = quiz)
    val userAnswer = TrueFalseAnswerData(correctAnswer = true, answer = true)

    `when`(quizRepo.findById(quizId)).thenReturn(java.util.Optional.of(quiz))
    `when`(tfRepo.findByQuizId(quizId)).thenReturn(tfAnswer)
    `when`(registry.getStrategy<TrueFalseAnswerData>("TRUE_FALSE")).thenReturn(tfStrategy)
    `when`(tfStrategy.validate(any(), any())).thenReturn(true)
    `when`(tfStrategy.score(any(), any())).thenReturn(1.0)
    `when`(tfStrategy.feedback(any(), any(), any())).thenReturn("Correct!")

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
    val quiz = Quiz(id = quizId, title = "Test Unknown", questionType = "UNKNOWN", book = mockBook)
    val userAnswer =
            MultipleChoiceAnswerData(
                    options = listOf("Option A", "Option B"),
                    correctIndex = 0,
                    selectedIndex = 0
            )

    `when`(quizRepo.findById(quizId)).thenReturn(java.util.Optional.of(quiz))

    // When & Then
    assertThrows<IllegalArgumentException> { quizService.evaluateQuizAnswer(quizId, userAnswer) }
  }

  @Test
  fun `throw exception for non-existent quiz`() {
    // Given
    val quizId = 999L
    val userAnswer =
            MultipleChoiceAnswerData(
                    options = listOf("Option A", "Option B"),
                    correctIndex = 0,
                    selectedIndex = 0
            )

    `when`(quizRepo.findById(quizId)).thenReturn(java.util.Optional.empty())

    // When & Then
    assertThrows<IllegalArgumentException> { quizService.evaluateQuizAnswer(quizId, userAnswer) }
  }

  @Test
  fun `get quizzes by book id`() {
    // Given
    val bookId = 1L
    val quizzes =
            listOf(
                    Quiz(
                            id = 1L,
                            title = "Quiz 1",
                            questionType = "MULTIPLE_CHOICE",
                            book = mockBook
                    ),
                    Quiz(id = 2L, title = "Quiz 2", questionType = "SUBJECTIVE", book = mockBook),
                    Quiz(id = 3L, title = "Quiz 3", questionType = "TRUE_FALSE", book = mockBook)
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
    verify(quizRepo).findByBookId(bookId)
  }
}
