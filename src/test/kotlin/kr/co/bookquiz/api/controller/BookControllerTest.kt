package kr.co.bookquiz.api.controller

import com.fasterxml.jackson.databind.ObjectMapper
import kr.co.bookquiz.api.api.exception.EntityNotFoundException
import kr.co.bookquiz.api.api.exception.ErrorCode
import kr.co.bookquiz.api.dto.book.AuthorResponse
import kr.co.bookquiz.api.dto.book.BookCreateRequest
import kr.co.bookquiz.api.dto.book.BookResponse
import kr.co.bookquiz.api.dto.book.BookUpdateRequest
import kr.co.bookquiz.api.dto.book.IllustratorResponse
import kr.co.bookquiz.api.dto.book.TranslatorResponse
import kr.co.bookquiz.api.security.JwtUtil
import kr.co.bookquiz.api.service.BookService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(BookController::class)
class BookControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var bookService: BookService

    @MockitoBean
    private lateinit var jwtUtil: JwtUtil

    private lateinit var testBookResponse: BookResponse
    private lateinit var testBookCreateRequest: BookCreateRequest
    private lateinit var testBookUpdateRequest: BookUpdateRequest
    private lateinit var testAuthorResponse: AuthorResponse
    private lateinit var testTranslatorResponse: TranslatorResponse
    private lateinit var testIllustratorResponse: IllustratorResponse

    @BeforeEach
    fun setUp() {
        testAuthorResponse = AuthorResponse(id = 1L, name = "Test Author")
        testTranslatorResponse = TranslatorResponse(id = 2L, name = "Test Translator")
        testIllustratorResponse = IllustratorResponse(id = 3L, name = "Test Illustrator")

        testBookResponse = BookResponse(
            id = 1L,
            title = "Test Book",
            isbn = "978-0123456789",
            publisher = "Test Publisher",
            quizPrice = 1000,
            thumbnail = "https://example.com/thumbnail.jpg",
            authors = listOf(testAuthorResponse),
            translators = listOf(testTranslatorResponse),
            illustrators = listOf(testIllustratorResponse)
        )

        testBookCreateRequest = BookCreateRequest(
            title = "Test Book",
            isbn = "978-0123456789",
            publisher = "Test Publisher",
            quizPrice = 1000,
            thumbnail = "https://example.com/thumbnail.jpg",
            authorIds = listOf(1L),
            translatorIds = listOf(2L),
            illustratorIds = listOf(3L)
        )

        testBookUpdateRequest = BookUpdateRequest(
            title = "Updated Test Book",
            isbn = "978-0123456789",
            publisher = "Test Publisher",
            quizPrice = 1000,
            thumbnail = "https://example.com/thumbnail.jpg",
            authorIds = listOf(1L),
            translatorIds = listOf(2L),
            illustratorIds = listOf(3L)
        )
    }

    @Test
    @WithMockUser
    fun `should create book successfully`() {
        // Given
        given(bookService.createBook(testBookCreateRequest))
            .willReturn(testBookResponse)

        // When & Then
        mockMvc.perform(
            post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testBookCreateRequest))
                .with(csrf())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Book created successfully"))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.title").value("Test Book"))
            .andExpect(jsonPath("$.data.isbn").value("978-0123456789"))
            .andExpect(jsonPath("$.data.publisher").value("Test Publisher"))
            .andExpect(jsonPath("$.data.quizPrice").value(1000))
            .andExpect(jsonPath("$.data.authors[0].id").value(1))
            .andExpect(jsonPath("$.data.authors[0].name").value("Test Author"))

        then(bookService).should().createBook(testBookCreateRequest)
    }

    @Test
    @WithMockUser
    fun `should get book by id successfully`() {
        // Given
        given(bookService.getBookById(1L)).willReturn(testBookResponse)

        // When & Then
        mockMvc.perform(get("/books/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Book retrieved successfully"))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.title").value("Test Book"))
            .andExpect(jsonPath("$.data.authors[0].id").value(1))
            .andExpect(jsonPath("$.data.authors[0].name").value("Test Author"))

        then(bookService).should().getBookById(1L)
    }

    @Test
    @WithMockUser
    fun `should return 404 when book not found`() {
        // Given
        given(bookService.getBookById(999L))
            .willThrow(EntityNotFoundException("Book", listOf("999"), ErrorCode.BOOK_NOT_FOUND))

        // When & Then
        mockMvc.perform(get("/books/999"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.title").value("Entity Not Found"))
            .andExpect(jsonPath("$.detail").value("Book not found: 999"))

        then(bookService).should().getBookById(999L)
    }

    @Test
    @WithMockUser
    fun `should update book successfully`() {
        // Given
        val updatedResponse = testBookResponse.copy(title = "Updated Test Book")

        given(bookService.updateBook(1L, testBookUpdateRequest))
            .willReturn(updatedResponse)

        // When & Then
        mockMvc.perform(
            put("/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testBookUpdateRequest))
                .with(csrf())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Book updated successfully"))
            .andExpect(jsonPath("$.data.title").value("Updated Test Book"))

        then(bookService).should().updateBook(1L, testBookUpdateRequest)
    }

    @Test
    @WithMockUser
    fun `should delete book successfully`() {
        // When & Then
        mockMvc.perform(delete("/books/1").with(csrf()))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Book deleted successfully"))

        then(bookService).should().deleteBook(1L)
    }

    @Test
    @WithMockUser
    fun `should handle validation errors during book creation`() {
        // Given
        val invalidBookRequest = testBookCreateRequest.copy(title = "") // Empty title

        // When & Then
        mockMvc.perform(
            post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidBookRequest))
                .with(csrf())
        )
            .andExpect(status().isBadRequest) // Validation error should return 400

        verifyNoInteractions(bookService)
    }
}