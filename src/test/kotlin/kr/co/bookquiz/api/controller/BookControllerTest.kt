package kr.co.bookquiz.api.controller

import com.fasterxml.jackson.databind.ObjectMapper
import kr.co.bookquiz.api.api.exception.EntityNotFoundException
import kr.co.bookquiz.api.api.exception.ErrorCode
import kr.co.bookquiz.api.dto.book.BookCreateRequest
import kr.co.bookquiz.api.dto.book.BookResponse
import kr.co.bookquiz.api.dto.book.BookUpdateRequest
import kr.co.bookquiz.api.security.JwtUtil
import kr.co.bookquiz.api.service.BookService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mockito.verifyNoInteractions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(BookController::class)
class BookControllerTest {

        @Autowired private lateinit var mockMvc: MockMvc

        @Autowired private lateinit var objectMapper: ObjectMapper

        @MockitoBean private lateinit var bookService: BookService

        @MockitoBean private lateinit var jwtUtil: JwtUtil

        private lateinit var testBookResponse: BookResponse
        private lateinit var testBookCreateRequest: BookCreateRequest
        private lateinit var testBookUpdateRequest: BookUpdateRequest
        private val testAuthors: String = "Test Author"
        private val testTranslators: String = "Test Translator"
        private val testIllustrators: String = "Test Illustrator"

        @BeforeEach
        fun setUp() {
                testBookResponse =
                        BookResponse(
                                id = 1L,
                                title = "Test Book",
                                isbn = "978-0123456789",
                                publisher = "Test Publisher",
                                quizPrice = 1000,
                                maxAttempt = 3,
                                thumbnail = "https://example.com/thumbnail.jpg",
                                authors = testAuthors,
                                translators = testTranslators,
                                illustrators = testIllustrators
                        )

                testBookCreateRequest =
                        BookCreateRequest(
                                title = "Test Book",
                                isbn = "978-0123456789",
                                publisher = "Test Publisher",
                                quizPrice = 1000,
                                thumbnail = "https://example.com/thumbnail.jpg",
                                authors = testAuthors,
                                translators = testTranslators,
                                illustrators = testIllustrators
                        )

                testBookUpdateRequest =
                        BookUpdateRequest(
                                title = "Updated Test Book",
                                isbn = "978-0123456789",
                                publisher = "Test Publisher",
                                quizPrice = 1000,
                                thumbnail = "https://example.com/thumbnail.jpg",
                                authors = testAuthors,
                                translators = testTranslators,
                                illustrators = testIllustrators
                        )
        }

        @Test
        @WithMockUser
        fun `should create book successfully`() {
                // Given
                given(bookService.createBook(testBookCreateRequest)).willReturn(testBookResponse)

                // When & Then
                mockMvc.perform(
                                post("/api/books")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                objectMapper.writeValueAsString(
                                                        testBookCreateRequest
                                                )
                                        )
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
                        .andExpect(jsonPath("$.data.authors").value("Test Author"))

                then(bookService).should().createBook(testBookCreateRequest)
        }

        @Test
        @WithMockUser
        fun `should create books in bulk successfully`() {
                // Given
                val bulkRequest =
                        listOf(
                                testBookCreateRequest,
                                testBookCreateRequest.copy(title = "Another Book")
                        )
                val bulkResponse =
                        listOf(
                                testBookResponse,
                                testBookResponse.copy(id = 2L, title = "Another Book")
                        )

                given(bookService.createBooksBulk(bulkRequest)).willReturn(bulkResponse)

                // When & Then
                mockMvc.perform(
                                post("/api/books/bulk")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(bulkRequest))
                                        .with(csrf())
                        )
                        .andExpect(status().isOk)
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.message").value("Books created successfully"))
                        .andExpect(jsonPath("$.data[0].id").value(1))
                        .andExpect(jsonPath("$.data[0].title").value("Test Book"))
                        .andExpect(jsonPath("$.data[1].id").value(2))
                        .andExpect(jsonPath("$.data[1].title").value("Another Book"))

                then(bookService).should().createBooksBulk(bulkRequest)
        }

        @Test
        @WithMockUser
        fun `should get book by id successfully`() {
                // Given
                given(bookService.getBookById(1L)).willReturn(testBookResponse)

                // When & Then
                mockMvc.perform(get("/api/books/1"))
                        .andExpect(status().isOk)
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.message").value("Book retrieved successfully"))
                        .andExpect(jsonPath("$.data.id").value(1))
                        .andExpect(jsonPath("$.data.title").value("Test Book"))
                        .andExpect(jsonPath("$.data.authors").value("Test Author"))

                then(bookService).should().getBookById(1L)
        }

        @Test
        @WithMockUser
        fun `should return 404 when book not found`() {
                // Given
                given(bookService.getBookById(999L))
                        .willThrow(
                                EntityNotFoundException(
                                        "Book",
                                        listOf("999"),
                                        ErrorCode.BOOK_NOT_FOUND
                                )
                        )

                // When & Then
                mockMvc.perform(get("/api/books/999"))
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

                given(bookService.updateBook(1L, testBookUpdateRequest)).willReturn(updatedResponse)

                // When & Then
                mockMvc.perform(
                                put("/api/books/1")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                objectMapper.writeValueAsString(
                                                        testBookUpdateRequest
                                                )
                                        )
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
                mockMvc.perform(delete("/api/books/1").with(csrf()))
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
                                post("/api/books")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                objectMapper.writeValueAsString(invalidBookRequest)
                                        )
                                        .with(csrf())
                        )
                        .andExpect(status().isBadRequest) // Validation error should return 400

                verifyNoInteractions(bookService)
        }

        @Test
        @WithMockUser
        fun `should return detailed validation errors when authors is blank`() {
                // Given
                val invalidBookRequest = testBookCreateRequest.copy(authors = "")

                // When & Then
                mockMvc.perform(
                                post("/api/books")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                objectMapper.writeValueAsString(invalidBookRequest)
                                        )
                                        .with(csrf())
                        )
                        .andExpect(status().isBadRequest)
                        .andExpect(jsonPath("$.detail").value("Validation failed"))
                        .andExpect(jsonPath("$.title").value("Validation Error"))
                        .andExpect(jsonPath("$.errorCode").value("E1004"))
                        .andExpect(jsonPath("$.fieldErrors.authors").isArray)

                verifyNoInteractions(bookService)
        }

        @Test
        @WithMockUser
        fun `should return detailed validation errors for multiple fields`() {
                // Given
                val invalidBookRequest =
                        testBookCreateRequest.copy(title = "", isbn = "", authors = "")

                // When & Then
                mockMvc.perform(
                                post("/api/books")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                objectMapper.writeValueAsString(invalidBookRequest)
                                        )
                                        .with(csrf())
                        )
                        .andExpect(status().isBadRequest)
                        .andExpect(jsonPath("$.detail").value("Validation failed"))
                        .andExpect(jsonPath("$.title").value("Validation Error"))
                        .andExpect(jsonPath("$.errorCode").value("E1004"))
                        .andExpect(jsonPath("$.fieldErrors.title").isArray)
                        .andExpect(jsonPath("$.fieldErrors.title[0]").value("Title is required"))
                        .andExpect(jsonPath("$.fieldErrors.isbn").isArray)
                        .andExpect(jsonPath("$.fieldErrors.isbn[0]").value("ISBN is required"))
                        .andExpect(jsonPath("$.fieldErrors.authors").isArray)

                verifyNoInteractions(bookService)
        }
}
